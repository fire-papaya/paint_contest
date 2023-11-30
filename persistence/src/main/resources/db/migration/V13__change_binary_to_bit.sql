alter table contest
    drop column if exists is_draft;

alter table user
    drop column if exists is_admin;

alter table contest
    add is_draft bit default b'0' not null;

alter table user
    add is_admin bit default b'0' not null;

