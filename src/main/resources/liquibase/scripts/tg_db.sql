-- liquibase formatted sql
-- changeset verlonar:1
CREATE TABLE notification_task
(
                       key SERIAL PRIMARY KEY,
                       chat_id INTEGER,
                       message TEXT,
                       date_time TIMESTAMP
);