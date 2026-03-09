-- V2__CreateBookingTable.sql
CREATE TABLE booking (
    booking_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES customer(customer_id),
    event_type VARCHAR(255),
    event_date DATE,
    location VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    photographer_name VARCHAR(255)
);

-- Indexes
CREATE INDEX idx_booking_customer_id ON booking(customer_id);
CREATE INDEX idx_booking_event_date ON booking(event_date);
CREATE INDEX idx_booking_status ON booking(status);
