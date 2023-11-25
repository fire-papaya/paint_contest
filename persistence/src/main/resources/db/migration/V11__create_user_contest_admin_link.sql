ALTER TABLE contest
    CHANGE start_date start_date TIMESTAMP NOT NULL DEFAULT 0;

ALTER TABLE contest
    CHANGE start_date start_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

create table community_admin_user
(
    id           int auto_increment
        primary key,
    guid         binary(36) default uuid()              null,
    date_created timestamp  default current_timestamp() null,
    date_updated timestamp                              null on update current_timestamp(),
    community_id   int                                  not null,
    user_id      int                                    null,
    constraint contest_admin_user_contest_user_uk
        unique (community_id, user_id),
    constraint contest_admin_user_uindex
        unique (guid),
    constraint community_admin_user_id_fk
        foreign key (user_id) references user (id),
    constraint community_admin_community_id_fk
        foreign key (community_id) references community (id)
);

alter table user
    add is_admin binary default b'0' not null;