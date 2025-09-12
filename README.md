# Talents-board

**Talents-board** est une plateforme web qui met en relation des étudiants à la recherche d'opportunités professionnelles (stage, alternance, CDD, CDI…) et des entreprises à la recherche de talents.  
Les étudiants peuvent créer des **tickets de candidature**, spécifier leurs compétences, joindre leur CV, et postuler aux offres. Les entreprises peuvent consulter les candidatures et rechercher des profils selon des critères précis.

---

## 📂 Structure du projet

```

project-root
├── backend         # Backend Spring Boot (REST API)
│   ├── HELP.md
│   ├── mvnw
│   ├── mvnw\.cmd
│   ├── pom.xml
│   └── src
├── docker-compose.yml  # Conteneurs MySQL (et autres services)
└── frontend        # Frontend Angular
├── angular.json
├── node\_modules
├── package.json
├── package-lock.json
├── public
├── README.md
├── src
├── tsconfig.app.json
├── tsconfig.json
└── tsconfig.spec.json

````

---

## ⚙️ Technologies et dépendances

### Backend

- Java 17
- Spring Boot 3.3.x
- Maven
- Modules : Spring Web, Spring Data JPA, MySQL Driver, Spring Validation

### Frontend

- Angular 17+
- Bootstrap 5
- Axios pour la communication API
- Node.js ≥ 22.12
- npm pour la gestion des packages

### Base de données

- MySQL 8
- Conteneur Docker (`docker-compose.yml`)
- Database : `talentsboard`
- User : `root`
- Password : `root` (à modifier en production)

### Conteneurs / Docker

- MySQL via docker-compose
- Volumes pour persistance des données

---

## 📝 Fonctionnalités principales

### Pour les étudiants

- Créer un profil et gérer ses informations personnelles
- Soumettre un CV
- Créer des tickets de candidature (stage, alternance, CDI…)
- Ajouter des compétences à chaque candidature
- Faire des candidatures spontanées aux entreprises

### Pour les entreprises

- Consulter les tickets et candidatures
- Rechercher des candidats selon :
  - Type de contrat
  - Compétences
  - Niveau d’études
- Télécharger ou consulter les CV

---

## 🚀 Installation et lancement

### 1. Prérequis

- Java 17
- Maven
- Node.js ≥ 22.12
- npm
- Angular CLI
- Docker + docker-compose
- Git

### 2. Cloner le projet

```bash
git clone <REPO_URL>
cd Talents-board/project-root
````

### 3. Backend

1. Créer une base MySQL ou utiliser Docker :

```bash
docker-compose up -d
```

2. Lancer le backend :

```bash
cd backend
./mvnw spring-boot:run
```

API disponible par défaut : `http://localhost:8080/api/...`

### 4. Frontend

1. Installer les dépendances :

```bash
cd frontend
npm install
```

2. Lancer l’application Angular :

```bash
ng serve
```

Frontend disponible par défaut : `http://localhost:4200`

---

## ⚡ Bonnes pratiques pour le développement

* Chaque membre travaille sur une **branche dédiée** et ouvre des **pull requests** vers `main`
* Les modifications sont validées par le propriétaire avant fusion
* Ne pas exécuter le projet entier avec `sudo` → éviter les problèmes de permissions
* Commits clairs et descriptifs (ex : `feat(frontend): ajout formulaire ticket`)

---

## 📁 Structure des modules

* **frontend/src/app** : composants Angular, services, modules
* **frontend/src/assets** : fichiers statiques
* **backend/src/main/java/com/talentsboard** : controllers, services, repositories, entities
* **backend/src/main/resources** : fichiers de configuration Spring Boot (`application.properties`)

---

## 🔗 Liens utiles

* [Angular Documentation](https://angular.io/docs)
* [Spring Boot Documentation](https://spring.io/projects/spring-boot)
* [Axios Documentation](https://axios-http.com/docs/intro)
* [MySQL Documentation](https://dev.mysql.com/doc/)

---

## 👨‍💻 Contribution

1. Fork le projet
2. Crée ta branche (`git checkout -b feature/ma-fonctionnalité`)
3. Commits clairs et fréquents
4. Push vers ta branche (`git push origin feature/ma-fonctionnalité`)
5. Ouvre une Pull Request vers `main`

---

## ⚠️ Notes importantes

* Modifier les mots de passe MySQL en production
* Docker est optionnel pour MySQL mais recommandé
* Toujours lancer Angular / backend sans sudo

---

## 🛠️ Guide de travail pour les membres de l’équipe

Ce guide explique **comment récupérer le projet**, travailler sur une fonctionnalité, et envoyer votre travail pour validation avant qu’il soit disponible pour les autres membres.

---

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

---

### 2️⃣ Travailler sur votre fonctionnalité

* Effectuez vos modifications dans votre **branche personnelle**.
* Commits fréquents et clairs :

```bash
git add .
git commit -m "feat(frontend): ajout formulaire JobRequest"
```

* Si vous travaillez en binôme, vous pouvez partager la branche et pousser vos modifications sur GitHub.

---

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

---

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

---

### 5️⃣ Après validation

* Une fois la PR approuvée, elle sera **mergée dans `develop`**.
* Votre travail devient disponible pour les autres membres qui peuvent alors récupérer la mise à jour :

```bash
git checkout develop
git pull origin develop
```

* Ensuite, vous pouvez créer une nouvelle branche pour une autre fonctionnalité.

---

### 6️⃣ Bonnes pratiques

* **Ne jamais travailler directement sur `main` ou `develop`**.
* Commits clairs et fréquents.
* Tester votre code avant de pousser.
* Pour les fichiers sensibles (comme `.env` ou `application.properties`), ne jamais les pousser sur GitHub.
