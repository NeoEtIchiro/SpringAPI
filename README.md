# SpringAPI

API de démonstration (articles / likes / utilisateurs) avec authentification JWT et gestion de rôles (`publisher`, `moderator`).

</div>

## Sommaire
1. Objectifs
2. Stack & Prérequis
3. Structure du projet
4. Installation & Lancement
5. Configuration (application.properties)
6. Authentification & JWT
7. Rôles et Autorisations
8. Endpoints principaux
9. Exemples curl / Postman
10. Tags Git attendus
11. Stratégie de sécurité technique
12. Améliorations possibles

---

## 1. Objectifs
Illustrer :
- Utilisation de Spring Boot 3 (Java 21)
- Persistance JPA (MySQL)
- Sécurisation via Spring Security + JWT
- Différenciation des vues selon rôle (public / publisher / moderator)
- Organisation simple (pas de sous-modules complexes)

## 2. Stack & Prérequis
- Java 21
- Maven Wrapper (`./mvnw`)
- MySQL 8.x
- Spring Boot 3.5.x
- Dépendances majeures :
	- spring-boot-starter-web
	- spring-boot-starter-data-jpa
	- spring-boot-starter-security
	- mysql-connector-j
	- jjwt-api / jjwt-impl / jjwt-jackson (>= 0.11.5)

## 3. Structure du projet
```
src/main/java/com/neo/SpringAPI
	├── SpringApiApplication.java
	├── Entities/ (Article, User, Like)
	├── Repositories/ (ArticleRepository, UserRepository, LikeRepository)
	├── Controllers/ (ArticleController, UserController, LikeController, AuthController)
	└── Security/ (JwtAuthFilter, TokenGenerator, TokenValidator, UserDetailsServiceSpringAPI, UnauthorizedHandler, WebSecurityConfigurer, JwtDTO)
```

## 4. Installation & Lancement
1. Configurer MySQL :
```sql
CREATE DATABASE springapi_db CHARACTER SET utf8mb4;
CREATE USER 'springuser'@'%' IDENTIFIED BY 'Mot.de.passe2004';
GRANT ALL PRIVILEGES ON springapi_db.* TO 'springuser'@'%';
FLUSH PRIVILEGES;
```
2. Cloner et lancer :
```bash
git clone <repo>
cd SpringAPI
./mvnw clean spring-boot:run
```
3. Application accessible sur http://localhost:8080

## 5. Configuration (application.properties)
```
spring.datasource.url=jdbc:mysql://localhost:3306/springapi_db?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=springuser
spring.datasource.password=Mot.de.passe2004
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

springapi.app.jwtSecret=<SECRET_BASE64_>= (≥ 64 octets encodés Base64)
springapi.app.jwtExpirationMs=8640000
```
Générer un secret :
```bash
openssl rand -base64 64
```

## 6. Authentification & JWT
- Endpoint d'authentification : `POST /login` (email + password JSON ou form).
- Réponse : `{ token, type, username, roles }`.
- Le token doit être envoyé dans `Authorization: Bearer <token>`.
- Expiration paramétrée via `springapi.app.jwtExpirationMs`.

## 7. Rôles et Autorisations (spécification fonctionnelle)
| Cas | Non authentifié | publisher | moderator |
|-----|-----------------|-----------|-----------|
| Lire articles existants | Oui (vue publique) | Oui (vue enrichie) | Oui (vue complète) |
| Créer article | Non | Oui | Oui |
| Modifier article | Non | Oui (si auteur) | Oui (tout) |
| Supprimer article | Non | Oui (si auteur) | Oui (tout) |
| Voir likes/dislikes détaillés | Non | Agrégats seulement | Détail + listes utilisateurs |
| Liker/Disliker | Non | Oui (pas sur ses propres articles) | Oui (option selon politique) |

## 8. Endpoints principaux
| Méthode | URL | Description | Auth | Rôle |
|---------|-----|-------------|------|------|
| POST | /login | Authentification | Non | - |
| POST | /users/add | Création user | Non | - |
| GET | /articles/public | Liste publique (auteur,date,content) | Non | - |
| GET | /articles/all | Vue selon rôle | Oui/Non | Selon rôle |
| POST | /articles/add | Créer article | Oui | publisher/moderator |
| PUT | /articles/update?id= | Modifier article | Oui | auteur/moderator |
| DELETE | /articles/delete?id= | Supprimer article | Oui | auteur/moderator |
| POST | /likes/add | Liker/Disliker | Oui | publisher (≠ auteur) |
| GET | /likes/all | Tous les likes | Oui | moderator (sinon restreindre) |

## 9. Exemples curl
Authentification :
```bash
curl -H "Content-Type: application/json" -d '{"email":"alice@test.com","password":"pass123"}' http://localhost:8080/login
```
Stocker le token :
```bash
TOKEN=$(curl -s -H 'Content-Type: application/json' -d '{"email":"alice@test.com","password":"pass123"}' http://localhost:8080/login | jq -r .token)
```
Vue publique :
```bash
curl http://localhost:8080/articles/public
```
Vue role-aware :
```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/articles/all
```
Création :
```bash
curl -X POST 'http://localhost:8080/articles/add?content=Texte&authorId=1' -H "Authorization: Bearer $TOKEN"
```

## 10. Tags Git attendus
| Tag | Contenu |
|-----|---------|
| `authentification` | Introduction sécurisation + /login + JWT |
| `autorisations` | Mise en place des règles de rôles et filtrage des vues |

Création d'un tag :
```bash
git tag -a authentification -m "Auth initiale"
git push origin authentification
```

## 11. Stratégie de sécurité technique
Chaîne de filtres :
1. `JwtAuthFilter` lit l'en-tête Authorization.
2. Valide et peuple le `SecurityContext`.
3. Méthodes contrôleurs annotées (`@PreAuthorize`) appliquent les règles.

Points d'attention :
- Pas encore de hashing de mot de passe (à remplacer par BCrypt).
- Pas de refresh token (option future).
- Vérifier taille du secret (≥ 256 bits effectifs).

## 12. Améliorations possibles
- Hash des mots de passe (BCryptPasswordEncoder).
- DTO de réponse homogènes (mapper type MapStruct).
- Pagination /articles.
- Tests unitaires et d'intégration (SpringBootTest + MockMvc).
- Mise en cache (Spring Cache) des agrégats de likes.
- Ajout d'un refresh token / rotation / liste de révocation.
- Observabilité (actuator, metrics, logs structurés).

---

## FAQ rapide
| Problème | Cause probable | Solution |
|----------|----------------|----------|
| 401 sur endpoint protégé | Token manquant / expiré | Vérifier en-tête Bearer |
| WeakKeyException | Secret trop court | Générer secret ≥ 64 octets |
| NoClassDefFound `DatatypeConverter` | Ancienne version jjwt | Utiliser jjwt 0.11.x |

---

## Licence
Projet pédagogique interne (adapter selon besoin).
