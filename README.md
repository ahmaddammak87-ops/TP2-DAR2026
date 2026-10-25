# TP2-DAR2026
mon code correspond à deux approches différentes pour résoudre le problème :
# 1. Activité 1 : 
échanges avec des flux de caractères (BufferedReader et PrintWriter).

Principe de fonctionnement
Le client saisit une opération complète :

55 * 25

Il vérifie d'abord que la syntaxe est correcte, puis extrait les trois éléments :
Élément	Valeur
Premier opérande	55
Opérateur	*
Deuxième opérande	25


Le client envoie ensuite les éléments séparément, chacun sur une ligne.
pw.println(m.group(1)); // 55
pw.println(m.group(2)); // *
pw.println(m.group(3)); // 25

Le serveur lit les trois lignes :
String operande1 = br.readLine();
String operateur = br.readLine();
String operande2 = br.readLine();

Il effectue le calcul et renvoie le résultat :
pw.println(reponse);

Enfin, le client reçoit et affiche la réponse :
String reponse = br.readLine();
System.out.println("Résultat : " + reponse);

Résultat attendu :
Je suis un client pas encore connecté...
je suis un client connecté
Donnez une opération (ex : 55 * 25) ou quit : 55 * 25
Résultat : 55 * 25 = 1375

Pourquoi utiliser BufferedReader et PrintWriter ?
Ces classes permettent de communiquer avec des données textuelles, plutôt que de se limiter à l'envoi d'un seul octet.
- PrintWriter : envoie des données sous forme de texte.
- BufferedReader : lit le texte reçu.
- println() : envoie une ligne terminée par un séparateur de ligne.
- readLine() : lit une ligne jusqu'au séparateur.
# 2. Activité 2 :
échanges d’objets Java (ObjectInputStream, ObjectOutputStream et Serializable).
