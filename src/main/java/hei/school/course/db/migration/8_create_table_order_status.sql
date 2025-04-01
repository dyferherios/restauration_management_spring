do
$$
    begin
        if not exists(select from pg_type where typname = 'order_status_process') then
            create type order_status_process as enum ('CREATED', 'CONFIRMED', 'INPROGRESS', 'FINISHED', 'DELIVERED');
        end if;
    end
$$;

create table order_status(
    id bigserial primary key,
    id_order bigint,
    status order_status_process,
    creation_date timestamp
);

ALTER TABLE order_status
ADD CONSTRAINT unique_order_status UNIQUE (id_order, status);

ALTER TABLE order_status
ADD CONSTRAINT unique_id_status UNIQUE (id, status);

INSERT INTO dish_order (id, id_order, id_dish, quantity)
VALUES (?, ?, ?, ?)
ON CONFLICT (id_order, id_dish) DO UPDATE
    SET quantity = EXCLUDED.quantity
    WHERE dish_order.id_order = EXCLUDED.id_order
    AND dish_order.id_dish = EXCLUDED.id_dish

