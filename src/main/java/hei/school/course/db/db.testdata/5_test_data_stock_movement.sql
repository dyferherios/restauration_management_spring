insert into stock_movement (quantity, unit, movement_type, creation_datetime, id_ingredient)
values (100, 'U', 'IN', '2025-02-01T05:00:00', 1),
       (50, 'U', 'IN', '2025-02-01T05:00:00', 4),
       (10000, 'G', 'IN', '2025-02-01T05:00:00', 2),
       (20, 'L', 'IN', '2025-02-01T05:00:00', 3),
       (10, 'U', 'OUT', '2025-02-10T07:00:00', 1),
       (10, 'U', 'OUT', '2025-02-03T12:00:00', 1),
       (20, 'U', 'OUT', '2025-02-20T12:00:00', 4)
on conflict do nothing;