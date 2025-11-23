ALTER TABLE aqua_net_user ADD COLUMN permission INTEGER;

CREATE TABLE aqua_net_logs (
    date           datetime,
    au_id          BIGINT  NOT NULL,
    affected_au_id BIGINT,
    type           INTEGER,
    details        VARCHAR(255) NOT NULL,

    CONSTRAINT fk_logs_on_aqua_net_user FOREIGN KEY (au_id) REFERENCES aqua_net_user (au_id) ON DELETE CASCADE,
    CONSTRAINT fk_logs_affected_on_aqua_net_user FOREIGN KEY (affected_au) REFERENCES aqua_net_user (au_id)
);