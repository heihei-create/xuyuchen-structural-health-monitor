# Structural Health Monitor

Realtime backend for structural and laboratory sensor data. It models the data path from device registration to measurement ingestion, quality checks, latest-value projection, time-window aggregation, threshold alerts and WebSocket subscriptions.

## Core modules

- Project, device, channel and device command lifecycle
- MQTT/HTTP-shaped batch decoder with device and channel validation
- Message deduplication and event-time ordering
- Sliding windows, trend downsampling and raw history queries
- Threshold alert state machine with debounce, recovery and acknowledgement
- Alert silence, notification queue and escalation policy
- Redis latest-value adapter and ClickHouse JSONEachRow sink behind configuration
- Kafka event publisher behind configuration
- STOMP WebSocket endpoint at /ws

## Run

    mvn spring-boot:run

The default profile uses in-memory repositories so the core rules can be started without external services. Compose enables Kafka, Redis and ClickHouse adapters.

    mvn package
    docker compose up --build

Register a project and a device, then create a rule before ingesting data. The main measurement endpoint is:

    POST /api/v1/projects/{projectId}/measurements

The WebSocket topics are:

    /topic/projects/{projectId}/measurements
    /topic/projects/{projectId}/alerts
