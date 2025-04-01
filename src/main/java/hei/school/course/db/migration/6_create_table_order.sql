create table orders(
    id bigserial primary key,
    order_references varchar,
    creation_date date
)