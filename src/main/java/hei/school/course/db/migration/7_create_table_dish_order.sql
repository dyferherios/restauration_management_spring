create table dish_order(
    id bigserial primary key,
    id_order bigint,
    id_dish bigint,
    quantity numeric,
    constraint fk_stock_movement_id_order foreign key (id_order) references orders (id),
    constraint fk_stock_movement_id_dish foreign key (id_dish) references dish (id)
);

ALTER TABLE dish_order
ADD CONSTRAINT unique_dish_order UNIQUE (id_order, id_dish);