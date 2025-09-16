# Talents-board

**Talents-board** est une plateforme web qui facilite la rencontre entre **jeunes talents** (étudiants, alternants, jeunes diplômés) et **entreprises** en recherche de compétences.  

🔹 Les **étudiants** peuvent créer des **tickets de candidature** (stage, alternance, CDI, CDD…), renseigner leurs compétences, uploader leur CV, et soumettre des candidatures spontanées.  
🔹 Les **entreprises** créent des **tickets de recherche de talents** pour définir un besoin précis (ex : développeur Angular pour un stage de 6 mois) et accèdent à une liste de candidats correspondant à leurs critères.  



## 📂 Structure du projet

```

project-root
├── backend                      # API REST (Spring Boot + Firebase)
│   ├── src
│   │   ├── main/java/com/talentsboard/backend
│   │   │   ├── config          # Configuration (Firebase, Security, etc.)
│   │   │   ├── controller      # API Controllers (endpoints REST)
│   │   │   ├── service         # Logique métier (auth, tickets, candidatures…)
│   │   │   ├── model           # Modèles (Entities, DTOs, Enums)
│   │   │   ├── repository      # Accès Firebase (Firestore/Storage)
│   │   │   └── util            # Utils (ex: extraction de compétences PDF)
│   │   └── main/resources
│   │       ├── application.properties   # Config Spring Boot
│   │       └── firebase-service-account.json (IGNORÉ dans git)
│   └── pom.xml
│
├── frontend                     # Application Angular
│   ├── src
│   │   ├── app
│   │   │   ├── core            # Services globaux (auth, interceptors, guards…)
│   │   │   ├── shared          # Composants/directives/pipes réutilisables
│   │   │   ├── features        # Modules fonctionnels (auth, students, companies, tickets, search…)
│   │   │   ├── models          # Interfaces / types partagés (User, Ticket, Application…)
│   │   │   └── app.module.ts   # Module principal
│   │   ├── assets
│   │   │   ├── images          # Logos, images
│   │   │   └── styles          # Styles globaux (Bootstrap overrides, SCSS…)
│   │   ├── environments
│   │   │   ├── environment.ts        # Config locale (IGNORÉ dans git)
│   │   │   ├── environment.prod.ts   # Config production (IGNORÉ dans git)
│   │   │   └── environment.example.ts # Modèle versionné (sans secrets)
│   │   ├── index.html
│   │   ├── main.ts
│   │   └── styles.scss
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
├── .gitignore                   # Ignorer node_modules, build, firebase keys, env files
└── README.md                    # Présentation du projet

```



## ⚙️ Technologies et dépendances

### Backend

- **Java 17**
- **Spring Boot 3.3.x**
- **Maven**
- **Firebase Admin SDK**
- Modules : Spring Web, Spring Validation

### Frontend

- **Angular 17+**
- **Bootstrap 5** (UI)
- **Axios** pour la communication API
- **Firebase Web SDK** (auth, firestore, storage)
- **Node.js ≥ 22.12**
- **npm**

### Base de données

- **Firebase Firestore** (NoSQL, hébergé, temps réel)
- **Firebase Authentication** (authentification sécurisée)
- **Firebase Storage** (gestion des CV et fichiers)



## 📝 Fonctionnalités principales

### Étudiants

- Créer un **ticket de candidature** (stage, alternance, CDI, etc.)
- Déposer un **CV** et renseigner ses compétences
- Rechercher des entreprises et postuler via **candidature spontanée**
- Suivre ses candidatures

### Entreprises

- Créer des **tickets de recherche de talents** pour un poste précis
- Définir des filtres (type de contrat, compétences, localisation…)
- Parcourir les **profils candidats correspondants**
- Consulter et télécharger les CV



## 🚀 Installation et lancement

### 1. Prérequis

- Java 17
- Maven
- Node.js ≥ 22.12
- npm
- Angular CLI
- Git
- Compte Firebase + fichier de configuration (`firebase-config.json` pour le backend et `environment.ts` pour le frontend)

### 2. Cloner le projet

```bash
git clone <REPO_URL>
cd Talents-board/project-root
```

### 3. Backend

1. Configurer Firebase :

   * Placer le fichier **`firebase-config.json`** (clé admin) dans `backend/src/main/resources/`.

2. Lancer le backend :

```bash
cd backend
./mvnw spring-boot:run
```

API disponible par défaut : `http://localhost:8080/api/...`

### 4. Frontend

1. Configurer Firebase dans **`frontend/src/environments/environment.ts`** :

```ts
export const environment = {
  production: false,
  firebase: {
    apiKey: "<API_KEY>",
    authDomain: "<PROJECT_ID>.firebaseapp.com",
    projectId: "<PROJECT_ID>",
    storageBucket: "<PROJECT_ID>.appspot.com",
    messagingSenderId: "<SENDER_ID>",
    appId: "<APP_ID>"
  },
  apiUrl: "http://localhost:8080/api"
};
```

2. Installer les dépendances et lancer Angular :

```bash
cd frontend
npm install
ng serve
```

Frontend disponible par défaut : `http://localhost:4200`



## ⚡ Bonnes pratiques pour le développement

* Chaque membre travaille sur une **branche dédiée** et ouvre une **pull request** vers `develop`
* `main` = stable, `develop` = intégration des nouvelles features
* **Commits clairs et fréquents** (`feat(frontend): ajout module ticket`)
* **Ne jamais commiter les clés Firebase** (les mettre dans `.gitignore`)
* Tests unitaires obligatoires avant PR


## 🛠️ Guide de travail pour les membres de l’équipe

Ce guide explique **comment récupérer le projet**, travailler sur une fonctionnalité, et envoyer votre travail pour validation avant qu’il soit disponible pour les autres membres.



### 1️⃣ Récupérer le projet

1. **Cloner le dépôt GitHub sur votre machine locale** :

```bash
git clone <URL_DU_DEPOT>
cd Talents-board/project-root
```

2. **Vérifier les branches disponibles** :

```bash
git branch -a
```

* La branche `main` contient la version stable.
* La branche `develop` contient la version intégrée des nouvelles fonctionnalités.

3. **Créer votre branche de travail depuis `develop`** :

```bash
git checkout develop
git pull origin develop
git checkout -b feature/<initiales>-<nom-fonctionnalité>
```

Exemple : `feature/pa-add-jobrequest` pour Pierre ajoutant la création d’un JobRequest.



### 2️⃣ Travailler sur votre fonctionnalité

* Effectuez vos modifications dans votre **branche personnelle**.
* Commits fréquents et clairs :

```bash
git add .
git commit -m "feat(frontend): ajout formulaire JobRequest"
```

* Si vous travaillez en binôme, vous pouvez partager la branche et pousser vos modifications sur GitHub.



### 3️⃣ Mettre à jour votre branche avec les changements du reste de l’équipe

Avant d’envoyer votre travail pour validation :

```bash
git checkout develop
git pull origin develop
git checkout feature/<votre-branche>
git merge develop
```

* Résoudre tout conflit éventuel.
* Tester que tout fonctionne correctement après la fusion.



### 4️⃣ Envoyer votre travail pour validation (Pull Request)

1. **Pousser votre branche sur GitHub** :

```bash
git push origin feature/<votre-branche>
```

2. **Créer une Pull Request (PR) depuis GitHub** :

* Base : `develop`
* Branche source : votre `feature/<votre-branche>`

3. **Notifier le propriétaire (owner)** pour la vérification.

* Le propriétaire fera la revue du code, vérifiera que tout fonctionne et validera la PR.



### 5️⃣ Après validation

* Une fois la PR approuvée, elle sera **mergée dans `develop`**.
* Votre travail devient disponible pour les autres membres qui peuvent alors récupérer la mise à jour :

```bash
git checkout develop
git pull origin develop
```

* Ensuite, vous pouvez créer une nouvelle branche pour une autre fonctionnalité.


## 📁 Modules fonctionnels prévus

### Frontend

* `auth` : login / register étudiant et entreprise
* `tickets` : gestion des tickets (création, recherche, filtrage)
* `profile` : gestion du profil étudiant et entreprise
* `dashboard` : vue entreprise (liste candidats) et vue étudiant (suivi candidatures)

### Backend

* `controller` : REST endpoints (tickets, users, candidatures)
* `service` : logique métier (filtrage, correspondance candidats-entreprises)
* `repository` : accès Firebase (Firestore, Auth, Storage)
* `model` : entités/DTO (User, Ticket, Candidature)



## 👨‍💻 Contribution

1. Fork le projet
2. Crée ta branche (`git checkout -b feature/ma-fonctionnalité`)
3. Commits clairs et fréquents
4. Push vers ta branche (`git push origin feature/ma-fonctionnalité`)
5. Ouvre une Pull Request vers `develop`



## ⚠️ Notes importantes

* ⚡ Remplacer MySQL → Firebase (Firestore, Auth, Storage)
* 🔐 Ne jamais exposer les fichiers de config Firebase (`firebase-config.json`)
* 🚀 Docker n’est plus nécessaire (Firebase est hébergé)
* 👨‍💻 Toujours tester son code avant de pousser sur GitHub
