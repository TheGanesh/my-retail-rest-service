CREATE KEYSPACE IF NOT EXISTS my_retail WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
CREATE TABLE IF NOT EXISTS my_retail.product_price (
product_id    bigint,
product_name  TEXT,
current_price DECIMAL,
currency_code TEXT,
updated_time  timestamp,
PRIMARY KEY ((product_id,currency_code))
);

INSERT INTO my_retail.product_price(product_id, product_name,current_price,currency_code,updated_time) VALUES(13860428, 'The Big Lebowski (Blu-ray) (Widescreen)',10.11,'USD',dateof(now()));
INSERT INTO my_retail.product_price(product_id, product_name,current_price,currency_code,updated_time) VALUES(1, 'The Small Lebowski (Blu-ray) (Widescreen)',10.11,'USD',dateof(now()));