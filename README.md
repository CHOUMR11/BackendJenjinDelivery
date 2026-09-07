# Jenjon Delivery — Backend MVP (complet)

Monolithe modulaire Spring Boot (Java 17), structuré pour une migration future vers des microservices.

## Modules
- `auth` — utilisateurs, rôles (ADMIN, SUPERVISEUR, CHEF_AGENCE, LIVREUR, EXPEDITEUR, EXPEDITEUR_PRO), JWT
- `colis` — enregistrement, code-barres, type (Courrier/Colis), mode de collecte (agence/pick-up), statuts, pagination
- `livraison` — bons de livraison, mode (Standard/Express/Same-day), affectation livreur, pagination
- `agence` — gestion des agences
- `reglement` — récapitulatif des montants dus aux livreurs et aux expéditeurs pro, fréquence configurable (quotidien/hebdomadaire/instantané)
- `notification` — **email uniquement pour le moment** (décision produit actuelle), réellement fonctionnel via SMTP. SMS/Push sont codés (interfaces `SmsProvider`/`PushProvider`) mais désactivés en attente d'un fournisseur.
- `document` — génération PDF du bordereau d'envoi / bon de livraison (code-barres réel via ZXing)
- `shared` — sécurité JWT, CORS configurable, gestion d'erreurs globale

## Lancer en local

1. Démarrer PostgreSQL :
   ```
   docker compose up -d
   ```

2. (Optionnel) Copier les variables d'environnement et les adapter :
   ```
   cp .env.example .env
   ```

3. Lancer l'application :
   ```
   mvn spring-boot:run
   ```
   API : `http://localhost:8080`
   Swagger UI : `http://localhost:8080/swagger-ui.html`

4. Lancer les tests :
   ```
   mvn test
   ```

## Variables d'environnement (production)

| Variable | Rôle | Défaut (dev) |
|---|---|---|
| `JWT_SECRET` | Secret de signature JWT (≥32 car.) | valeur factice — **à changer** |
| `JWT_EXPIRATION_MS` | Durée de validité du token | 86400000 (24h) |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | Connexion PostgreSQL | localhost / jenjon / jenjon |
| `CORS_ALLOWED_ORIGINS` | Origines autorisées (séparées par virgules) | localhost:4200, localhost:8081 |
| `NOTIF_EMAIL_ENABLED` + `MAIL_HOST/PORT/USERNAME/PASSWORD` | Envoi email réel | **activé** (canal actif) |
| `NOTIF_SMS_ENABLED` + `SMS_PROVIDER_API_KEY/URL` | Envoi SMS réel | désactivé — non utilisé pour le moment |
| `NOTIF_PUSH_ENABLED` + `FCM_CREDENTIALS_PATH` | Envoi push réel (Firebase) | désactivé — non utilisé pour le moment |

## Endpoints principaux

### Auth
- `POST /api/auth/register` — inscription libre, **réservée aux expéditeurs** (comme Intigo). Le compte est créé en statut `EN_ATTENTE_VALIDATION` et **ne peut pas se connecter** tant qu'il n'est pas validé.
- `POST /api/auth/interne` (ADMIN uniquement) — création d'un compte interne (Chef d'agence, Livreur, Superviseur, Admin), auto-validé. **C'est la seule façon de créer un Chef d'agence** : il n'y a pas d'auto-inscription pour les rôles internes, pour éviter que n'importe qui se déclare Admin. Un Chef d'agence doit obligatoirement être rattaché à une agence (`agenceId`).
- `POST /api/auth/login` — bloqué (403) tant que le compte n'est pas `VALIDE`.
- `GET /api/auth/internes?role=CHEF_AGENCE` (ADMIN, SUPERVISEUR) — liste les utilisateurs internes par rôle.
- `GET /api/auth/comptes-en-attente` (ADMIN, SUPERVISEUR) — liste paginée des inscriptions expéditeurs à valider.
- `PATCH /api/auth/{userId}/valider` (ADMIN, SUPERVISEUR) — active le compte.
- `PATCH /api/auth/{userId}/rejeter` (ADMIN, SUPERVISEUR) — rejette l'inscription.

### Agences
- CRUD complet sur `/api/agences`

### Colis
- `POST /api/colis`
- `GET /api/colis?page=0&size=20&sort=dateCreation,desc` (paginé)
- `GET /api/colis/{id}`
- `GET /api/colis/code-barres/{codeBarres}`
- `PATCH /api/colis/{id}/statut?statut=LIVRE&destinataireTelephone=...&canalPrefere=SMS`
- `PATCH /api/colis/{id}/livrer?montantEncaisse=97.000` — bouton vert "LIVRER" de l'app Livreur
- `PATCH /api/colis/{id}/retour?motif=INJOIGNABLE` — bouton rouge "Retour : choisir un motif" (motifs : `A_VERIFIER_AVEC_EXPEDITEUR`, `TROIS_TENTATIVES_ACCOMPLIES`, `PAS_DE_REPONSE`, `INJOIGNABLE`, `FERMEE`, `CLIENT_DISPONIBLE_DEMAIN`, `CLIENT_NON_DISPONIBLE_DATE`, `ADRESSE_INCORRECTE`, `ADRESSE_INCOMPLETE`, `PARCOURS_NON_TERMINE`)

### Bons de livraison
- `POST /api/livraisons`
- `GET /api/livraisons?page=0&size=20` (paginé)
- `GET /api/livraisons/{id}`
- `GET /api/livraisons/livreur/{livreurId}`
- `PATCH /api/livraisons/{id}/statut?statut=EN_TOURNEE`
- `GET /api/livraisons/{id}/bilan` — "Bilan du jour" (compteurs Total/En cours/Livrés/Retours, total encaissé, taux de réussite), comme l'écran runsheet de l'app Livreur

### Règlements (livreurs & expéditeurs pro)
- `POST /api/reglements`
- `GET /api/reglements/beneficiaire/{id}?type=LIVREUR` (paginé)
- `GET /api/reglements/statut/{statut}` (paginé)
- `PATCH /api/reglements/{id}/valider`
- `PATCH /api/reglements/{id}/payer`

### Documents
- `GET /api/documents/bordereau/{bonDeLivraisonId}/colis/{colisId}` — PDF du bordereau

### Timeline de suivi (côté expéditeur — colis & courrier)
- `GET /api/colis/{colisId}/timeline` — historique détaillé de tous les événements (dépôt, en cours avec nom du livreur, appel, SMS envoyé, appel sortant avec durée, client injoignable, retour dépôt, livré...), triés chronologiquement. Inspiré de l'écran de suivi fournisseur montré par l'utilisateur (First Delivery). Les événements de statut (dépôt, livré, retourné) sont générés automatiquement ; les événements terrain (appel, SMS, retour dépôt) sont ajoutés via :
- `POST /api/colis/{colisId}/timeline?type=APPEL_SORTANT&livreurNom=...&dureeAppelSecondes=3` — ajout manuel d'un événement (ex: depuis l'app Livreur)

## Flux d'inscription (comme Intigo)
1. Un expéditeur (ou tout profil) s'inscrit via `POST /api/auth/register` → compte créé, statut `EN_ATTENTE_VALIDATION`.
2. Il **ne peut pas se connecter** (`/api/auth/login` renvoie 403) tant que le compte n'est pas validé.
3. Un Admin/Superviseur consulte `GET /api/auth/comptes-en-attente` et valide (`PATCH .../valider`) ou rejette (`PATCH .../rejeter`).
4. Une fois `VALIDE`, l'utilisateur peut se connecter normalement et obtenir un token utilisable.

## Qui sont les Chefs d'agence (et les autres comptes internes) ?
Contrairement aux expéditeurs, les rôles internes (Admin, Superviseur, **Chef d'agence**, Livreur) ne s'auto-inscrivent jamais. Ils sont créés exclusivement par un Admin via `POST /api/auth/interne`, déjà actifs (pas de file d'attente), et pour un Chef d'agence, obligatoirement rattachés à une agence via `agenceId`. C'est ce qui empêche n'importe qui de se déclarer Chef d'agence ou Admin en s'inscrivant sur le formulaire public.

**Le tout premier Admin** est créé automatiquement au premier démarrage de l'application (`AdminBootstrap`), avec le téléphone/mot de passe définis par `ADMIN_BOOTSTRAP_TELEPHONE` / `ADMIN_BOOTSTRAP_PASSWORD` (valeurs par défaut à changer absolument avant la mise en production). Ensuite, cet Admin crée tous les autres comptes internes via `/api/auth/interne`.

## Ce qui est fait à 100% (code)
- Auth JWT multi-rôles, sécurité par route
- CRUD complet Agence / Colis / Bon de livraison / Règlement, avec pagination
- Génération PDF du bordereau (code-barres réel)
- Email transactionnel réellement câblé (JavaMailSender)
- SMS/Push : interface prête, implémentation "log" par défaut, à brancher sur un vrai fournisseur
- Événements internes (ApplicationEventPublisher) pour le découplage colis ↔ notification
- CORS et secret JWT externalisés en variables d'environnement
- Tests unitaires (Auth, Colis) avec JUnit 5 + Mockito
- Documentation API interactive (Swagger UI / OpenAPI)

## Ce qui reste (nécessite des comptes/config externes, pas du code)
- Un compte SMTP réel (Gmail, SendGrid...) pour activer l'email en prod
- Un compte fournisseur SMS tunisien (ou Twilio) + implémentation de `SmsProvider`
- Un projet Firebase + credentials pour activer les push (`PushProvider`)
- Une vraie base PostgreSQL de prod (au lieu de Docker local)
- CI/CD (build, tests, déploiement)
- Compilation non vérifiée ici (pas d'accès à Maven Central dans ce sandbox) — lancer `mvn clean install` pour confirmer.
