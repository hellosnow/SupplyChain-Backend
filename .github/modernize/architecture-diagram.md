# Architecture Diagram

This diagram represents the current architecture of the SupplyChain Backend application, a Spring Boot 2.7.18 / Java 8 monolithic REST API with MySQL persistence and RabbitMQ messaging.

## Application Architecture

```mermaid
flowchart TD
    Client["HTTP Client\nREST Consumer"]

    subgraph App["SupplyChain Backend - Spring Boot 2.7.18 / Java 8"]
        subgraph API["REST API Layer"]
            IC["InventoryController\nGET/POST /api/inventory"]
            VC["VendorController\nGET/POST /api/vendors"]
            POC["PurchaseOrderController\nGET/POST /api/orders"]
        end

        subgraph SVC["Service Layer"]
            IS["InventoryService\nLombok, SLF4J"]
            VS["VendorService\nLombok, SLF4J"]
            POS["PurchaseOrderService\nLombok, SLF4J, Transactional"]
        end

        subgraph DAL["Data Access Layer - Spring Data JPA / Hibernate"]
            IR["InventoryRepository\nJpaRepository"]
            VR["VendorRepository\nJpaRepository"]
            POR["PurchaseOrderRepository\nJpaRepository"]
        end

        subgraph MODELS["Domain Model - javax.persistence"]
            IM["Inventory"]
            VM["Vendor"]
            POM["PurchaseOrder"]
        end

        subgraph CONFIG["Configuration"]
            AC["AppConfig\nRestTemplate, RabbitTemplate\nQueue Declarations"]
        end
    end

    subgraph INFRA["Infrastructure"]
        DB[("MySQL 8\nPort 3306\nmysql:supplychain")]
        MQ["RabbitMQ\nPort 5672\norder.created queue\ninventory.alert queue\napproval.pending queue"]
    end

    ExtAPI["External REST API\nvia RestTemplate"]

    Client -->|HTTP REST calls| IC
    Client -->|HTTP REST calls| VC
    Client -->|HTTP REST calls| POC

    IC --> IS
    VC --> VS
    POC --> POS

    IS --> IR
    VS --> VR
    POS --> POR
    POS --> VS

    IR --> DAL
    VR --> DAL
    POR --> DAL

    IR -.->|JPA/Hibernate ORM| IM
    VR -.->|JPA/Hibernate ORM| VM
    POR -.->|JPA/Hibernate ORM| POM

    DAL -->|JDBC - mysql-connector-java 8.0.33| DB

    POS -->|publish order.created| MQ
    IS -->|publish inventory.alert| MQ
    POS -->|publish approval.pending| MQ

    AC -->|configures| MQ
    AC -->|provides RestTemplate| SVC
    SVC -->|HTTP calls| ExtAPI
```
