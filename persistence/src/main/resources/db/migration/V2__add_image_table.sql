create table image (
    id          int auto_increment not null,
    guid        binary(36) default UUID(),
    entry_id int not null,
    is_ready bit(1) default b'0' not null,
    date_created timestamp default now(),
    date_updated timestamp null default null on update now(),
    constraint contest_pk
        primary key (id),
    constraint image_entry_id_fk
        foreign key (entry_id) references entry (id)
);

create unique index image_guid_uindex
    on image(guid);
