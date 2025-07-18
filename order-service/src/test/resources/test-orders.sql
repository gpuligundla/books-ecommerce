truncate table orders cascade;
alter sequence order_id_seq restart with 100;
alter sequence order_item_id_seq restart with 100;

insert into orders (id,order_number,username,
                    customer_name,customer_email,customer_phone,
                    delivery_address_line1,delivery_address_line2,delivery_address_city,
                    delivery_address_state,delivery_address_zip_code,delivery_address_country,
                    status,comments) values
(1, 'order-123', 'Gk', 'gk', 'gk@gmail.com', '11111111', '123 Main St', 'Apt 1', 'Clemson', 'SC', '29631', 'USA', 'NEW', null),
(2, 'order-456', 'Gk', 'puligundla', 'balu@gmail.com', '2222222', '345 street', 'Apt 1', 'Atlanta', 'GA', '400073', 'USA', 'NEW', null)
;

insert into order_items(order_id, code, name, price, quantity) values
(1, 'P100', 'The Hunger Games', 34.0, 2),
(1, 'P101', 'To Kill a Mockingbird', 45.40, 1),
(2, 'P102', 'The Chronicles of Narnia', 44.50, 1)
;