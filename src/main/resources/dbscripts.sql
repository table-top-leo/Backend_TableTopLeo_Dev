

CREATE TABLE IF NOT EXISTS tabletop_leo_customer_sessions (
    id                BIGSERIAL       PRIMARY KEY,
    session_id        VARCHAR(50)     NOT NULL UNIQUE,
    admin_id          VARCHAR(50)     NOT NULL,
    business_id       VARCHAR(50)     NOT NULL,
    order_type        VARCHAR(20),
    customer_name     VARCHAR(150),
    customer_phone    VARCHAR(20),
    customer_email    VARCHAR(255),
    table_number      VARCHAR(20),
    session_status    VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    expires_at        TIMESTAMP       NOT NULL,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_cs_session_id   ON tabletop_leo_customer_sessions(session_id);
CREATE INDEX IF NOT EXISTS idx_cs_business_id  ON tabletop_leo_customer_sessions(business_id);
CREATE INDEX IF NOT EXISTS idx_cs_admin_id     ON tabletop_leo_customer_sessions(admin_id);

-- 2. Orders
CREATE TABLE IF NOT EXISTS tabletop_leo_orders (
    id                BIGSERIAL       PRIMARY KEY,
    order_id          VARCHAR(50)     NOT NULL UNIQUE,
    order_number      VARCHAR(30)     NOT NULL,
    session_id        VARCHAR(50)     NOT NULL,
    admin_id          VARCHAR(50)     NOT NULL,
    business_id       VARCHAR(50)     NOT NULL,
    order_type        VARCHAR(20)     NOT NULL,
    table_number      VARCHAR(20),
    customer_name     VARCHAR(150),
    customer_phone    VARCHAR(20),
    customer_note     TEXT,
    subtotal          DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    tax_amount        DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    discount_amount   DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    grand_total       DECIMAL(10,2)   NOT NULL DEFAULT 0.00,
    payment_status    VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    order_status      VARCHAR(20)     NOT NULL DEFAULT 'PLACED',
    payment_method    VARCHAR(30),
    estimated_minutes INTEGER,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_ord_order_id      ON tabletop_leo_orders(order_id);
CREATE INDEX IF NOT EXISTS idx_ord_admin_biz     ON tabletop_leo_orders(admin_id, business_id);
CREATE INDEX IF NOT EXISTS idx_ord_status        ON tabletop_leo_orders(order_status, payment_status);
CREATE INDEX IF NOT EXISTS idx_ord_created       ON tabletop_leo_orders(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_ord_session       ON tabletop_leo_orders(session_id);

-- 3. Order Items (snapshot — stores name/price/image at order time)
CREATE TABLE IF NOT EXISTS tabletop_leo_order_items (
    id                BIGSERIAL       PRIMARY KEY,
    item_id           VARCHAR(50)     NOT NULL UNIQUE,
    order_id          VARCHAR(50)     NOT NULL,
    admin_id          VARCHAR(50)     NOT NULL,
    business_id       VARCHAR(50)     NOT NULL,
    product_id        BIGINT          NOT NULL,
    product_name      VARCHAR(255)    NOT NULL,
    product_description TEXT,
    product_image_url TEXT,
    category_name     VARCHAR(150),
    unit_price        DECIMAL(10,2)   NOT NULL,
    quantity          INTEGER         NOT NULL DEFAULT 1,
    line_total        DECIMAL(10,2)   NOT NULL,
    special_request   TEXT,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_oi_order_id   ON tabletop_leo_order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_oi_admin_biz  ON tabletop_leo_order_items(admin_id, business_id);
CREATE INDEX IF NOT EXISTS idx_oi_product_id ON tabletop_leo_order_items(product_id);

-- 4. Payments
CREATE TABLE IF NOT EXISTS tabletop_leo_order_payments (
    id                BIGSERIAL       PRIMARY KEY,
    payment_id        VARCHAR(50)     NOT NULL UNIQUE,
    order_id          VARCHAR(50)     NOT NULL,
    admin_id          VARCHAR(50)     NOT NULL,
    business_id       VARCHAR(50)     NOT NULL,
    gateway_name      VARCHAR(30)     NOT NULL,
    transaction_id    VARCHAR(300),
    payment_reference VARCHAR(300),
    paid_amount       DECIMAL(10,2)   NOT NULL,
    currency          VARCHAR(10)     NOT NULL DEFAULT 'INR',
    payment_status    VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    gateway_response  TEXT,
    failure_reason    VARCHAR(500),
    initiated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    completed_at      TIMESTAMP,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_pay_order_id      ON tabletop_leo_order_payments(order_id);
CREATE INDEX IF NOT EXISTS idx_pay_admin_biz     ON tabletop_leo_order_payments(admin_id, business_id);
CREATE INDEX IF NOT EXISTS idx_pay_transaction   ON tabletop_leo_order_payments(transaction_id);
CREATE INDEX IF NOT EXISTS idx_pay_status        ON tabletop_leo_order_payments(payment_status);

-- Order number sequence per business (optional helper)
CREATE SEQUENCE IF NOT EXISTS tabletop_leo_order_seq START 1000 INCREMENT 1;





-------New alter columns************************************************************************
select * from tabletop_leo_payment_configurations


ALTER TABLE tabletop_leo_orders
    ADD COLUMN IF NOT EXISTS pay_at_counter BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE tabletop_leo_payment_configurations
    ADD COLUMN IF NOT EXISTS pay_at_counter_enabled BOOLEAN NOT NULL DEFAULT FALSE;

SELECT column_name, data_type, column_default
FROM information_schema.columns
WHERE table_name = 'tabletop_leo_orders'
  AND column_name = 'pay_at_counter';

SELECT column_name, data_type, column_default
FROM information_schema.columns
WHERE table_name = 'tabletop_leo_payment_configurations'
  AND column_name = 'pay_at_counter_enabled';


-------New alter columns (Invoice Email feature) ***********************************************
-- customer_email is now the single source of truth on tabletop_leo_customer_sessions.
-- Drop the duplicate column from tabletop_leo_orders (safe / idempotent — run once).
ALTER TABLE tabletop_leo_orders
    DROP COLUMN IF EXISTS customer_email;

-- Sanity check: confirm the column is gone from orders and still present on sessions.
SELECT column_name FROM information_schema.columns
WHERE table_name = 'tabletop_leo_orders' AND column_name = 'customer_email';

SELECT column_name FROM information_schema.columns
WHERE table_name = 'tabletop_leo_customer_sessions' AND column_name = 'customer_email';

-------New tables (Reviews feature — Task 2) *********************************************
-- These are auto-created by Hibernate (spring.jpa.hibernate.ddl-auto=update) on startup,
-- same as tabletop_leo_business_information. Listed here for reference only.

-- 5. Customer -> Business/Merchant reviews (no order link; write-once per business+phone)
CREATE TABLE IF NOT EXISTS tabletop_leo_business_reviews (
    id                BIGSERIAL       PRIMARY KEY,
    review_id         VARCHAR(50)     NOT NULL UNIQUE,
    business_id       VARCHAR(50)     NOT NULL,
    admin_id          VARCHAR(50)     NOT NULL,
    customer_name     VARCHAR(150),
    customer_phone    VARCHAR(20)     NOT NULL,
    rating            INTEGER         NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text       TEXT,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_business_review_phone UNIQUE (business_id, customer_phone)
);
CREATE INDEX IF NOT EXISTS idx_brev_business_id ON tabletop_leo_business_reviews(business_id);
CREATE INDEX IF NOT EXISTS idx_brev_admin_id    ON tabletop_leo_business_reviews(admin_id);

-- 6. Merchant/Admin -> Application reviews (write-once per admin)
CREATE TABLE IF NOT EXISTS tabletop_leo_app_reviews (
    id                BIGSERIAL       PRIMARY KEY,
    review_id         VARCHAR(50)     NOT NULL UNIQUE,
    admin_id          VARCHAR(50)     NOT NULL UNIQUE,
    business_id       VARCHAR(50),
    rating            INTEGER         NOT NULL CHECK (rating BETWEEN 1 AND 5),
    review_text       TEXT,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_arev_admin_id ON tabletop_leo_app_reviews(admin_id);
