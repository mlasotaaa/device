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