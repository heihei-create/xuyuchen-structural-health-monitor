# Structural Health Monitor

Realtime backend for structural and laboratory sensor data. It models the data path from device registration to measurement ingestion, quality checks, latest-value projection, time-window aggregation, threshold alerts and WebSocket subscriptions.

## Core modules

- Project, device, channel and device command lifecycle
- MQTT/HTTP-shaped batch decoder with device and channel validation
- Message deduplication and event-time ordering
- Sequence-aware alert evaluation prevents duplicate or late measurements from advancing debounce state
- Sliding windows, trend downsampling and raw history queries
- Threshold alert state machine with debounce, recovery and acknowledgement
- Alert silence, notification queue and escalation policy
- Redis latest-value adapter and ClickHouse JSONEachRow sink behind configuration
- Kafka event publisher behind configuration
- Project-scoped API tokens and admin credentials are enforced at the HTTP boundary
- STOMP WebSocket endpoint at /ws/{projectId}, bound to the authenticated project

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

Set `HEALTH_ADMIN_TOKEN` and `HEALTH_PROJECT_TOKENS=demo-project=local-project-token` for a local smoke run. Production must supply its own credentials; the default profile is fail-closed when they are absent.

WebSocket clients connect to `/ws/{projectId}` with `X-Project-Token` or an `access_token` query parameter; subscriptions are bound to that project.

The default profile is intentionally self-contained for development. Compose switches to Redis latest values, Kafka delivery and ClickHouse raw measurement storage; ClickHouse is initialized from `docker/clickhouse/init.sql` and retains raw data for 365 days.
