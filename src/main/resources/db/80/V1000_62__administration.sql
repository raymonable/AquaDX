ALTER TABLE aqua_net_user ADD COLUMN permission INTEGER DEFAULT 0;
ALTER TABLE aqua_net_user ADD COLUMN profile_view_restriction INTEGER DEFAULT 0;

CREATE TABLE aqua_net_logs (
    id             BIGINT AUTO_INCREMENT NOT NULL,
    date           datetime,
    au_id          BIGINT  NOT NULL,
    affected_au_id BIGINT,
    type           INTEGER,
    details        VARCHAR(255) NOT NULL,

    CONSTRAINT fk_logs_on_aqua_net_user FOREIGN KEY (au_id) REFERENCES aqua_net_user (au_id) ON DELETE CASCADE,
    CONSTRAINT fk_logs_affected_on_aqua_net_user FOREIGN KEY (affected_au_id) REFERENCES aqua_net_user (au_id),
    CONSTRAINT pk_aqua_net_logs PRIMARY KEY (id)
);