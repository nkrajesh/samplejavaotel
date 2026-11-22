# OTel distributed tracing demo

`POST /orders` → **order-service** (saves to Postgres, publishes to Kafka `orders`) → **notification-service** (consumes, logs).
Tracing uses the **OpenTelemetry Java agent** (zero code), so the trace flows HTTP → JDBC → Kafka producer → Kafka consumer.

## 1. Start infra
```
docker compose up -d
```

## 2. Download the OTel Java agent (once)
```
curl -L -o opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar
```

## 3. Build
```
mvn clean package
```

## 4. Run both services (two terminals)
```
java -javaagent:opentelemetry-javaagent.jar -Dotel.service.name=order-service -Dotel.metrics.exporter=none -Dotel.logs.exporter=none -jar order-service/target/order-service-1.0.jar

java -javaagent:opentelemetry-javaagent.jar -Dotel.service.name=notification-service -Dotel.metrics.exporter=none -Dotel.logs.exporter=none -jar notification-service/target/notification-service-1.0.jar
```
The agent sends traces to `http://localhost:4318` (collector) by default.

## 5. Send a request
```
curl -X POST localhost:8080/orders -H "Content-Type: application/json" -d "{\"item\":\"book\",\"qty\":2}"
```

## 6. View the trace
Open http://localhost:16686, pick `order-service`, Find Traces. You'll see one trace spanning both services.
