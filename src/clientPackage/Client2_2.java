package clientPackage;

import commonPackage.Operation;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Client2_2 {

    static final Pattern SYNTAXE = Pattern.compile(
            "^\\s*(-?\\d+(?:\\.\\d+)?)\\s*([+\\-*/])\\s*(-?\\d+(?:\\.\\d+)?)\\s*$");

    public static void main(String[] args) {
        try {
            System.out.println("Je suis un client pas encore connecté...");
            Socket socket = new Socket("localhost", 1234);
            System.out.println("je suis un client connecté");

            // Même ordre que le serveur : ObjectOutputStream d'abord, puis ObjectInputStream
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.print("Donnez une opération (ex : 55 * 25) ou quit : ");
                String saisie = sc.nextLine().trim();
                if (saisie.equals("quit")) {
                    break;
                }

                Matcher m = SYNTAXE.matcher(saisie);
                if (!m.matches()) {
                    System.out.println("Syntaxe invalide, rien n'est envoyé.");
                    continue;
                }

                // b) Le client envoie un objet Operation
                Operation op = new Operation(
                        Double.parseDouble(m.group(1)),
                        m.group(2).charAt(0),
                        Double.parseDouble(m.group(3)));
                oos.writeObject(op);
                oos.flush();

                // g) Le client lit et affiche le résultat
                String reponse = (String) ois.readObject();
                System.out.println("Résultat : " + reponse);
            }

            oos.writeObject("FIN"); // prévenir le serveur
            oos.flush();
            System.out.println("Fin de la communication");
            sc.close();
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erreur côté client : " + e.getMessage());
        }
    }
}