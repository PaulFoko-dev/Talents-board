# Talents-board

**Talents-board** est une plateforme web qui facilite la rencontre entre **jeunes talents** (étudiants, alternants, jeunes diplômés) et **entreprises** en recherche de compétences.  

🔹 Les **étudiants** peuvent créer des **tickets de candidature** (stage, alternance, CDI, CDD…), renseigner leurs compétences, uploader leur CV, et soumettre des candidatures spontanées.  
🔹 Les **entreprises** créent des **tickets de recherche de talents** pour définir un besoin précis (ex : développeur Angular pour un stage de 6 mois) et accèdent à une liste de candidats correspondant à leurs critères.  



## 📂 Structure du projet



project-root
├── backend              # API REST (Spring Boot + Firebase)
│   ├── src
│   │   ├── main/java/com/talentsboard/backend
│   │   │   ├── controller   # API Controllers (REST endpoints)
│   │   │   ├── service      # Logique métier
│   │   │   ├── model        # Modèles (Entities/DTO)
│   │   │   └── repository   # Accès Firebase
│   │   └── main/resources
│   │       └── application.properties
│   └── pom.xml
├── frontend             # Application Angular
│   ├── src/app
│   │   ├── modules       # Modules fonctionnels (étudiants, entreprises, auth…)
│   │   ├── components    # Composants Angular réutilisables
│   │   ├── services      # Services Angular (API, auth, firebase…)
│   │   ├── models        # Interfaces/Types partagés
│   │   └── assets        # Ressources statiques (images, CSS, logos…)
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
└── docs                 # Documentation technique et fonctionnelle





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
