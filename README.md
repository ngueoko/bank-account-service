# Atelier Spring Boot : gestion des comptes avec REST, JPA et Spring Data REST

> **Objectif :** construire progressivement une application Spring Boot
> de gestion de comptes, tester sa couche DAO, exposer des API REST,
> documenter les API avec Swagger/OpenAPI, puis découvrir Spring Data
> REST, les projections, les DTOs et les mappers.

## Sommaire

1.  [Créer le projet Spring Boot](#1-créer-le-projet-spring-boot)
2.  [Créer l'entité JPA `Compte`](#2-créer-lentité-jpa-compte)
3.  [Créer `CompteRepository`](#3-créer-compterepository)
4.  [Tester la couche DAO](#4-tester-la-couche-dao)
5.  [Créer le service REST de gestion des
    comptes](#5-créer-le-service-rest-de-gestion-des-comptes)
6.  [Tester le microservice avec
    Postman](#6-tester-le-microservice-avec-postman)
7.  [Générer et tester la documentation
    Swagger](#7-générer-et-tester-la-documentation-swagger)
8.  [Exposer une API avec Spring Data REST et des
    projections](#8-exposer-une-api-avec-spring-data-rest-et-des-projections)
9.  [Créer les DTOs et les mappers](#9-créer-les-dtos-et-les-mappers)
10. [Créer la couche Service métier et finaliser le
    microservice](#10-créer-la-couche-service-métier-et-finaliser-le-microservice)

------------------------------------------------------------------------

## 1. Créer le projet Spring Boot

### Travail à faire

Créer un projet Spring Boot avec les dépendances suivantes :

-   **Spring Web** : création des contrôleurs et des API REST.
-   **Spring Data JPA** : accès aux données avec JPA et Hibernate.
-   **H2 Database** : base de données en mémoire pour les tests et les
    démonstrations.
-   **Lombok** : réduction du code répétitif (getters, setters,
    constructeurs, etc.).
-   **Spring Boot Test** : tests unitaires et tests d'intégration.

Le projet peut être créé avec [Spring
Initializr](https://start.spring.io/).

### Paramètres suggérés

  -----------------------------------------------------------------------
  Paramètre                           Valeur indicative
  ----------------------------------- -----------------------------------
  Project                             Maven

  Language                            Java

  Spring Boot                         Choisir une version stable
                                      compatible avec l'environnement

  Group                               `com.example`

  Artifact                            `gestion-comptes`

  Packaging                           Jar

  Java                                17 ou 21, selon la version de
                                      Spring Boot retenue
  -----------------------------------------------------------------------

### Dépendances Maven

Exemple de dépendances à sélectionner dans Spring Initializr ou à
déclarer dans le `pom.xml` :

``` xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

Les versions des dépendances Spring Boot sont normalement gérées par le
parent ou le BOM Spring Boot. Éviter de leur attribuer des versions
individuellement sans raison particulière.

### Structure de départ

``` text
gestion-comptes/
├── src/
│   ├── main/
│   │   ├── java/com/example/gestioncomptes/
│   │   │   └── GestionComptesApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/example/gestioncomptes/
└── pom.xml
```

### Configuration H2

Dans `src/main/resources/application.properties` :

``` properties
spring.application.name=gestion-comptes

spring.datasource.url=jdbc:h2:mem:comptesdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

> La base `mem` est créée en mémoire. Elle est généralement
> réinitialisée à l'arrêt de l'application. Pour conserver les données
> entre les redémarrages, une configuration H2 en mode fichier peut être
> utilisée.

### Vérifications

1.  Démarrer l'application.
2.  Vérifier qu'elle démarre sans erreur.
3.  Ouvrir la console H2 à l'adresse `http://localhost:8080/h2-console`.
4.  Utiliser l'URL JDBC `jdbc:h2:mem:comptesdb`, l'utilisateur `sa` et
    un mot de passe vide.

### Captures d'écran à insérer

-   **Image 1.1 --- Spring Initializr :** paramètres du projet et choix
    des dépendances.
-   **Image 1.2 --- IntelliJ IDEA :** projet Maven importé et
    arborescence initiale.
-   **Image 1.3 --- `pom.xml` :** dépendances Web, JPA, H2 et Lombok.
-   **Image 1.4 --- Console H2 :** page de connexion à la base de
    données.

```{=html}
<!-- IMAGE À INSÉRER : Spring Initializr et sélection des dépendances -->
```
```{=html}
<!-- IMAGE À INSÉRER : projet Maven ouvert dans IntelliJ IDEA -->
```
```{=html}
<!-- IMAGE À INSÉRER : console H2 connectée -->
```

------------------------------------------------------------------------

## 2. Créer l'entité JPA `Compte`

### Travail à faire

Créer une classe `Compte` représentant un compte bancaire. Cette classe
sera persistée dans la base de données grâce à JPA.

### Attributs proposés

  Attribut         Type Java                  Description
  ---------------- -------------------------- -----------------------
  `id`             `Long`                     Identifiant du compte
  `solde`          `double` ou `BigDecimal`   Solde du compte
  `dateCreation`   `LocalDateTime`            Date de création
  `type`           `TypeCompte`               Type de compte

Pour une application financière, `BigDecimal` est préférable à `double`
pour représenter les montants, afin d'éviter les imprécisions des
nombres flottants.

### Énumération `TypeCompte`

``` java
public enum TypeCompte {
    COURANT,
    EPARGNE
}
```

### Entité `Compte`

``` java
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal solde;

    private LocalDateTime dateCreation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeCompte type;
}
```

> Avec Spring Boot 3, les annotations JPA proviennent de
> `jakarta.persistence`. Pour un projet Spring Boot 2, les imports sont
> généralement en `javax.persistence`.

### Points à comprendre

-   `@Entity` indique que la classe est une entité JPA.
-   `@Id` désigne la clé primaire.
-   `@GeneratedValue` configure la génération de l'identifiant.
-   `@Column` précise les caractéristiques d'une colonne.
-   `@Enumerated(EnumType.STRING)` stocke le nom de l'énumération dans
    la base.
-   Les annotations Lombok génèrent automatiquement les méthodes et
    constructeurs correspondants.

### Captures d'écran à insérer

-   **Image 2.1 ---** classe `Compte` annotée avec `@Entity`.
-   **Image 2.2 ---** énumération `TypeCompte`.
-   **Image 2.3 ---** table `COMPTE` visible dans la console H2.

```{=html}
<!-- IMAGE À INSÉRER : code de l'entité Compte dans IntelliJ -->
```
```{=html}
<!-- IMAGE À INSÉRER : table COMPTE dans la console H2 -->
```

------------------------------------------------------------------------

## 3. Créer `CompteRepository`

### Travail à faire

Créer l'interface `CompteRepository` en s'appuyant sur Spring Data JPA.

``` java
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompteRepository extends JpaRepository<Compte, Long> {
}
```

Cette simple déclaration donne accès à plusieurs opérations standards :

  Méthode            Rôle
  ------------------ ----------------------------------------
  `save(compte)`     Enregistrer ou mettre à jour un compte
  `findById(id)`     Rechercher un compte par identifiant
  `findAll()`        Récupérer tous les comptes
  `deleteById(id)`   Supprimer un compte
  `existsById(id)`   Vérifier l'existence d'un compte
  `count()`          Compter les comptes

### Ajouter une méthode de recherche dérivée

Spring Data JPA peut générer une requête à partir du nom de la méthode :

``` java
import java.util.List;

public interface CompteRepository extends JpaRepository<Compte, Long> {

    List<Compte> findByType(TypeCompte type);

}
```

Cette méthode permet de rechercher les comptes selon leur type.

### Captures d'écran à insérer

-   **Image 3.1 ---** interface `CompteRepository` dans IntelliJ.
-   **Image 3.2 ---** méthodes héritées de `JpaRepository` proposées par
    l'IDE.

```{=html}
<!-- IMAGE À INSÉRER : interface CompteRepository et héritage JpaRepository -->
```

------------------------------------------------------------------------

## 4. Tester la couche DAO

### Travail à faire

Vérifier que le repository permet bien d'enregistrer, de rechercher, de
modifier et de supprimer des comptes.

Les tests peuvent être réalisés avec `@DataJpaTest`, qui configure un
contexte de test adapté à la couche JPA.

### Exemple de test

``` java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CompteRepositoryTest {

    @Autowired
    private CompteRepository compteRepository;

    @Test
    void doitEnregistrerEtRetrouverUnCompte() {
        Compte compte = Compte.builder()
                .solde(new BigDecimal("1500.00"))
                .dateCreation(LocalDateTime.now())
                .type(TypeCompte.COURANT)
                .build();

        Compte compteEnregistre = compteRepository.save(compte);

        assertThat(compteEnregistre.getId()).isNotNull();

        var resultat = compteRepository.findById(compteEnregistre.getId());

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getSolde())
                .isEqualByComparingTo("1500.00");
    }
}
```

### Scénarios de test à réaliser

1.  Enregistrer un compte.
2.  Rechercher un compte par son identifiant.
3.  Récupérer la liste des comptes.
4.  Rechercher les comptes par type.
5.  Modifier le solde d'un compte.
6.  Supprimer un compte.
7.  Vérifier le comportement lorsqu'un identifiant n'existe pas.

### Captures d'écran à insérer

-   **Image 4.1 ---** classe de test `CompteRepositoryTest`.
-   **Image 4.2 ---** résultats des tests dans IntelliJ IDEA.
-   **Image 4.3 ---** données enregistrées dans H2.

```{=html}
<!-- IMAGE À INSÉRER : tests JUnit dans IntelliJ -->
```
```{=html}
<!-- IMAGE À INSÉRER : résultats verts des tests DAO -->
```
```{=html}
<!-- IMAGE À INSÉRER : enregistrements dans la table COMPTE -->
```

------------------------------------------------------------------------

## 5. Créer le service REST de gestion des comptes

### Travail à faire

Créer un contrôleur REST qui expose les opérations de gestion des
comptes.

Dans un premier temps, le contrôleur peut utiliser directement
`CompteRepository` pour comprendre le fonctionnement des annotations
REST. La séparation en couche métier sera réalisée dans la partie 10.

### Endpoints proposés

  Méthode HTTP   URL                   Opération
  -------------- --------------------- ---------------------
  `GET`          `/api/comptes`        Lister les comptes
  `GET`          `/api/comptes/{id}`   Consulter un compte
  `POST`         `/api/comptes`        Créer un compte
  `PUT`          `/api/comptes/{id}`   Modifier un compte
  `DELETE`       `/api/comptes/{id}`   Supprimer un compte

### Exemple de contrôleur

``` java
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comptes")
public class CompteRestController {

    private final CompteRepository compteRepository;

    public CompteRestController(CompteRepository compteRepository) {
        this.compteRepository = compteRepository;
    }

    @GetMapping
    public List<Compte> getComptes() {
        return compteRepository.findAll();
    }

    @GetMapping("/{id}")
    public Compte getCompte(@PathVariable Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Compte introuvable"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Compte createCompte(@RequestBody Compte compte) {
        compte.setId(null);
        return compteRepository.save(compte);
    }

    @PutMapping("/{id}")
    public Compte updateCompte(
            @PathVariable Long id,
            @RequestBody Compte compte) {

        if (!compteRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Compte introuvable");
        }

        compte.setId(id);
        return compteRepository.save(compte);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompte(@PathVariable Long id) {
        if (!compteRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Compte introuvable");
        }

        compteRepository.deleteById(id);
    }
}
```

### Points à comprendre

-   `@RestController` : indique que la classe expose des ressources
    REST.
-   `@RequestMapping` : définit le chemin de base.
-   `@GetMapping` : expose une opération HTTP GET.
-   `@PostMapping` : expose une opération HTTP POST.
-   `@PutMapping` : expose une opération HTTP PUT.
-   `@DeleteMapping` : expose une opération HTTP DELETE.
-   `@PathVariable` : récupère une valeur présente dans l'URL.
-   `@RequestBody` : convertit le corps JSON en objet Java.

> Cette première version est pédagogique. Dans une application plus
> complète, il est préférable de placer la logique métier dans un
> service, de valider les entrées et d'exposer des DTOs plutôt que les
> entités JPA directement.

### Captures d'écran à insérer

-   **Image 5.1 ---** classe `CompteRestController`.
-   **Image 5.2 ---** annotations REST et endpoints dans IntelliJ.
-   **Image 5.3 ---** application Spring Boot démarrée dans la console.

```{=html}
<!-- IMAGE À INSÉRER : code du contrôleur REST -->
```
```{=html}
<!-- IMAGE À INSÉRER : application démarrée sans erreur -->
```

------------------------------------------------------------------------

## 6. Tester le microservice avec Postman

### Travail à faire

Utiliser Postman, ou un autre client REST, pour appeler les endpoints et
vérifier les réponses HTTP.

Adresse de base :

``` text
http://localhost:8080
```

### Test 1 --- Créer un compte

-   Méthode : `POST`
-   URL : `http://localhost:8080/api/comptes`
-   En-tête : `Content-Type: application/json`
-   Corps en mode `raw` / `JSON` :

``` json
{
  "solde": 1500.00,
  "dateCreation": "2026-01-15T10:30:00",
  "type": "COURANT"
}
```

Résultat attendu : statut `201 Created`, avec le compte créé et son
identifiant.

### Test 2 --- Lister les comptes

-   Méthode : `GET`
-   URL : `http://localhost:8080/api/comptes`

Résultat attendu : statut `200 OK` et une liste JSON.

### Test 3 --- Consulter un compte

-   Méthode : `GET`
-   URL : `http://localhost:8080/api/comptes/1`

Remplacer `1` par l'identifiant réellement retourné lors de la création.

### Test 4 --- Modifier un compte

-   Méthode : `PUT`
-   URL : `http://localhost:8080/api/comptes/1`
-   Corps JSON :

``` json
{
  "solde": 2000.00,
  "dateCreation": "2026-01-15T10:30:00",
  "type": "EPARGNE"
}
```

Résultat attendu : statut `200 OK`.

### Test 5 --- Supprimer un compte

-   Méthode : `DELETE`
-   URL : `http://localhost:8080/api/comptes/1`

Résultat attendu : statut `204 No Content`.

### Vérifications complémentaires

Tester également : - un identifiant inexistant : réponse attendue
`404 Not Found` ; - un JSON invalide : réponse d'erreur ; - un type de
compte inconnu : réponse d'erreur de conversion ; - une requête sans
corps lorsque celui-ci est obligatoire.

### Captures d'écran à insérer

-   **Image 6.1 ---** requête POST dans Postman et réponse
    `201 Created`.
-   **Image 6.2 ---** requête GET retournant la liste des comptes.
-   **Image 6.3 ---** requête PUT et réponse de modification.
-   **Image 6.4 ---** requête DELETE et réponse `204 No Content`.
-   **Image 6.5 ---** exemple de réponse `404 Not Found`.

```{=html}
<!-- IMAGE À INSÉRER : Postman - création d'un compte -->
```
```{=html}
<!-- IMAGE À INSÉRER : Postman - liste des comptes -->
```
```{=html}
<!-- IMAGE À INSÉRER : Postman - modification et suppression -->
```

------------------------------------------------------------------------

## 7. Générer et tester la documentation Swagger

### Travail à faire

Ajouter Springdoc OpenAPI pour générer automatiquement la documentation
des API REST et disposer d'une interface Swagger UI.

**Utiliser impérativement la dépendance suivante**, à la place de celle
mentionnée dans la vidéo :

``` xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.13</version>
</dependency>
```

Cette dépendance est à ajouter dans la section `<dependencies>` du
`pom.xml`.

### Recharger Maven

Après modification du `pom.xml` :

1.  Enregistrer le fichier.
2.  Ouvrir la fenêtre Maven dans IntelliJ.
3.  Cliquer sur **Reload All Maven Projects**.
4.  Vérifier que la dépendance Springdoc est téléchargée.
5.  Redémarrer l'application.

### Accéder à Swagger UI

Lorsque l'application est démarrée, ouvrir :

``` text
http://localhost:8080/swagger-ui/index.html
```

Le document OpenAPI au format JSON est normalement accessible à
l'adresse :

``` text
http://localhost:8080/v3/api-docs
```

### Tester les API avec Swagger

1.  Ouvrir Swagger UI.
2.  Déplier la section correspondant à `CompteRestController`.
3.  Sélectionner une opération, par exemple `GET /api/comptes`.
4.  Cliquer sur **Try it out**.
5.  Cliquer sur **Execute**.
6.  Examiner la requête, le code HTTP et la réponse JSON.

### Personnalisation facultative

Il est possible d'ajouter des annotations OpenAPI pour enrichir la
documentation :

``` java
@Operation(
    summary = "Lister les comptes",
    description = "Retourne la liste de tous les comptes"
)
@GetMapping
public List<Compte> getComptes() {
    return compteRepository.findAll();
}
```

Importer `Operation` depuis `io.swagger.v3.oas.annotations`.

### Captures d'écran à insérer

-   **Image 7.1 ---** dépendance Springdoc dans le `pom.xml`.
-   **Image 7.2 ---** Swagger UI affichant les endpoints.
-   **Image 7.3 ---** exécution de `GET /api/comptes` dans Swagger.
-   **Image 7.4 ---** document JSON `/v3/api-docs`.

```{=html}
<!-- IMAGE À INSÉRER : dépendance springdoc dans pom.xml -->
```
```{=html}
<!-- IMAGE À INSÉRER : interface Swagger UI -->
```
```{=html}
<!-- IMAGE À INSÉRER : test d'un endpoint depuis Swagger -->
```

------------------------------------------------------------------------

## 8. Exposer une API avec Spring Data REST et des projections

### Travail à faire

Découvrir Spring Data REST, qui permet d'exposer automatiquement des
repositories sous forme de ressources REST, sans écrire un contrôleur
REST pour chaque opération.

### Ajouter la dépendance

``` xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-rest</artifactId>
</dependency>
```

La version est gérée par le parent ou le BOM Spring Boot.

### Exposer le repository

Il est possible de personnaliser le chemin de la ressource :

``` java
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "comptes")
public interface CompteRepository extends JpaRepository<Compte, Long> {
}
```

L'API Spring Data REST sera alors exposée sous une URL de la forme :

``` text
http://localhost:8080/comptes
```

> Attention : si le contrôleur de la partie 5 expose déjà
> `/api/comptes`, il ne faut pas confondre les deux API. Elles peuvent
> coexister avec des chemins distincts, mais il est important de les
> distinguer dans les tests et la documentation.

### Créer une projection

Une projection permet de choisir les propriétés visibles dans une
représentation REST.

Exemple : `CompteProjection`.

``` java
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "solde", types = Compte.class)
public interface CompteProjection {
    Long getId();
    BigDecimal getSolde();
    TypeCompte getType();
}
```

Ajouter les imports nécessaires :

``` java
import java.math.BigDecimal;
```

La projection peut être appelée avec le paramètre `projection` sur une
ressource compatible, par exemple :

``` text
http://localhost:8080/comptes/1?projection=solde
```

La disponibilité et la forme exacte des liens dépendent de la
configuration Spring Data REST et de la ressource exposée.

### À comprendre

-   Spring Data REST expose automatiquement les repositories
    compatibles.
-   Les ressources sont généralement présentées selon le format HAL.
-   Les projections permettent de contrôler les propriétés présentées.
-   Les projections ne remplacent pas systématiquement les DTOs utilisés
    dans une API métier.

### Captures d'écran à insérer

-   **Image 8.1 ---** dépendance Spring Data REST dans le `pom.xml`.
-   **Image 8.2 ---** interface `CompteRepository` annotée.
-   **Image 8.3 ---** réponse HAL de `/comptes`.
-   **Image 8.4 ---** réponse utilisant la projection `solde`.

```{=html}
<!-- IMAGE À INSÉRER : repository Spring Data REST -->
```
```{=html}
<!-- IMAGE À INSÉRER : réponse HAL dans Postman ou navigateur -->
```
```{=html}
<!-- IMAGE À INSÉRER : réponse REST avec projection -->
```

------------------------------------------------------------------------

## 9. Créer les DTOs et les mappers

### Travail à faire

Introduire des DTOs (*Data Transfer Objects*) pour séparer les objets
échangés par l'API des entités persistées en base de données.

Cette séparation permet notamment : - de ne pas exposer directement la
structure interne de l'entité ; - de contrôler les champs reçus et
retournés ; - de faire évoluer l'API indépendamment du modèle de
persistance ; - de centraliser certaines règles de conversion.

### Créer `CompteDTO`

``` java
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompteDTO(
        Long id,
        BigDecimal solde,
        LocalDateTime dateCreation,
        TypeCompte type
) {
}
```

Un `record` est adapté à un DTO simple en Java 17 ou supérieur. Une
classe classique peut également être utilisée.

### Créer un DTO de création

Il est souvent préférable de distinguer les données de création des
données de réponse :

``` java
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateCompteDTO(
        @NotNull
        @PositiveOrZero
        BigDecimal solde,

        @NotNull
        TypeCompte type
) {
}
```

Pour activer les validations, ajouter si nécessaire la dépendance
`spring-boot-starter-validation`.

### Créer `CompteMapper`

Un mapper convertit une entité en DTO et inversement.

``` java
import org.springframework.stereotype.Component;

@Component
public class CompteMapper {

    public CompteDTO toDTO(Compte compte) {
        return new CompteDTO(
                compte.getId(),
                compte.getSolde(),
                compte.getDateCreation(),
                compte.getType()
        );
    }

    public Compte toEntity(CreateCompteDTO dto) {
        return Compte.builder()
                .solde(dto.solde())
                .dateCreation(LocalDateTime.now())
                .type(dto.type())
                .build();
    }
}
```

Ajouter les imports :

``` java
import java.time.LocalDateTime;
```

### Variante : utiliser MapStruct

MapStruct peut générer automatiquement le code de mapping à la
compilation. Il nécessite une configuration supplémentaire du processeur
d'annotations dans Maven, notamment si Lombok est également utilisé.

Pour ce travail, commencer par un mapper manuel permet de mieux
comprendre le rôle de cette couche.

### Captures d'écran à insérer

-   **Image 9.1 ---** classe ou record `CompteDTO`.
-   **Image 9.2 ---** DTO de création `CreateCompteDTO`.
-   **Image 9.3 ---** classe `CompteMapper`.
-   **Image 9.4 ---** exemple de réponse JSON utilisant le DTO.

```{=html}
<!-- IMAGE À INSÉRER : CompteDTO et CreateCompteDTO -->
```
```{=html}
<!-- IMAGE À INSÉRER : CompteMapper dans IntelliJ -->
```
```{=html}
<!-- IMAGE À INSÉRER : réponse Postman au format DTO -->
```

------------------------------------------------------------------------

## 10. Créer la couche Service métier et finaliser le microservice

### Travail à faire

Faire évoluer l'architecture pour séparer clairement les responsabilités
:

-   **Controller** : reçoit les requêtes HTTP et retourne les réponses.
-   **Service** : porte les règles métier et orchestre les opérations.
-   **Repository** : assure l'accès aux données.
-   **Mapper** : convertit les entités en DTOs et inversement.
-   **DTO** : définit les données échangées avec les clients.

### Architecture cible

``` text
src/main/java/com/example/gestioncomptes/
├── GestionComptesApplication.java
├── controller/
│   └── CompteRestController.java
├── service/
│   ├── CompteService.java
│   └── CompteServiceImpl.java
├── repository/
│   └── CompteRepository.java
├── entity/
│   ├── Compte.java
│   └── TypeCompte.java
├── dto/
│   ├── CompteDTO.java
│   └── CreateCompteDTO.java
└── mapper/
    └── CompteMapper.java
```

### Définir l'interface `CompteService`

``` java
import java.util.List;

public interface CompteService {

    List<CompteDTO> getAllComptes();

    CompteDTO getCompte(Long id);

    CompteDTO createCompte(CreateCompteDTO dto);

    CompteDTO updateCompte(Long id, CreateCompteDTO dto);

    void deleteCompte(Long id);
}
```

### Implémenter `CompteServiceImpl`

``` java
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CompteServiceImpl implements CompteService {

    private final CompteRepository compteRepository;
    private final CompteMapper compteMapper;

    public CompteServiceImpl(
            CompteRepository compteRepository,
            CompteMapper compteMapper) {
        this.compteRepository = compteRepository;
        this.compteMapper = compteMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompteDTO> getAllComptes() {
        return compteRepository.findAll()
                .stream()
                .map(compteMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompteDTO getCompte(Long id) {
        Compte compte = trouverCompte(id);
        return compteMapper.toDTO(compte);
    }

    @Override
    public CompteDTO createCompte(CreateCompteDTO dto) {
        Compte compte = compteMapper.toEntity(dto);
        Compte saved = compteRepository.save(compte);
        return compteMapper.toDTO(saved);
    }

    @Override
    public CompteDTO updateCompte(Long id, CreateCompteDTO dto) {
        Compte compte = trouverCompte(id);

        compte.setSolde(dto.solde());
        compte.setType(dto.type());

        Compte updated = compteRepository.save(compte);
        return compteMapper.toDTO(updated);
    }

    @Override
    public void deleteCompte(Long id) {
        Compte compte = trouverCompte(id);
        compteRepository.delete(compte);
    }

    private Compte trouverCompte(Long id) {
        return compteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Compte introuvable : " + id
                ));
    }
}
```

### Adapter le contrôleur REST

Le contrôleur ne doit plus appeler directement le repository. Il délègue
désormais les opérations à `CompteService`.

``` java
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comptes")
public class CompteRestController {

    private final CompteService compteService;

    public CompteRestController(CompteService compteService) {
        this.compteService = compteService;
    }

    @GetMapping
    public List<CompteDTO> getComptes() {
        return compteService.getAllComptes();
    }

    @GetMapping("/{id}")
    public CompteDTO getCompte(@PathVariable Long id) {
        return compteService.getCompte(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompteDTO createCompte(@Valid @RequestBody CreateCompteDTO dto) {
        return compteService.createCompte(dto);
    }

    @PutMapping("/{id}")
    public CompteDTO updateCompte(
            @PathVariable Long id,
            @Valid @RequestBody CreateCompteDTO dto) {
        return compteService.updateCompte(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompte(@PathVariable Long id) {
        compteService.deleteCompte(id);
    }
}
```

### Finaliser et tester l'architecture

1.  Vérifier que le contrôleur ne dépend plus de `CompteRepository`.
2.  Vérifier que le service utilise le repository et le mapper.
3.  Vérifier que les réponses REST utilisent les DTOs.
4.  Tester les cas nominaux et les erreurs.
5.  Vérifier que Swagger documente les endpoints.
6.  Exécuter l'ensemble des tests automatisés.

### Captures d'écran à insérer

-   **Image 10.1 ---** arborescence finale du projet.
-   **Image 10.2 ---** interface `CompteService`.
-   **Image 10.3 ---** implémentation `CompteServiceImpl`.
-   **Image 10.4 ---** contrôleur REST utilisant le service.
-   **Image 10.5 ---** tests de l'application et résultats dans
    IntelliJ.
-   **Image 10.6 ---** API finale testée avec Postman et documentée avec
    Swagger.

```{=html}
<!-- IMAGE À INSÉRER : architecture finale dans IntelliJ -->
```
```{=html}
<!-- IMAGE À INSÉRER : service métier et contrôleur -->
```
```{=html}
<!-- IMAGE À INSÉRER : tests finaux -->
```
```{=html}
<!-- IMAGE À INSÉRER : Swagger et Postman après refactorisation -->
```

------------------------------------------------------------------------

## Bilan du travail

À la fin de cet atelier, l'application doit permettre de :

-   créer et configurer un projet Spring Boot avec Maven ;
-   modéliser une entité JPA et la persister dans H2 ;
-   exploiter Spring Data JPA avec un repository ;
-   tester la couche DAO avec JUnit ;
-   exposer des opérations REST ;
-   tester les API avec Postman ;
-   générer et consulter la documentation OpenAPI avec Swagger UI ;
-   exposer un repository avec Spring Data REST ;
-   présenter des données avec des projections ;
-   séparer les entités des DTOs grâce aux mappers ;
-   organiser le code autour d'une couche métier Service.

## Checklist de livraison

-   [ ] Projet Spring Boot créé et démarré.
-   [ ] Dépendances Maven vérifiées.
-   [ ] Entité `Compte` créée.
-   [ ] `CompteRepository` créé.
-   [ ] Tests DAO exécutés avec succès.
-   [ ] Contrôleur REST opérationnel.
-   [ ] Requêtes Postman testées.
-   [ ] Swagger UI accessible.
-   [ ] Dépendance Springdoc `2.8.13` utilisée.
-   [ ] API Spring Data REST testée.
-   [ ] Projection testée.
-   [ ] DTOs et mapper créés.
-   [ ] Couche Service métier créée.
-   [ ] Contrôleur refactorisé pour utiliser le service.
-   [ ] Captures d'écran ajoutées à chaque partie.

## Ressources utiles

-   [Spring Initializr](https://start.spring.io/)
-   [Documentation Spring
    Boot](https://docs.spring.io/spring-boot/index.html)
-   [Documentation Spring Data
    JPA](https://docs.spring.io/spring-data/jpa/reference/)
-   [Documentation Spring Data
    REST](https://docs.spring.io/spring-data/rest/reference/)
-   [Documentation Springdoc OpenAPI](https://springdoc.org/)
-   [Documentation Lombok](https://projectlombok.org/)
-   [Documentation Postman](https://learning.postman.com/)
-   [Documentation H2
    Database](https://www.h2database.com/html/main.html)

------------------------------------------------------------------------

**Consigne pour les captures :** remplacer chaque commentaire
`IMAGE À INSÉRER` par une capture d'écran réelle de votre environnement.
Ajouter une légende sous chaque image (numéro, titre et courte
explication). Les captures doivent montrer les étapes effectivement
réalisées et les résultats obtenus.
