drop table if exists cars;
drop table if exists checkUps;

create table cars(
    id bigserial primary key,
    date date,
    manufacturer varchar(50),
    model varchar(50),
    productionYear int,
    vin varchar(50)
);
create table checkUps(
    id bigserial primary key,
    performedAt timestamp,
    worker varchar(50),
    price int,
    carId bigint,
    foreign key(carId) references cars(id)
);

