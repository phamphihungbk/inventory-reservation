CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    venue VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    event_date TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ticket_types (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES events(id),
    name VARCHAR(255) NOT NULL,
    price NUMERIC(12, 2) NOT NULL CHECK (price >= 0),
    remaining_quantity INTEGER NOT NULL CHECK (remaining_quantity >= 0),
    version BIGINT NOT NULL DEFAULT 0
);

INSERT INTO events (id, name, description, venue, city, country, event_date, created_at)
SELECT
    id,
    name,
    'Migrated event from inventory product',
    'Main Arena',
    'Vienna',
    'Austria',
    created_at + INTERVAL '30 days',
    created_at
FROM products;

SELECT setval(
    pg_get_serial_sequence('events', 'id'),
    COALESCE((SELECT MAX(id) FROM events), 1),
    (SELECT COUNT(*) > 0 FROM events)
);

INSERT INTO ticket_types (id, event_id, name, price, remaining_quantity, version)
SELECT
    id,
    id,
    'General Admission',
    price,
    stock,
    version
FROM products;

SELECT setval(
    pg_get_serial_sequence('ticket_types', 'id'),
    COALESCE((SELECT MAX(id) FROM ticket_types), 1),
    (SELECT COUNT(*) > 0 FROM ticket_types)
);

ALTER TABLE reservations ADD COLUMN ticket_type_id BIGINT;
UPDATE reservations SET ticket_type_id = product_id;
ALTER TABLE reservations ALTER COLUMN ticket_type_id SET NOT NULL;
ALTER TABLE reservations ADD CONSTRAINT fk_reservations_ticket_type_id FOREIGN KEY (ticket_type_id) REFERENCES ticket_types(id);
UPDATE reservations SET status = 'CANCELLED' WHERE status = 'CANCELED';
ALTER TABLE reservations DROP CONSTRAINT IF EXISTS reservations_product_id_fkey;
DROP INDEX IF EXISTS idx_reservations_product_id;
ALTER TABLE reservations DROP COLUMN product_id;

CREATE INDEX idx_ticket_types_event_id ON ticket_types(event_id);
CREATE INDEX idx_reservations_ticket_type_id ON reservations(ticket_type_id);

CREATE INDEX idx_events_fts ON events USING GIN (
    to_tsvector('simple', coalesce(name, '') || ' ' || coalesce(venue, '') || ' ' || coalesce(city, '') || ' ' || coalesce(country, ''))
);
CREATE INDEX idx_events_name_trgm ON events USING GIN (name gin_trgm_ops);
CREATE INDEX idx_events_venue_trgm ON events USING GIN (venue gin_trgm_ops);
CREATE INDEX idx_events_city_trgm ON events USING GIN (city gin_trgm_ops);
CREATE INDEX idx_events_country_trgm ON events USING GIN (country gin_trgm_ops);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE REFERENCES reservations(id),
    status VARCHAR(32) NOT NULL,
    provider VARCHAR(64) NOT NULL,
    provider_reference VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL UNIQUE REFERENCES reservations(id),
    total_amount NUMERIC(12, 2) NOT NULL CHECK (total_amount >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE products;
