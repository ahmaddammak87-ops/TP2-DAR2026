package serverPackage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Je suis un serveur en attente la connexion d'un client ");
            Socket socket = serverSocket.accept();
            System.out.println("un client est connecté");

            // Flux de communication (octets) puis flux de traitement (texte)
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os, true); // true = envoi automatique à chaque println

            // a) Le client envoie 3 lignes : opérande 1, opérateur, opérande 2
            String operande1 = br.readLine();
            while (operande1 != null && !operande1.equals("quit")) {
                String operateur = br.readLine();
                String operande2 = br.readLine();
                System.out.println("Opération reçue : " + operande1 + " " + operateur + " " + operande2);

                // b) Le serveur calcule
                String reponse;
                try {
                    reponse = calculer(Double.parseDouble(operande1), operateur,
                            Double.parseDouble(operande2));
                } catch (NumberFormatException e) {
                    reponse = "Erreur : opérande invalide";
                }

                // c) Le serveur envoie le résultat
                pw.println(reponse);
                System.out.println("Réponse envoyée : " + reponse);

                operande1 = br.readLine(); // opération suivante, ou "quit"
            }

            System.out.println("Fin de la communication");
            socket.close();
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Erreur côté serveur : " + e.getMessage());
        }
    }

    // Effectue l'opération et renvoie le texte à afficher chez le client
    static String calculer(double a, String operateur, double b) {
        double r;
        switch (operateur) {
            case "+": r = a + b; break;
            case "-": r = a - b; break;
            case "*": r = a * b; break;
            case "/":
                if (b == 0) {
                    return "Erreur : division par zéro impossible";
                }
                r = a / b;
                break;
            default:
                return "Erreur : opérateur inconnu";
        }
        return formater(a) + " " + operateur + " " + formater(b) + " = " + formater(r);
    }

    // 1375.0 s'affiche 1375 ; 3.5 reste 3.5
    static String formater(double v) {
        if (v == Math.rint(v) && !Double.isInfinite(v)) {
            return String.valueOf((long) v);
        }
        return String.valueOf(v);
    }
}
