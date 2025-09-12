# Talents-board

**Talents-board** est une plateforme web collaborative qui connecte des **étudiants en recherche d’opportunités professionnelles** (stage, alternance, CDD, CDI, etc.) avec des **entreprises en recherche de talents**.

* Les étudiants peuvent créer des **profils et candidatures**, ajouter leurs compétences et leur CV, et postuler ou soumettre des candidatures spontanées.
* Les entreprises peuvent publier des **opportunités** (tickets de recherche de talents), appliquer des filtres (compétences, type de contrat, niveau d’études, etc.) et consulter les profils pertinents.

---

## 📂 Structure du projet

```
project-root
├── backend/              # Backend Spring Boot (API REST + Firebase)
│   ├── pom.xml
│   └── src/
├── frontend/             # Frontend Angular (interface utilisateur)
│   ├── package.json
│   └── src/
├── docs/                 # Documentation projet
├── scripts/              # Scripts d'installation et de configuration
│   ├── setup_env_linux.sh
│   └── setup_env_windows.ps1
└── README.md             # Documentation principale
```



## ⚙️ Technologies et dépendances

### Backend

* **Java 17**
* **Spring Boot 3.3.x**
* **Maven**
* **Modules** : Spring Web, Spring Security, Firebase Admin SDK
* API REST exposée sur `http://localhost:8080/api/...`

### Frontend

* **Angular 17+**
* **Bootstrap 5** (UI responsive)
* **Axios** (communication API)
* **Firebase JS SDK** (authentification et base de données temps réel)
* **Node.js ≥ 22.12** + **npm**

### Base de données

* **Firebase** (Firestore + Authentication)
* Avantages :

  * Temps réel
  * Auth simplifiée (Google, email, etc.)
  * Pas besoin de gérer un serveur SQL



## 📝 Fonctionnalités principales

### Pour les étudiants

* Création d’un profil complet (infos, CV, compétences)
* Création et gestion de candidatures (stage, alternance, CDI, etc.)
* Soumission de candidatures spontanées aux entreprises
* Suivi de l’état de ses candidatures

### Pour les entreprises

* Création d’**opportunités** (tickets de recherche de candidats)
* Consultation et filtrage des candidatures reçues
* Recherche de profils par compétences, type de contrat, études
* Accès aux CV et informations clés des candidats



## 🚀 Installation et lancement

### 1. Prérequis

* Java 17
* Maven
* Node.js ≥ 22.12 + npm
* Angular CLI
* Git
* Compte Firebase (avec Firestore + Authentication activés)

### 2. Cloner le projet

```bash
git clone <REPO_URL>
cd Talents-board/project-root
```

### 3. Configuration Firebase

1. Créez un projet Firebase.
2. Activez **Firestore** et **Authentication**.
3. Téléchargez la clé d’admin (`serviceAccountKey.json`) pour le backend et placez-la dans :

```
backend/src/main/resources/firebase/
```

4. Configurez le frontend avec vos credentials Firebase (`frontend/src/environments/environment.ts`).



### 4. Backend

```bash
cd backend
./mvnw spring-boot:run
```

API disponible sur : `http://localhost:8080/api/...`



### 5. Frontend

```bash
cd frontend
npm install
ng serve
```

Application Angular disponible sur : `http://localhost:4200`



## ⚡ Bonnes pratiques de développement

* Chaque développeur travaille sur une **branche dédiée** → `feature/<initiales>-<fonctionnalité>`
* Les PR sont ouvertes vers `develop` et validées par le **propriétaire (owner)**
* **Ne pas pousser** de fichiers sensibles (`.env`, `serviceAccountKey.json`)
* Commit clair et structuré :

  * `feat(frontend): ajout formulaire candidature`
  * `fix(backend): correction API auth Firebase`



## 📁 Structure détaillée des modules

### Frontend (Angular)

```
frontend/src
├── app/
│   ├── core/            # Services globaux (auth, API, firebase.service.ts)
│   ├── shared/          # Composants et modules réutilisables (UI, pipes, directives)
│   ├── features/
│   │   ├── student/     # Espace étudiant (profil, candidatures, candidatures spontanées)
│   │   └── company/     # Espace entreprise (opportunités, recherche, gestion candidats)
│   ├── app-routing.module.ts
│   └── app.module.ts
├── assets/              # Images, CSS global
└── environments/        # Configurations (environment.ts, environment.prod.ts)
```

### Backend (Spring Boot)

```
backend/src/main/java/com/talentsboard
├── controller/          # REST controllers
├── service/             # Services métiers (Firebase, gestion étudiants/entreprises)
├── repository/          # Interfaces de persistance (Firebase wrappers)
├── model/               # Entités (Student, Company, Opportunity, Application)
└── config/              # Config Spring + Firebase
```

### Docs / Scripts

```
docs/                   # Documentation technique et fonctionnelle
scripts/                # Scripts d’installation (Linux, Windows)
```



## 🔗 Liens utiles

* [Angular](https://angular.io/docs)
* [Spring Boot](https://spring.io/projects/spring-boot)
* [Firebase](https://firebase.google.com/docs)
* [Bootstrap 5](https://getbootstrap.com/docs/5.0/getting-started/introduction/)



## 👨‍💻 Contribution

1. Fork le projet
2. Crée ta branche : `git checkout -b feature/<ma-fonctionnalité>`
3. Commit clair et fréquent
4. Push vers ta branche : `git push origin feature/<ma-fonctionnalité>`
5. Ouvre une **Pull Request** vers `develop`



## ⚠️ Notes importantes

* ⚡ **Firebase remplace MySQL** → plus besoin de Docker MySQL.
* 🔒 Ne jamais partager vos fichiers de credentials Firebase (`serviceAccountKey.json`).
* 🚀 Le backend et le frontend doivent être synchronisés sur la même config Firebase.

