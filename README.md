## How to Run the Project

Follow these steps to build and run the application along with the PostgreSQL database.

### Prerequisites

Make sure you have the following installed on your local machine:
* Java 21 (JDK)
* Maven
* Docker & Docker Compose

### Build and Run

**Step 1: Build the application**
First, open your terminal in the project's root directory and build the Java executable `.jar` file using Maven:

```bash
mvn clean package
```
**Step 2: Start the containers**
Once the build is successful and the app.jar file is generated in the target/ directory, start the Docker containers:
```bash
DB_PORT=5432 DB_NAME=device DB_USER=device DB_PASSWORD=device docker compose up -d --build
```

Useful Commands
View application logs:
```bash
docker compose logs -f device-app
```
Stop and remove containers:
```bash
docker compose down
```

## Future Architectural Improvements

### Horizontal Scaling & High Availability
The current single-instance deployment represents a **Single Point of Failure (SPOF)**. To ensure system resilience and handle increased traffic, the following steps are planned:
* **Multi-Instance Deployment**: Scaling the application horyzontally (e.g., via Kubernetes Replicas) across multiple Availability Zones to ensure **High Availability (HA)**.
* **Stateless Architecture**: Moving from local synchronization (like `ReentrantLock`) to a fully stateless model where any instance can process any request for any `deviceId`.
* **Load Balancing**: Implementing a Load Balancer with health checks to distribute traffic evenly and take failing instances out of rotation.

### Concurrency & Data Consistency
The current implementation of `DeviceService.updateDevice` uses a local `ReentrantLock` combined with a `ConcurrentHashMap` to ensure thread-safety. While effective for a single-node setup, the following improvements are recommended for production scaling:

* **Distributed Locking**: To support horizontal scaling (multiple application instances/replicas), replace the local lock with a distributed mechanism like **Redis (Redisson)** or **ShedLock**.
* **Optimistic Locking (@Version)**: Implement version-based conflict detection by adding a `@Version` field to `DeviceEntity`. This increases throughput and offloads locking logic to the database.
* **Retry Mechanism**: Use **Spring Retry** or **Resilience4j** to automatically re-invoke the update logic upon `OptimisticLockingFailureException`.

### Performance & Caching
* **Pagination & Filtering**:
  * *Current State:* List operations currently return all records, which is a performance risk as the database grows.
  * *Recommendation:* Implement **Pagination**  This prevents high memory consumption (OOM) and reduces network payload size.
* **Read-Through Caching**: Implement a caching layer (e.g., **Redis** or **Caffeine**) for `find` operations to reduce database load.
* **Read/Write Splitting**: Leverage database read-replicas for non-locking queries to further scale read-heavy workloads.

### System Stability & Resilience
* **Circuit Breaker Pattern**: Integrate **Resilience4j Circuit Breaker** to protect the system from cascading failures. If the database becomes unresponsive, the circuit will "open," allowing the system to fail fast and recover.
* **Rate Limiting**: Implement rate limiting per `deviceId` or per user to prevent API abuse and resource exhaustion.

### Observability & Metrics
* **Micrometer Metrics**: Export custom metrics to **Prometheus** and visualize them in **Grafana**.
* **Distributed Tracing**: Use **Spring Cloud Sleuth / Micrometer Tracing** with **Zipkin** or **Jaeger** to identify bottlenecks across service boundaries.
* **Structured Logging**: Enhance debugging with correlation IDs and JSON-formatted logs for centralized management.

### Security & Authorization
The application is designed to be fully **Stateless**, ensuring compatibility with horizontal scaling and modern cloud environments.

* **Recommended: JWT (JSON Web Token) Implementation**:
    * It is **highly recommended** to implement JWT for securing device management endpoints.
    * *Why JWT?* In a multi-instance environment, JWT allows any application replica to verify a user's identity and roles without needing to query a central session database or a specific server node.
    * *Validation:* Access should be controlled via the `Authorization: Bearer <token>` header, using public-key cryptography for secure, decentralized verification.
  
### CI/CD & Security Automation
To ensure the reliability and security of the system throughout the Software Development Life Cycle (SDLC), the following automation pipeline is implemented/recommended:

* **Dependency-Track & SCA**: Integration with **OWASP Dependency-Track** to continuously monitor third-party libraries for known vulnerabilities (CVEs).
* **Static Application Security Testing (SAST)**: Use tools like **SonarQube** or **Snyk** to scan the source code for security vulnerabilities, such as hardcoded secrets, SQL injection risks.
* **Automated Integration Testing**: Run tests in the CI pipeline using **Testcontainers**. This validates that the logic boundaries hold up before any code is merged.
* **Database Migration Safety**: Automated validation of **Liquibase** scripts
