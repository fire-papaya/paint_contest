# drop unnecessary indices
drop index if exists user_guid_uindex on user;
drop index if exists entry_guid_uindex on user;
drop index if exists contest_guid_uindex on user;

alter table image drop foreign key image_entry_id_fk;
drop index if exists image_guid_entry_id_uindex on image;

# add temporary field
alter table user
    add guid_temp binary(16) null;

alter table contest
    add guid_temp binary(16) null;

alter table entry
    add guid_temp binary(16) null;

alter table image
    add guid_temp binary(16) null;

# copy first 16 bytes of guid into guid_temp
UPDATE user c SET c.guid_temp = unhex(substr(hex(c.guid), 1, 32)) WHERE guid is not null;

UPDATE contest c SET c.guid_temp = unhex(substr(hex(c.guid), 1, 32)) WHERE guid is not null;

UPDATE entry c SET c.guid_temp = unhex(substr(hex(c.guid), 1, 32)) WHERE guid is not null;

UPDATE image c SET c.guid_temp = unhex(substr(hex(c.guid), 1, 32)) WHERE guid is not null;

# drop old guid column
alter table user
    drop column guid;
alter table contest
    drop column guid;
alter table entry
    drop column guid;
alter table image
    drop column guid;

# rename guid_temp
alter table user
    change guid_temp guid binary(16) default null null;

alter table contest
    change guid_temp guid binary(16) default null null;

alter table entry
    change guid_temp guid binary(16) default null null;

alter table image
    change guid_temp guid binary(16) default null null;

# put back indices and keys where needed
create unique index image_guid_entry_id_uindex
    on image (entry_id, guid);

alter table image
    add constraint image_entry_id_fk
        foreign key (entry_id) references entry (id);