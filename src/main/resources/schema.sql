create table geologic_class (
        id bigint not null,
        code varchar(255),
        name varchar(255),
        selection_id bigint,
        primary key (id)
    );
create table selection (
        id bigint not null,
        name varchar(255),
        primary key (id)
    );
create sequence geologic_class_seq start with 1 increment by 50;
create sequence selection_seq start with 1 increment by 50;

alter table if exists geologic_class
       add constraint ReferenceToSelection_FKey
       foreign key (selection_id)
       references selection;

