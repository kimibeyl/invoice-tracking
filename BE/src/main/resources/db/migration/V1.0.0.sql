CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists billing_account
(
    billing_acc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name           VARCHAR(255),
    address        VARCHAR(255),
    phone          VARCHAR(20),
    email          VARCHAR(255),
    created_at     BIGINT NOT NULL,
    created_by     VARCHAR(255) NOT NULL,
    modified_at    BIGINT    NOT NULL,
    modified_by    VARCHAR(255) NOT NULL
);

CREATE TABLE if not exists invoice
(
    invoice_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    billing_acc_id UUID NOT NULL,
    invoice_number VARCHAR(100),
    status         VARCHAR(20),
    invoice_date   BIGINT    NOT NULL,
    amount         NUMERIC(18, 2),
    vat_amount     NUMERIC(18, 2),
    created_at     BIGINT    NOT NULL,
    created_by     VARCHAR(255) NOT NULL,
    modified_at    BIGINT    NOT NULL,
    modified_by    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_invoice_billing_account
        FOREIGN KEY (billing_acc_id)
            REFERENCES billing_account (billing_acc_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

CREATE TABLE notification
(
    notification_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type           VARCHAR(20),
    message        VARCHAR(255),
    status        VARCHAR(20),
    invoice_id    UUID NOT NULL,
    created_at     BIGINT NOT NULL,
    created_by     VARCHAR(255) NOT NULL,
    modified_at    BIGINT    NOT NULL,
    modified_by    VARCHAR(255) NOT NULL,
    CONSTRAINT fk_notification_invoice
        FOREIGN KEY (invoice_id)
            REFERENCES invoice (invoice_id)
            ON UPDATE CASCADE
            ON DELETE RESTRICT
);

start transaction;
-- 5 Billing Accounts
INSERT INTO billing_account (billing_acc_id, name, address, phone, email, created_at, created_by, modified_at, modified_by)
VALUES
    (gen_random_uuid(), 'Acme Corp', '123 Main St', '555-1000', 'contact@acme.com', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system'),
    (gen_random_uuid(), 'Beta Industries', '45 Industrial Rd', '555-2000', 'info@beta.com', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system'),
    (gen_random_uuid(), 'Gamma Solutions', '789 Sunset Blvd', '555-3000', 'support@gamma.com', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system'),
    (gen_random_uuid(), 'Delta Services', '22 Ocean Ave', '555-4000', 'office@delta.com', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system'),
    (gen_random_uuid(), 'Epsilon Group', '9 Mountain Dr', '555-5000', 'hello@epsilon.com', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system');


WITH accs AS (
    SELECT billing_acc_id
    FROM billing_account
    ORDER BY name
)

INSERT INTO invoice (
    invoice_id, created_at, created_by, modified_at, modified_by,
    amount, invoice_date, invoice_number, status, vat_amount, billing_acc_id
)
VALUES
-- 90 DAYS AGO (7 invoices)
(gen_random_uuid(),
 EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')),   -- FIXED
 'system',
 EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')),
 'system',
 100.00,
 EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')),
 'INV-001',
 'PAID',
 20.00,
 (SELECT billing_acc_id FROM accs LIMIT 1)),


(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days'))
, 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 150.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-002', 'PAID', 30.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 200.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-003', 'UNPAID', 40.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 2)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 175.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-004', 'UNPAID', 35.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 3)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 220.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-005', 'PAID', 44.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 4)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 130.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-006', 'PAID', 26.00, (SELECT billing_acc_id FROM accs LIMIT 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 180.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'INV-007', 'UNPAID', 36.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 1)),

-- 60 DAYS AGO (7 invoices)
(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 110.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-008', 'PAID', 22.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 2)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 160.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-009', 'UNPAID', 32.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 3)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 210.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-010', 'PAID', 42.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 4)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 125.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-011', 'UNPAID', 25.00, (SELECT billing_acc_id FROM accs LIMIT 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 190.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-012', 'PAID', 38.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 140.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-013', 'UNPAID', 28.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 2)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 175.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '60 days')), 'INV-014', 'PAID', 35.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 3)),

-- 30 DAYS AGO (6 invoices)
(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 115.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-015', 'PAID', 23.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 4)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 170.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-016', 'UNPAID', 34.00, (SELECT billing_acc_id FROM accs LIMIT 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 205.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-017', 'PAID', 41.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 1)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 150.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-018', 'UNPAID', 30.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 2)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 199.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-019', 'PAID', 39.80, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 3)),

(gen_random_uuid(), EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'system', EXTRACT(EPOCH FROM (NOW() - INTERVAL '90 days')), 'system',
 135.00, EXTRACT(EPOCH FROM (NOW() - INTERVAL '30 days')), 'INV-020', 'UNPAID', 27.00, (SELECT billing_acc_id FROM accs LIMIT 1 OFFSET 4));

end transaction;

