
# Seata Sample

## Setup

#### Infra components
Go to *infra* folder:

    docker compose build --no-cache seata-server

    docker compose up

This will start:

- MySQL (with the tables needed for Seata Server).
- Seata Server.
- Grafana, Prometheus and Tempo for observability.

#### Microservices
Foe each microservice folder (application-manager, credit-api and shipping-api) run:

    ./gradlew bootRun

This will start:

- microservice
- the dedicated db (MariaDB for credit-api and PostgreSQL for shipping-api)

#### Observability
- Grafana: http://localhost:3000
  - Spring Boot dashboard: http://localhost:3000/dashboards
  - Prometheus & Tempo: http://localhost:3000/explore

#### OpenAPI
Microservices OpenAPI:
- application-manager: http://localhost:8080/swagger-ui/index.html
- credit-api: http://localhost:8081/swagger-ui/index.html
- shipping-api: http://localhost:8082/swagger-ui/index.html
