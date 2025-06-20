CREATE TABLE chusan_teams (
    team_id BIGINT AUTO_INCREMENT NOT NULL,
    owner_au_id BIGINT NOT NULL, -- AquaNet user ID
    name VARCHAR(32) NOT NULL,

    ranking INT NOT NULL DEFAULT 0, -- Note: Do NOT display teams with ranking 0 in the UI!!
    points INT NOT NULL DEFAULT 0,
    members_count INT NOT NULL DEFAULT 0,

    CONSTRAINT pk_chusan_team PRIMARY KEY (team_id)
);

ALTER TABLE chusan_teams
    ADD CONSTRAINT FK_TEAM_OWNER_ON_AQUA_USER FOREIGN KEY (owner_au_id) REFERENCES aqua_net_user (au_id);

CREATE TABLE chusan_team_requests (
    request_id BIGINT AUTO_INCREMENT NOT NULL,
    team_id BIGINT NOT NULL,
    request_au_id BIGINT NOT NULL, -- AquaNet user ID
    request_time VARCHAR(255) NOT NULL, -- Timestamp of the request

    CONSTRAINT pk_chusan_team_requests PRIMARY KEY (request_id)
);

ALTER TABLE chusan_team_requests
    ADD CONSTRAINT FK_TEAM_REQUEST_ON_AQUA_USER FOREIGN KEY (request_au_id) REFERENCES aqua_net_user (au_id);

ALTER TABLE chusan_user_data
    ADD team_id BIGINT NULL;
ALTER TABLE chusan_user_data
    ADD team_contribution INT NOT NULL;