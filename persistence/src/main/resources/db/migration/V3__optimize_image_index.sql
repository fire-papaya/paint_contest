CREATE FUNCTION BIN_TO_UUID(b BINARY)
    RETURNS CHAR(36)
BEGIN
    DECLARE hexStr CHAR(32);
    SET hexStr = HEX(b);
    RETURN LOWER(CONCAT(
            SUBSTR(hexStr, 1, 8), '-',
            SUBSTR(hexStr, 9, 4), '-',
            SUBSTR(hexStr, 13, 4), '-',
            SUBSTR(hexStr, 17, 4), '-',
            SUBSTR(hexStr, 21)
        ));
END;

CREATE FUNCTION UUID_TO_BIN(uuid CHAR(36))
    RETURNS BINARY(16)
BEGIN
    RETURN UNHEX(REPLACE(uuid, '-', ''));
END;

drop index if exists image_guid_uindex on image;

create unique index image_guid_entry_id_uindex
    on image (entry_id, guid);
