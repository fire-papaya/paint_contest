alter table contest
    modify start_date timestamp default current_timestamp() not null;