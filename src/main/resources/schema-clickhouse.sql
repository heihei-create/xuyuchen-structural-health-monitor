create table if not exists measurements (
  project_id String,
  device_id String,
  channel_id String,
  event_time DateTime64(3),
  sequence UInt64,
  value Float64,
  unit LowCardinality(String),
  message_id String
) engine = MergeTree
partition by toDate(event_time)
order by (project_id, device_id, channel_id, event_time, sequence);
