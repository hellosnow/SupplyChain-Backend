# SupplyChain Backend API

Legacy Supply Chain Management System - Backend REST API

## 🚨 Technical Debt Summary

This is an intentionally **legacy application** designed to demonstrate modernization scenarios. It contains the following technical debt that violates enterprise guardrails:

### Prohibited Technologies
- ✅ **Java 8** (EOL) upgraded to **Java 25**
- ✅ **Spring Boot 2.7.18** upgraded to **Spring Boot 4.0+**
- ❌ **RestTemplate** (bypasses mesh) → Should use **ServiceMesh SDK** (`com.acme.mesh.ServiceMesh`)
- ❌ **SLF4J logging** (no trace context) → Should use **InternalLogger** (`com.acme.logging.InternalLogger`)
- ❌ **RabbitMQ 3.6 client** → Should migrate to **Azure Service Bus** with custom messaging API

### Prohibited Patterns
- ❌ **Exception-based flow control** → Should use **Result<T> pattern** (`com.acme.commons.Result`)
- ❌ **Hardcoded credentials** in `application.yml` → Should use **Azure Key Vault**

### Missing Requirements
- ✅ Container base image updated to `mcr.microsoft.com/openjdk/jdk:25-ubuntu` (build) and `mcr.microsoft.com/openjdk/jdk:25-distroless` (runtime)

---

## 🏗️ Architecture

```
┌─────────────────────┐
│   REST Controllers  │
│  /api/orders        │
│  /api/inventory     │
│  /api/vendors       │
└──────────┬──────────┘
           │
┌──────────▼──────────┐
│   Service Layer     │
│  - PurchaseOrder    │
│  - Inventory        │
│  - Vendor           │
└────┬────────┬───────┘
     │        │
     ▼        ▼
  MySQL    RabbitMQ
           (sends messages)
```

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- Or: Java 25, Maven, MySQL, RabbitMQ

### Run with Docker Compose

```bash
# Start all dependencies (MySQL + RabbitMQ + Backend)
cd ../SupplyChain-Demo-Orchestration
docker-compose up --build

# Or run backend standalone (requires MySQL + RabbitMQ running)
docker build -t supplychain-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/supplychain \
  -e SPRING_RABBITMQ_HOST=rabbitmq \
  supplychain-backend
```

### Run Locally (Development)

```bash
# Start MySQL
docker run -d --name mysql \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=supplychain \
  -p 3306:3306 mysql:5.7

# Start RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.6-management

# Build and run
mvn clean package
java -jar target/supplychain-backend-1.0.0-LEGACY.jar
```

---

## 📡 API Endpoints

### Purchase Orders
```
GET    /api/orders             # List all orders
GET    /api/orders/{orderNumber}  # Get order by number
GET    /api/orders/pending     # Get pending orders
POST   /api/orders             # Create new order
PUT    /api/orders/{orderNumber}/approve  # Approve order
```

### Inventory
```
GET    /api/inventory          # List all inventory
GET    /api/inventory/{sku}    # Get inventory by SKU
GET    /api/inventory/low-stock  # Get low stock items
```

### Vendors
```
GET    /api/vendors            # List all vendors
GET    /api/vendors/{vendorCode}  # Get vendor by code
GET    /api/vendors/{vendorCode}/rating  # Get vendor rating (uses RestTemplate)
```

---

## 🐛 Known Issues (Tech Debt)

### 1. Hardcoded Credentials
**Location:** `src/main/resources/application.yml`
```yaml
spring:
  datasource:
    username: root
    password: root  # ❌ Hardcoded
  rabbitmq:
    username: guest
    password: guest  # ❌ Hardcoded
```

**Fix:** Migrate to Azure Key Vault

---

### 2. RestTemplate Usage
**Location:** `VendorService.getVendorRatingFromExternalService()`
```java
// ❌ RestTemplate bypasses mesh layer
Double rating = restTemplate.getForObject(url, Double.class);
```

**Fix:** Replace with ServiceMesh SDK
```java
// ✅ Should use ServiceMesh SDK
Double rating = serviceMesh.get("vendor-rating-service")
    .path("/api/ratings/" + vendorCode)
    .retrieve(Double.class);
```

---

### 3. Exception-Based Flow Control
**Location:** `PurchaseOrderService.createOrder()`
```java
// ❌ Throwing exceptions for business logic
if (order.getTotalAmount().doubleValue() <= 0) {
    throw new IllegalArgumentException("Order amount must be greater than zero");
}
```

**Fix:** Use Result<T> pattern
```java
// ✅ Should use Result<T>
if (order.getTotalAmount().doubleValue() <= 0) {
    return Result.error("Order amount must be greater than zero");
}
```

---

### 4. RabbitMQ Direct Usage
**Location:** `PurchaseOrderService.createOrder()`
```java
// ❌ RabbitMQ client (should use custom messaging API)
rabbitTemplate.convertAndSend(orderCreatedQueue, savedOrder);
```

**Fix:** Migrate to custom messaging API (Azure Service Bus)
```java
// ✅ Should use custom messaging API
messagingClient.publish("order.created", savedOrder, 
    MessageOptions.withPriority(Priority.HIGH));
```

---

## 🔄 Messaging (RabbitMQ)

### Queues
- `order.created` - Published when a new order is created
- `inventory.alert` - Published when inventory is low
- `approval.pending` - Published when order needs approval

### View Messages
RabbitMQ Management UI: http://localhost:15672 (guest/guest)

---

## 🗄️ Database Schema

### Tables
- `purchase_orders` - Purchase order records
- `inventory` - Inventory items with stock levels
- `vendors` - Vendor information

---

## 🧪 Testing

```bash
# Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "PO-2024-001",
    "vendorId": 1,
    "totalAmount": 15000.00,
    "requestedBy": "john.doe",
    "description": "Office supplies Q1 2024"
  }'

# Get pending orders
curl http://localhost:8080/api/orders/pending

# Get low stock items
curl http://localhost:8080/api/inventory/low-stock
```

---

## 📦 Dependencies

- Spring Boot 4.0.0
- Java 25
- MySQL 8.0.33
- RabbitMQ 3.6.6 (❌ old version)
- Hibernate 4.x (via Spring Boot)

---

## 🎯 Modernization Roadmap

1. **Framework Upgrade** (Act 2)
   - Java 8 → Java 25 ✅
   - Spring Boot 2.7 → Spring Boot 4.0 ✅

2. **Replace Prohibited Libraries** (Act 3)
   - RestTemplate → ServiceMesh SDK
   - SLF4J → InternalLogger
   - Hardcoded credentials → Azure Key Vault

3. **Migrate Messaging** (Act 4)
   - RabbitMQ → Custom Messaging API (Azure Service Bus)

4. **Fix Patterns** (Act 3)
   - Exception flow → Result<T> pattern

5. **Infrastructure** (Act 5)
   - Generate Bicep for Azure Container Apps
   - Generate Bicep for Azure Service Bus
   - Generate Bicep for Azure SQL
   - Update Dockerfile with compliant base images

---

## 📝 License

For demonstration purposes only.
