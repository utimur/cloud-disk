create table hibernate_sequence (
    next_val bigint
) engine=MyISAM;

insert into hibernate_sequence values ( 1 );
insert into hibernate_sequence values ( 1 );

CREATE TABLE usr (
    id BIGINT NOT NULL AUTO_INCREMENT,
    password VARCHAR(64) NOT NULL,
    username VARCHAR(64) NOT NULL,
    mail VARCHAR(64) NOT NULL,
    free_space bigint default 0,
    has_avatar boolean default false,
    PRIMARY KEY (id)
) engine=myISAM;

CREATE TABLE role (
    id BIGINT NOT NULL AUTO_INCREMENT,
    role VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
) engine=myISAM;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL references usr,
    role_id BIGINT NOT NULL references role,
    PRIMARY KEY (user_id, role_id)
) engine=myISAM;

INSERT INTO role values (1, "ROLE_USER");
INSERT INTO role values (2, "ROLE_ADMIN");


INSERT INTO usr(password, username, mail)
        values("$2a$04$zUk5psHVasW8ZTh6yLE/T.tIkJM1M5aNgeXb0E6inUihrTGl0CY5e", "admin", "admin@mail.ru");

INSERT INTO usr(password, username, mail)
        values("$2a$04$zUk5psHVasW8ZTh6yLE/T.tIkJM1M5aNgeXb0E6inUihrTGl0CY5e", "usr", "user@mail.ru");

INSERT INTO usr(password, username, mail)
        values("$2a$04$zUk5psHVasW8ZTh6yLE/T.tIkJM1M5aNgeXb0E6inUihrTGl0CY5e", "test", "test@mail.ru");


INSERT INTO user_roles values (1,1);
INSERT INTO user_roles values (1,2);

INSERT INTO user_roles values (2,1);
INSERT INTO user_roles values (3,1);
INSERT INTO user_roles values (3,2);