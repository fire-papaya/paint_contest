alter table user
    add community_id int null;

alter table user
    add constraint user_community_id_fk
        foreign key (community_id) references community (id);
