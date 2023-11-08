-- liquibase formatted sql

-- changeset Yurii Yatsenko:1
create table notification_task (
    id integer primary key,
    message varchar(255),
    time timestamp
);

-- changeset Yurii Yatsenko:2
alter table notification_task drop constraint notification_task_pkey;
alter table notification_task rename column id to chat_id;
alter table notification_task add id integer;
alter table notification_task add constraint notification_task_pkey primary key (id);