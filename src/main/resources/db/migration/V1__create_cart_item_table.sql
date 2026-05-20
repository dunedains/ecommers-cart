CREATE TABLE cart_item (
    id         BIGINT  GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT  NOT NULL,
    product_id BIGINT  NOT NULL,
    quantity   INTEGER DEFAULT 1 NOT NULL,
    CONSTRAINT uq_cart_user_product UNIQUE (user_id, product_id)
);
