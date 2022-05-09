create trigger user_guid_generate
    before insert on user for each row
begin
    set NEW.guid = COALESCE(NEW.guid, UUID_TO_BIN(uuid()));
end;

create trigger contest_guid_generate
    before insert on contest for each row
begin
    set NEW.guid = COALESCE(NEW.guid, UUID_TO_BIN(uuid()));
end;

create trigger entry_guid_generate
    before insert on entry for each row
begin
    set NEW.guid = COALESCE(NEW.guid, UUID_TO_BIN(uuid()));
end;

create trigger image_guid_generate
    before insert on image for each row
begin
    set NEW.guid = COALESCE(NEW.guid, UUID_TO_BIN(uuid()));
end;