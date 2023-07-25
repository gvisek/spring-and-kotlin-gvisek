drop table if exists cars;
drop table if exists checkUps;

CREATE TABLE cars (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    manufacturer VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    production_year INTEGER NOT NULL,
    vin VARCHAR(17) NOT NULL
);
CREATE TABLE checkUps (
    id UUID PRIMARY KEY,
    performed_At TIMESTAMP NOT NULL,
    worker VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    car_id UUID NOT NULL,
    CONSTRAINT fk_car_checkup FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
);

