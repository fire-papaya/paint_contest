create table community
(
    id           int auto_increment,
    guid         binary(36) default UUID() null,
    date_created timestamp  default now()  null,
    date_updated timestamp  default null   null,
    label        varchar(100)              not null,
    name         varchar(255)              not null,
    constraint community_pk
        primary key (id)
);

create unique index community_label_uindex
    on community (label);

alter table contest
    add community_id int null;

alter table contest
    add constraint contest_community_id_fk
        foreign key (community_id) references community (id);

