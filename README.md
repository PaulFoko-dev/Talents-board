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

## 📌 Objectif pédagogique

* Projet fullstack pour étudiants
* Appliquer les bonnes pratiques Git et CI/CD
* Simuler une plateforme de gestion de candidatures et talents
* Travailler en collaboration backend/frontend
