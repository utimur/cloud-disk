CREATE TABLE access(
    id bigint AUTO_INCREMENT,
    type varchar(32) NOT NULL,
    PRIMARY KEY(id)
) engine=myISAM;

CREATE TABLE file (
    id bigint AUTO_INCREMENT,
    name varchar(64) NOT NULL,
    type varchar(32) NOT NULL,
    avatar varchar(255) DEFAULT NULL,
    access_link varchar(255) DEFAULT NULL,
    size bigint default 0,
    access_id bigint REFERENCES access,
    disk_id bigint REFERENCES disk,
    basket_id bigint REFERENCES basket,
    parent_id bigint REFERENCES file,
    PRIMARY KEY(id)
) engine=myISAM;