create table user
(
    id          int auto_increment,
    guid        binary(36) default UUID(),
    telegram_id bigint                not null,
    username    varchar(100) not null,
    date_created timestamp default now(),
    date_updated timestamp default null on update now(),
    constraint user_pk
        primary key (id)
);

create unique index user_guid_uindex
    on user (guid);

create unique index user_telegram_id_uindex
    on user (telegram_id);


create table contest
(
    id          int auto_increment not null ,
    guid        binary(36) default UUID(),
    `name` varchar(100),
    `description` varchar(511),
    start_date timestamp not null,
    end_date timestamp not null,
    date_created timestamp default now(),
    date_updated timestamp default null on update now(),
    constraint contest_pk
        primary key (id)
);

create unique index contest_guid_uindex
    on user (guid);


create table entry
(
    id          int auto_increment not null,
    guid        binary(36) default UUID(),
    `code` varchar(10),
    contest_id int not null,
    user_id int not null,
    date_created timestamp default now(),
    date_updated timestamp default null on update now(),
    constraint contest_pk
        primary key (id),
    constraint entry_contest_id_fk
        foreign key (contest_id) references contest (id),
    constraint entry_user_id_fk
        foreign key (user_id) references user (id)
);

create unique index entry_guid_uindex
    on user (guid);
