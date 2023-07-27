CREATE TABLE manufacturer_model(
    id UUID PRIMARY KEY,
    manufacturer VARCHAR (50),
    model VARCHAR (50)
);

CREATE TABLE cars (
    id UUID PRIMARY KEY,
    date DATE NOT NULL,
    car_details UUID NOT NULL,
    production_year INTEGER NOT NULL,
    vin VARCHAR(17) NOT NULL,
    CONSTRAINT fk_car_carDetails FOREIGN KEY (car_details) REFERENCES manufacturer_model(id) ON DELETE SET NULL
);
CREATE TABLE checkUps (
    id UUID PRIMARY KEY,
    performed_At TIMESTAMP NOT NULL,
    worker VARCHAR(255) NOT NULL,
    price INTEGER NOT NULL,
    car_id UUID NOT NULL,
    CONSTRAINT fk_car_checkup FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
);

