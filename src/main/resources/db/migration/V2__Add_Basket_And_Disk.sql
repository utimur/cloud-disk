CREATE TABLE basket (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT REFERENCES usr,
    PRIMARY KEY(id)
) engine=myISAM;

CREATE TABLE disk (
    id BIGINT AUTO_INCREMENT,
    user_id BIGINT REFERENCES usr,
    PRIMARY KEY(id)
) engine=myISAM;