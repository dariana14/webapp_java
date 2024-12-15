DROP TABLE IF EXISTS order_rows;
DROP TABLE IF EXISTS orders;
DROP SEQUENCE IF EXISTS seq1;

CREATE SEQUENCE seq1 AS INTEGER START WITH 1;

CREATE TABLE orders (
    id BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('seq1'),
    order_number VARCHAR(255)
);

CREATE TABLE order_rows (
    itemname VARCHAR(255),
    price INT,
    quantity INT,
    orders_id BIGINT,
    FOREIGN KEY (orders_id)
        REFERENCES orders(id) ON DELETE CASCADE
);

