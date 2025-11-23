CREATE TABLE aqua_net_friend (
    id             BIGINT AUTO_INCREMENT NOT NULL,
    date           datetime,
    au_id          BIGINT  NOT NULL,
    friend_au_id   BIGINT  NOT NULL,

    CONSTRAINT fk_friend_owner_on_aqua_net_user FOREIGN KEY (au_id) REFERENCES aqua_net_user (au_id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_friend_on_aqua_net_user FOREIGN KEY (friend_au_id) REFERENCES aqua_net_user (au_id) ON DELETE CASCADE,
    CONSTRAINT pk_aqua_net_friend PRIMARY KEY (id)
);