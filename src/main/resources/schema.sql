create table if not exists currencies (
    id       serial primary key,
    code     varchar(10) unique not null,
    fullname varchar(100) not null,
    sign     varchar(5) not null
);

create table if not exists exchange_rates (
    id serial primary key,
    base_currency_id int not null references currencies(id),
    target_currency_id int not null references currencies(id),
    rate decimal not null
);
