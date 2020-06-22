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
