insert into order_status (id_order, status, creation_date) values
(3, 'CREATED', '2025-02-25T10:00:00')
on conflict do nothing;

