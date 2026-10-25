# TP2-DAR2026
mon code correspond à deux approches différentes pour résoudre le problème :

# Client / Serveur Java : calculatrice à distance

Ce projet présente deux manières d'échanger des données entre un **client** et un **serveur** Java via des sockets, en réalisant une calculatrice à distance.

- **Activité 1** : échange de texte avec `BufferedReader` et `PrintWriter`
- **Activité 2** : échange d'objets Java avec `ObjectOutputStream` et `ObjectInputStream`

## Sommaire

1. [Activité 1 : flux textuels](#1-activité-1--flux-textuels)
2. [Activité 2 : échange d'objets Java](#2-activité-2--échange-dobjets-java)
3. [Comparaison des deux activités](#3-comparaison-des-deux-activités)
4. [Points d'attention](#4-points-dattention)

---

## 1. Activité 1 : flux textuels

### Principe de fonctionnement

Le client saisit une opération complète :

```
55 * 25
```

Il vérifie d'abord que la syntaxe est correcte (expression régulière), puis extrait les trois éléments :

| Élément            | Valeur |
|--------------------|--------|
| Premier opérande   | `55`   |
| Opérateur          | `*`    |
| Deuxième opérande  | `25`   |

### Étapes de l'échange

**1. Le client envoie les éléments séparément, chacun sur une ligne :**

```java
pw.println(m.group(1)); // 55
pw.println(m.group(2)); // *
pw.println(m.group(3)); // 25
```

**2. Le serveur lit les trois lignes :**

```java
String operande1 = br.readLine();
String operateur = br.readLine();
String operande2 = br.readLine();
```

**3. Le serveur effectue le calcul et renvoie le résultat :**

```java
pw.println(reponse);
```

**4. Le client reçoit et affiche la réponse :**

```java
String reponse = br.readLine();
System.out.println("Résultat : " + reponse);
```

### Résultat attendu

```
Je suis un client pas encore connecté...
je suis un client connecté
Donnez une opération (ex : 55 * 25) ou quit : 55 * 25
Résultat : 55 * 25 = 1375
```

### Pourquoi `BufferedReader` et `PrintWriter` ?

Ces classes permettent de communiquer avec des données **textuelles**, plutôt que de se limiter à l'envoi d'un seul octet.

| Élément          | Rôle                                              |
|------------------|---------------------------------------------------|
| `PrintWriter`    | Envoie des données sous forme de texte            |
| `BufferedReader` | Lit le texte reçu                                 |
| `println()`      | Envoie une ligne terminée par un séparateur de ligne |
| `readLine()`     | Lit une ligne jusqu'au séparateur                 |

> **Attention :** `println()` et `readLine()` doivent respecter le même protocole. Puisque le client envoie **trois lignes** par opération, le serveur doit lire **trois lignes** pour chaque opération.

---

## 2. Activité 2 : échange d'objets Java

Dans cette version, au lieu d'envoyer trois chaînes séparées, le client envoie **un objet Java** contenant toute l'opération.

### A. La classe `Operation`

C'est la classe principale de cette activité.

```java
import java.io.Serializable;

public class Operation implements Serializable {

    private static final long serialVersionUID = 1L;

    private double operande1;
    private char operateur;
    private double operande2;

    public Operation(double operande1, char operateur, double operande2) {
        this.operande1 = operande1;
        this.operateur = operateur;
        this.operande2 = operande2;
    }

    public double getOperande1() {
        return operande1;
    }

    public char getOperateur() {
        return operateur;
    }

    public double getOperande2() {
        return operande2;
    }
}
```

L'instruction suivante :

```java
Operation op = new Operation(55, '*', 25);
```

crée un objet contenant trois attributs :

```
op
 ├── operande1 = 55.0
 ├── operateur = '*'
 └── operande2 = 25.0
```

#### Pourquoi `implements Serializable` ?

`Serializable` est une interface Java qui permet à un objet d'être **sérialisé**. La sérialisation transforme l'état de l'objet en une représentation pouvant être transmise dans un flux d'octets.

Sans cette interface, l'envoi de l'objet `Operation` avec `writeObject()` provoque normalement une `NotSerializableException`.

#### Pourquoi `serialVersionUID` ?

```java
private static final long serialVersionUID = 1L;
```

Cet identifiant permet notamment de vérifier la **compatibilité des versions** de la classe lors de la désérialisation. Il est préférable que le client et le serveur utilisent la même définition compatible de la classe `Operation`.

### B. Le client envoie l'objet

**Création des flux :**

```java
ObjectOutputStream oos =
        new ObjectOutputStream(socket.getOutputStream());

oos.flush();
```

**Construction de l'objet à partir des trois éléments extraits de la saisie :**

```java
Operation op = new Operation(
        Double.parseDouble(m.group(1)),
        m.group(2).charAt(0),
        Double.parseDouble(m.group(3))
);
```

**Envoi :**

```java
oos.writeObject(op);
oos.flush();
```

- `writeObject(op)` : sérialise et envoie l'objet.
- `flush()` : force l'envoi des données en attente dans le flux.

### C. Le serveur reçoit l'objet

```java
ObjectInputStream ois =
        new ObjectInputStream(socket.getInputStream());
```

Le serveur crée un flux pour recevoir les objets, puis lit :

```java
Object recu = ois.readObject();
```

#### Pourquoi utiliser le type `Object` ?

`readObject()` renvoie un objet de type général `Object`. Il faut donc **vérifier son type** avant de le convertir :

```java
if (recu instanceof Operation) {
    Operation op = (Operation) recu;
}
```

Dans le programme, la condition se trouve dans la boucle :

```java
while (recu instanceof Operation)
```

Le serveur traite les objets `Operation` reçus tant que le client continue à en envoyer.

### D. Le serveur récupère les données et répond

```java
double a = op.getOperande1();
char operateur = op.getOperateur();
double b = op.getOperande2();
```

Pour l'objet envoyé précédemment, on obtient :

```
a = 55.0
operateur = '*'
b = 25.0
```

Le serveur effectue alors le calcul :

```java
String reponse = calculer(a, operateur, b);
```

puis renvoie le résultat :

```java
oos.writeObject(reponse);
oos.flush();
```

Le client le reçoit avec :

```java
String reponse = (String) ois.readObject();
System.out.println("Résultat : " + reponse);
```

### Résultat attendu

```
Résultat : 55 * 25 = 1375
```

---

## 3. Comparaison des deux activités

| Critère                 | Activité 1 : flux textuels      | Activité 2 : flux d'objets  |
|-------------------------|---------------------------------|-----------------------------|
| Envoi des données       | Trois lignes de texte           | Un objet `Operation`        |
| Flux de sortie          | `PrintWriter`                   | `ObjectOutputStream`        |
| Flux d'entrée           | `BufferedReader`                | `ObjectInputStream`         |
| Méthode d'envoi         | `println()`                     | `writeObject()`             |
| Méthode de réception    | `readLine()`                    | `readObject()`              |
| Structure des données   | Séparée en plusieurs lignes     | Regroupée dans un objet     |
| Sérialisation Java      | Non nécessaire                  | Nécessaire                  |
| Classe `Operation`      | Non nécessaire                  | Nécessaire                  |
| Validation de la saisie | Expression régulière            | Expression régulière        |

### La différence essentielle

**Activité 1** : le client envoie les données séparément :

```
55
*
25
```

**Activité 2** : le client envoie un objet qui contient les trois données :

```java
new Operation(55, '*', 25)
```

Les deux approches permettent d'effectuer le calcul à distance. La seconde permet de se familiariser avec la **sérialisation** et la **transmission d'objets Java**.

---

## 4. Points d'attention

### Point 1 : la commande `quit` et la commande `FIN`

**Activité 1** : le client envoie :

```java
pw.println("quit");
```

Le serveur vérifie :

```java
while (operande1 != null && !operande1.equals("quit"))
```

Le protocole est cohérent : le serveur sait qu'il doit arrêter de traiter les opérations.

**Activité 2** : le client envoie :

```java
oos.writeObject("FIN");
```

Le serveur sort de sa boucle lorsque l'objet reçu n'est plus une instance de `Operation`. Cela fonctionne, mais il est plus explicite de vérifier la commande de fin :

```java
Object recu = ois.readObject();

while (true) {
    if (recu instanceof String && recu.equals("FIN")) {
        break;
    }

    if (!(recu instanceof Operation)) {
        System.out.println("Objet reçu invalide");
        break;
    }

    Operation op = (Operation) recu;

    double a = op.getOperande1();
    char operateur = op.getOperateur();
    double b = op.getOperande2();

    String reponse = calculer(a, operateur, b);

    oos.writeObject(reponse);
    oos.flush();

    recu = ois.readObject();
}
```

Cette version vérifie explicitement que le serveur a reçu la commande de fin.

### Point 2 : l'ordre de création des flux d'objets

Il faut utiliser cet ordre **dans le client et dans le serveur** :

```java
ObjectOutputStream oos =
        new ObjectOutputStream(socket.getOutputStream());

oos.flush();

ObjectInputStream ois =
        new ObjectInputStream(socket.getInputStream());
```

**Pourquoi ?** Le constructeur de `ObjectOutputStream` écrit un en-tête dans le flux. Le constructeur de `ObjectInputStream` attend de recevoir cet en-tête.

Si les deux côtés créent d'abord leur `ObjectInputStream`, ils risquent de rester **bloqués** en attendant l'en-tête que l'autre côté n'a pas encore envoyé.
