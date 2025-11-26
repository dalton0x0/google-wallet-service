# Google Wallet Student Card Service

Service Spring Boot pour la génération et la gestion de cartes étudiantes numériques via l'API Google Wallet.

## Table des matières

- [Aperçu](#aperçu)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Utilisation](#utilisation)
- [API Endpoints](#api-endpoints)
- [Modèle de données](#modèle-de-données)
- [Déploiement](#déploiement)
- [Troubleshooting](#troubleshooting)
- [Contribution](#contribution)
- [License](#license)

## Aperçu

Ce projet permet de créer, gérer et distribuer des cartes étudiantes numériques via Google Wallet. Les étudiants peuvent ajouter leur carte d'étudiant directement dans leur portefeuille Google et l'utiliser comme preuve d'identité étudiante.

### Captures d'écran
```
┌─────────────────────────────────┐
│  CARTE D'ÉTUDIANT               │
│  ──────────────────────         │
│  jean.dupont@example.com        │
│  Jean Dupont                    │
│                                 │
│  NIVEAU: Master 1               │
│  ANNEE: 2025 - 2026             │
│                                 │
│  FORMATION : Base de données    │
│  N° ÉTUDIANT: STD-A1B2C3        │
│                                 │
│  [QR CODE]                      │
│                                 │
│  Tech School - ITIC Paris       │
└─────────────────────────────────┘
```

## Fonctionnalités

### Gestion des étudiants
-  Création et mise à jour des profils étudiants
-  Stockage sécurisé dans la base de données H2
-  Génération automatique d'ID étudiant unique
-  Photo de profil (URL personnalisée ou avatar par défaut)
-  Validation des données (email, champs obligatoires)

### Gestion des cartes
-  Génération de cartes Google Wallet
-  Création de carte par ID étudiant
-  Mise à jour de cartes existantes
-  Historique complet des cartes générées
-  Gestion des statuts (ACTIVE, EXPIRED, REVOKED)
-  Date d'expiration personnalisable
-  QR Code intégré
-  Design personnalisé aux couleurs de l'école

### Fonctionnalités avancées
-  Désactivation automatique des anciennes cartes
-  Expiration automatique des cartes périmées
-  Révocation manuelle de cartes
-  Consultation de l'historique des cartes par étudiant
-  Relation bidirectionnelle Student ↔ StudentCard

## Architecture
```
┌─────────────────────────────────────────────────────────┐
│                    Client Layer                         │
│              (Postman, Frontend, Mobile)                │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                 Controller Layer                        │
│          StudentController | StudentCardController      │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  Service Layer                          │
│       StudentService | StudentCardService               │
│              GoogleWalletService                        │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                Repository Layer                         │
│    StudentRepository | StudentCardRepository            │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│                  Database (H2)                          │
│           Student Table | StudentCard Table             │
└─────────────────────────────────────────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────────────────────┐
│              Google Wallet API                          │
│        (GenericClass | GenericObject)                   │
└─────────────────────────────────────────────────────────┘
```

## Technologies utilisées

### Backend
- **Java 21** - Langage de programmation
- **Spring Boot 3.5.x** - Framework principal
- **Spring Data JPA** - Persistance des données
- **H2 Database** - Base de données en mémoire
- **Lombok** - Réduction du boilerplate code
- **Validation API** - Validation des données
- **JWT token** - Signature des tokens

### Google Wallet
- **Google Wallet API** - API de génération de cartes
- **Google Auth Library** - Authentification OAuth2
- **JWT (auth0)** - Génération de tokens JWT

### Build & Configuration
- **Maven** - Gestionnaire de dépendances
- **Spring Dotenv** - Gestion des variables d'environnement

### Documentation & Logs
- **SLF4J/Logback** - Système de logs
- **Spring Boot Actuator** - Monitoring

## Prérequis

### Environnement de développement
- **JDK 21** ou supérieur
- **Maven 3.6+**
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code
- **Git**

### Compte Google Cloud
1. Compte Google Cloud Platform actif
2. Projet GCP créé
3. API Google Wallet activée
4. Service Account créé avec les permissions nécessaires
5. Fichier de clés JSON téléchargé
6. Issuer ID obtenu

### Obtenir les credentials Google Wallet

#### Étape 1 : Créer un projet Google Cloud
```bash
1. Aller sur https://console.cloud.google.com
2. Créer un nouveau projet ou sélectionner un projet existant
3. Noter le Project ID
```

#### Étape 2 : Activer l'API Google Wallet
```bash
1. Dans le menu, aller dans "APIs & Services" > "Library"
2. Rechercher "Google Wallet API"
3. Cliquer sur "Enable"
```

#### Étape 3 : Créer un Service Account
```bash
1. Aller dans "APIs & Services" > "Credentials"
2. Cliquer sur "Create Credentials" > "Service Account"
3. Nommer le compte (ex: wallet-service)
4. Accorder le rôle "Service Account Token Creator"
5. Cliquer sur "Done"
```

#### Étape 4 : Générer la clé JSON
```bash
1. Cliquer sur le Service Account créé
2. Aller dans l'onglet "Keys"
3. Cliquer sur "Add Key" > "Create new key"
4. Sélectionner "JSON"
5. Télécharger le fichier (ex: key.json)
```

#### Étape 5 : Obtenir l'Issuer ID
```bash
1. Aller sur https://pay.google.com/business/console
2. Aller dans "Google Wallet API"
3. Créer ou sélectionner un Issuer
4. Copier l'Issuer ID (format: VOTRE_ISSUER_ID)
```

## Installation

### 1. Cloner le repository
```bash
git clone https://github.com/dalton0x0/wallet-service.git
cd wallet-service
```

### 2. Configurer les variables d'environnement
```bash
# Copier le fichier d'exemple
cp .env.example .env

# Éditer le fichier .env avec vos valeurs
nano .env  # ou votre éditeur préféré
```

### 3. Ajouter le fichier de clés Google
```bash
# Créer le dossier credentials s'il n'existe pas
mkdir -p src/main/resources/credentials

# Copier votre fichier de clés
cp /chemin/vers/votre/key.json src/main/resources/credentials/key.json
```

### 4. Installer les dépendances
```bash
mvn clean install
```

### 5. Lancer l'application
```bash
mvn spring-boot:run
```

L'application sera accessible sur `http://localhost:8080`

## Configuration

### Structure des fichiers de configuration
```
src/main/resources/
├── application.properties      # Configuration principale
├── credentials/
│   ├── .gitkeep
│   └── key.json                # Clé Google
```

### Variables d'environnement (.env)

#### Variables obligatoires
```properties
# Google Wallet - OBLIGATOIRE
ISSUER_ID=VOTRE_ISSUER_ID
KEY_FILE_NAME=key
```

#### Variables optionnelles (avec valeurs par défaut)
```properties
# Application
APP_NAME=wallet-service

# Database
H2_JDBC_URL=jdbc:h2:mem:student_db
DRIVER_CLASS_NAME=org.h2.Driver
DB_USERNAME=sa
DB_PASSWORD=

# JPA
DB_DIALECT=org.hibernate.dialect.H2Dialect
DDL_AUTO=create-drop
JPA_SHOW_SQL=true

# H2 Console
H2_CONSOLE_ENABLED=true

# Google Wallet
GOOGLE_WALLET_APPLICATION_NAME=TechSchool Student Card

# Server
SERVER_PORT=8080

# Log Configuration
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_APP=DEBUG
LOGGING_LEVEL_HIBERNATE=WARN
LOG_FILE_NAME=google-wallet-service
```

### Console H2

La console H2 pour le développement est accessible à : `http://localhost:8080/h2-console`

**Configuration de connexion :**
- JDBC URL: `jdbc:h2:mem:student_db`
- Username: `sa`
- Password: *(laisser vide)*

## Utilisation

### Workflow complet

#### 1. Initialiser la classe Google Wallet (une seule fois)
```bash
POST http://localhost:8080/api/v1/student-cards/init-class
```

**Réponse :**
```json
{
  "success": "true",
  "classId": "VOTRE_ISSUER_ID.student_card_class",
  "message": "Classe créée avec succès"
}
```

#### 2. Créer un étudiant
```bash
POST http://localhost:8080/api/v1/students
Content-Type: application/json

{
  "firstName": "Jean",
  "lastName": "Dupont",
  "email": "jean.dupont@example.com",
  "level": "Master 1",
  "formation": "Informatique",
  "profilePictureUrl": "https://example.com/photo.jpg"
}
```

**Réponse :**
```json
{
  "fullName": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "level": "Master 1",
  "formation": "Informatique",
  "profilePictureUrl": "https://example.com/photo.jpg"
}
```

**Note :** Le student ID étudiant est généré automatiquement (ex: STD-A1B2C3)

#### 3. Générer la carte pour l'étudiant
```bash
POST http://localhost:8080/api/v1/student-cards/generate-by-id
Content-Type: application/json

{
  "studentId": 1,
  "expirationDate": "2025-08-31"
}
```

**Réponse :**
```json
{
  "objectId": "VOTRE_ISSUER_ID.std_a1b2c3",
  "saveUrl": "https://pay.google.com/gp/v/save/eyJhbGc...",
  "message": "Carte étudiante générée avec succès",
  "success": true
}
```

#### 4. L'étudiant ajoute la carte à son Google Wallet

L'étudiant clique sur le lien `saveUrl` ou scanne un QR code généré à partir de ce lien.

### Exemples de requêtes

#### Obtenir tous les étudiants
```bash
GET http://localhost:8080/api/v1/students
```

#### Obtenir un étudiant avec ses cartes
```bash
GET http://localhost:8080/api/v1/students/1/with-cards
```

**Réponse :**
```json
{
  "id": 1,
  "studentId": "STD-A1B2C3",
  "fullName": "Jean Dupont",
  "email": "jean.dupont@example.com",
  "level": "Master 1",
  "formation": "Informatique",
  "profilePictureUrl": "https://example.com/photo.jpg",
  "cards": [
    {
      "id": 1,
      "walletObjectId": "VOTRE_ISSUER_ID.std_a1b2c3",
      "saveUrl": "https://pay.google.com/gp/v/save/...",
      "expirationDate": "2025-08-31",
      "status": "ACTIVE",
      "level": "Master 1",
      "formation": "Informatique",
      "academicYear": "2024 - 2025",
      "isExpired": false,
      "isActive": true,
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "activeCard": {
    "id": 1,
    "walletObjectId": "VOTRE_ISSUER_ID.std_a1b2c3",
    "status": "ACTIVE",
    "..." : "..."
  }
}
```

#### Mettre à jour une carte existante
```bash
PUT http://localhost:8080/api/v1/student-cards/update-by-id
Content-Type: application/json

{
  "studentId": 1,
  "expirationDate": "2026-08-31"
}
```

#### Révoquer une carte
```bash
PUT http://localhost:8080/api/v1/student-cards/1/revoke
```

#### Obtenir la carte active d'un étudiant
```bash
GET http://localhost:8080/api/v1/student-cards/student/1/active
```

## API Endpoints

### Students API

| Méthode | Endpoint                           | Description                         | Body              |
|---------|------------------------------------|-------------------------------------|-------------------|
| GET     | `/api/v1/students`                 | Liste tous les étudiants            | -                 |
| GET     | `/api/v1/students/{id}`            | Obtenir un étudiant                 | -                 |
| GET     | `/api/v1/students/{id}/with-cards` | Obtenir un étudiant avec ses cartes | -                 |
| POST    | `/api/v1/students`                 | Créer un étudiant                   | StudentRequestDto |
| PUT     | `/api/v1/students/{id}`            | Mettre à jour un étudiant           | StudentRequestDto |

### Student Cards API

| Méthode | Endpoint                                           | Description                             | Body                       |
|---------|----------------------------------------------------|-----------------------------------------|----------------------------|
| POST    | `/api/v1/student-cards/init-class`                 | Initialiser la classe Google Wallet     | -                          |
| POST    | `/api/v1/student-cards`                            | Créer une carte (mode direct)           | StudentCardRequestDto      |
| POST    | `/api/v1/student-cards/generate-by-id`             | Générer une carte par ID étudiant       | GenerateCardByIdRequestDto |
| PUT     | `/api/v1/student-cards/update-by-id`               | Mettre à jour une carte                 | GenerateCardByIdRequestDto |
| GET     | `/api/v1/student-cards/student/{studentId}`        | Obtenir toutes les cartes d'un étudiant | -                          |
| GET     | `/api/v1/student-cards/student/{studentId}/active` | Obtenir la carte active                 | -                          |
| GET     | `/api/v1/student-cards/{cardId}`                   | Obtenir une carte spécifique            | -                          |
| PUT     | `/api/v1/student-cards/{cardId}/revoke`            | Révoquer une carte                      | -                          |
| POST    | `/api/v1/student-cards/expire-old-cards`           | Expirer les cartes périmées             | -                          |

### DTOs

#### StudentRequestDto
```json
{
  "firstName": "string (required)",
  "lastName": "string (required)",
  "email": "string (required, valid email)",
  "level": "string (required)",
  "formation": "string (required)",
  "profilePictureUrl": "string (optional)"
}
```

#### StudentCardRequestDto
```json
{
  "studentId": "string (required)",
  "firstName": "string (required)",
  "lastName": "string (required)",
  "email": "string (required, valid email)",
  "formation": "string (required)",
  "level": "string (required)",
  "photoUrl": "string (optional)",
  "expirationDate": "date (required, format: YYYY-MM-DD)"
}
```

#### GenerateCardByIdRequestDto
```json
{
  "studentId": "integer (required)",
  "expirationDate": "date (optional, format: YYYY-MM-DD)"
}
```

## Modèle de données

### Diagramme de relation
```
┌─────────────────┐         1:N         ┌─────────────────┐
│     Student     │◄────────────────────┤  StudentCard    │
├─────────────────┤                     ├─────────────────┤
│ id (PK)         │                     │ id (PK)         │
│ studentId       │                     │ walletObjectId  │
│ firstName       │                     │ saveUrl         │
│ lastName        │                     │ expirationDate  │
│ email           │                     │ status          │
│ level           │                     │ student_id (FK) │
│ formation       │                     │ createdAt       │
│ ...             │                     │ ...             │
└─────────────────┘                     └─────────────────┘
```

## Déploiement

### Déploiement local
```bash
# Build du projet
mvn clean package

# Lancer le JAR
java -jar target/wallet-service-0.0.1-SNAPSHOT.jar
```

### Déploiement avec Docker

#### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY src/main/resources/credentials /app/credentials
ENTRYPOINT ["java","-jar","/app.jar"]
```

#### docker-compose.yml
```yaml
version: '3.8'

services:
  wallet-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      - APP_NAME=wallet-service
      - ISSUER_ID=${ISSUER_ID}
      - KEY_FILE_NAME=${KEY_FILE_NAME}
      - SERVER_PORT=8080
    volumes:
      - ./logs:/app/logs
    restart: unless-stopped
```

#### Commandes Docker
```bash
# Build l'image
docker build -t wallet-service .

# Lancer le conteneur
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter
docker-compose down
```

## Troubleshooting

### Problèmes courants

#### 1. Erreur: "Failed to initialize class"

**Cause:** Le fichier de clés Google est manquant ou mal configuré.

**Solution:**
```bash
# Vérifier que le fichier existe
ls -la src/main/resources/credentials/key.json

# Vérifier les permissions
chmod 644 src/main/resources/credentials/key.json

# Vérifier la variable d'environnement
echo $KEY_FILE_NAME
```

#### 2. Erreur: "Invalid Issuer ID"

**Cause:** L'Issuer ID est incorrect ou mal formaté.

**Solution:**
- Vérifier l'Issuer ID dans Google Pay Console
- S'assurer qu'il est au format numérique (ex: VOTRE_ISSUER_ID)
- Vérifier la variable d'environnement ISSUER_ID

#### 3. Erreur: "HTTP 409 - Class already exists"

**Cause:** La classe Google Wallet existe déjà.

**Solution:** C'est normal ! L'application met à jour automatiquement la classe existante.

#### 4. Erreur: "Email is not available"

**Cause:** Un étudiant avec cet email existe déjà.

**Solution:** Utiliser un email unique ou mettre à jour l'étudiant existant.

#### 5. La carte n'apparaît pas dans Google Wallet

**Vérifications:**
1. Le lien `saveUrl` est-il valide ?
2. L'utilisateur a-t-il un compte Google ?
3. L'utilisateur est-il dans un pays supporté ?
4. La date d'expiration n'est-elle pas dépassée ?

#### 6. Erreur de connexion H2

**Solution:**
```properties
# Dans application.properties, vérifier :
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:student_db
```

### Logs utiles
```bash
# Voir tous les logs
tail -f logs/google-wallet-service.log

# Filtrer les erreurs
grep ERROR logs/google-wallet-service.log

# Logs en temps réel avec filtre
tail -f logs/google-wallet-service.log | grep "StudentCard"
```

### Mode Debug

Pour activer le mode debug complet :
```properties
# Dans .env
LOGGING_LEVEL_ROOT=DEBUG
LOGGING_LEVEL_APP=DEBUG
LOGGING_LEVEL_HIBERNATE=DEBUG
JPA_SHOW_SQL=true
```

## Monitoring et Maintenance

### Health Check
```bash
# Vérifier que l'application est en ligne
curl http://localhost:8080/actuator/health
```

### Tâches planifiées recommandées

#### Expirer les cartes périmées (quotidien)
```bash
# Créer un cron job
0 0 * * * curl -X POST http://localhost:8080/api/v1/student-cards/expire-old-cards
```

#### Backup de la base de données (si migration vers PostgreSQL)
```bash
# Exemple avec PostgreSQL
0 2 * * * pg_dump -U postgres student_db > backup_$(date +\%Y\%m\%d).sql
```

### Métriques importantes

- Nombre total d'étudiants
- Nombre de cartes actives
- Nombre de cartes expirées
- Taux de génération de cartes réussi
- Temps de réponse moyen de l'API

## Tests

### Tests unitaires
```bash
mvn test
```

### Tests d'intégration
```bash
mvn verify
```

### Collection Postman

Importez le fichier `postman_collection.json` dans Postman pour tester tous les endpoints.

**Création de la collection:**

1. Créer une nouvelle collection "Wallet Service"
2. Ajouter les variables d'environnement:
    - `base_url`: http://localhost:8080
    - `student_id`: 1

3. Importer les requêtes depuis le dossier `postman/`

## Contribution

Les contributions sont les bienvenues ! Voici comment contribuer :

### 1. Fork le projet
```bash
git clone https://github.com/dalton0x0/wallet-service.git
cd wallet-service
```

### 2. Créer une branche
```bash
git checkout -b feature/nouvelle-fonctionnalite
```

### 3. Faire vos modifications
```bash
# Coder la fonctionnalité
# Ajouter des tests
# Mettre à jour la documentation
```

### 4. Commiter
```bash
git add .
git commit -m "feat: ajout de la nouvelle fonctionnalité"
```

### 5. Pousser et créer une Pull Request
```bash
git push origin feature/nouvelle-fonctionnalite
```

### Conventions de commit

Nous utilisons [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nouvelle fonctionnalité
- `fix:` Correction de bug
- `docs:` Documentation
- `style:` Formatage
- `refactor:` Refactoring
- `test:` Tests
- `chore:` Maintenance

## License

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

## Auteurs

- **Chéridanh TSIELA** - *Développeur principal* - [@dalton0x0](https://github.com/dalton0x0)

## Remerciements

- Google Wallet API Team
- Spring Boot Community
- Tous les contributeurs
- Malek CHAOUCHE (Tech School) [@chaouche-praetoria](https://github.com/chaouche-praetoria)
- Adnan RIHAN (Redvise) [@Max13](https://github.com/Max13)

## Support

Pour toute question ou support:

-  Email: contact@cheridanh.cg
-  Issues: [GitHub Issues](https://github.com/dalton0x0/wallet-service/issues)
-  Documentation: [Wiki](https://github.com/dalton0x0/wallet-service/wiki)

## Roadmap

### Version 1.1 (Q2 2026)
- [ ] Migration vers MySQL
- [ ] Authentification JWT
- [ ] API de gestion des administrateurs
- [ ] Dashboard web pour la gestion

### Version 1.2 (Q3 2026)
- [ ] Notifications push
- [ ] Génération de QR codes dynamiques
- [ ] Statistiques et analytics
- [ ] Export de données

### Version 2.0 (Q4 2026)
- [ ] Support multi-établissements
- [ ] API GraphQL
- [ ] Application mobile (React Native)
- [ ] Intégration avec Apple Wallet

## Ressources utiles

- [Documentation Google Wallet API](https://developers.google.com/wallet)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Google Cloud Console](https://console.cloud.google.com)
- [H2 Database Documentation](https://www.h2database.com)

---

**Si ce projet vous a été utile, n'hésitez pas à lui donner une étoile ⭐**

Made with ❤️ by [Chéridanh TSIELA]
