CREATE DATABASE IF NOT EXISTS health;

CREATE TABLE IF NOT EXISTS health.measurements
(
    project_id String,
    device_id String,
    channel_id String,
    event_time DateTime64(3, 'UTC'),
    sequence UInt64,
    value Float64,
    unit String,
    message_id String,
    ingested_at DateTime64(3, 'UTC') DEFAULT now64(3)
)
ENGINE = ReplacingMergeTree(ingested_at)
PARTITION BY toYYYYMM(event_time)
ORDER BY (project_id, device_id, channel_id, event_time, sequence, message_id)
TTL event_time + INTERVAL 365 DAY;
