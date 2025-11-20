CREATE TABLE IF NOT EXISTS billing_account
(
    billing_acc_id UUID PRIMARY KEY,
    name           VARCHAR(255),
    address        VARCHAR(255),
    phone          VARCHAR(20),
    email          VARCHAR(255),
    created_at     BIGINT NOT NULL,
    created_by     VARCHAR(255) NOT NULL,
    modified_at    BIGINT NOT NULL,
    modified_by    VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS invoice
(
    invoice_id      UUID PRIMARY KEY,
    billing_acc_id  UUID NOT NULL,
    invoice_number  VARCHAR(255),
    status          VARCHAR(50),
    invoice_date    BIGINT NOT NULL,
    amount          DECIMAL(19, 2),
    vat_amount      DECIMAL(19, 2),
    created_at      BIGINT NOT NULL,
    created_by      VARCHAR(255) NOT NULL,
    modified_at     BIGINT NOT NULL,
    modified_by     VARCHAR(255) NOT NULL,
    CONSTRAINT fk_invoice_billing_account FOREIGN KEY (billing_acc_id) REFERENCES billing_account(billing_acc_id)
);

CREATE TABLE IF NOT EXISTS notification
(
    notification_id UUID PRIMARY KEY,
    invoice_id      UUID NOT NULL,
    type            VARCHAR(50),
    status          VARCHAR(50),
    message         VARCHAR(500),
    created_at      BIGINT NOT NULL,
    created_by      VARCHAR(255) NOT NULL,
    modified_at     BIGINT NOT NULL,
    modified_by     VARCHAR(255) NOT NULL,
    CONSTRAINT fk_notification_invoice FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id)
);