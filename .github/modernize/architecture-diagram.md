# Architecture Diagram

SupplyChain Backend is a Java 8 / Spring Boot 2.7.18 REST API for supply chain management, with MySQL for persistence and RabbitMQ for asynchronous messaging.

## Application Architecture

```mermaid
flowchart TD
    Client(["HTTP Clients\n(REST Consumers)"])

    subgraph API["REST API Layer (Spring Boot 2.7.18 / Java 8)"]
        VC["VendorController\nGET /api/vendors"]
        IC["InventoryController\nGET /api/inventory"]
        OC["PurchaseOrderController\nGET /api/orders\nPOST /api/orders"]
    end

    subgraph SVC["Business Logic Layer"]
        VS["VendorService\n(RestTemplate)"]
        IS["InventoryService\n(RabbitTemplate)"]
        OS["PurchaseOrderService\n(RabbitTemplate)"]
    end

    subgraph DAL["Data Access Layer (Spring Data JPA / Hibernate 5 / javax.persistence)"]
        VR["VendorRepository"]
        IR["InventoryRepository"]
        OR["PurchaseOrderRepository"]
    end

    subgraph STORE["Data Storage"]
        DB[("MySQL 8\njdbc:mysql://mysql:3306/supplychain\nSSL disabled, hardcoded credentials")]
    end

    subgraph MSG["Messaging"]
        MQ[["RabbitMQ\nhost: rabbitmq:5672\n3 queues: order.created,\ninventory.alert, approval.pending"]]
    end

    EXT["External Vendor Rating Service\nhttp://vendor-rating-service/api/ratings"]

    Client -->|"HTTP GET/POST/PUT"| API
    VC --> VS
    IC --> IS
    OC --> OS
    VS --> VR
    IS --> IR
    OS --> OR
    OS --> VS
    VR --> DB
    IR --> DB
    OR --> DB
    IS -->|"inventory.alert"| MQ
    OS -->|"order.created"| MQ
    VS -->|"RestTemplate HTTP GET"| EXT
```
