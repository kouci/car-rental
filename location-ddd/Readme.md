# Structure du projet (DDD + Clean Architecture + Hexagonale)

```
com/
└── check_location/
    └── location_ddd/
        ├── application/
        │   └── service/
        │       ├── RentalService.java
        │       ├── CarService.java
        │       └── ClientService.java
        ├── domain/
        │   ├── enums/
        │   │   └── CarStatus.java
        │   └── model/
        │       ├── Car.java
        │       ├── Client.java
        │       └── Rental.java
        ├── port/
        │   └── out/
        │       ├── CarRepository.java      🅸
        │       ├── ClientRepository.java   🅸
        │       └── RentalRepository.java  🅸
        └── adapter/
            ├── out/
            │   └── persistence/
            │       ├── CarRepositoryImpl.java
            │       ├── ClientRepositoryImpl.java
            │       └── RentalRepositoryImpl.java
            └── in/
                └── web/
                    ├── RentalController.java
                    ├── CarController.java
                    └── ClientController.java
```

**Légende** :
- `domain/` : Cœur métier avec entités et énumérations
- `application/` : Cas d'utilisation et services
- `port/` : Interfaces pour les dépendances externes
- `adapter/` : Implémentations concrètes (web et persistence)