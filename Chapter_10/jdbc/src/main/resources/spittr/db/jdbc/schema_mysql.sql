-- noinspection SqlNoDataSourceInspectionForFile

drop table if exists spittle;
drop table if exists spitter;

create table spitter
(
    id            integer primary key auto_increment,
    username      varchar(25)  not null,
    password      varchar(25)  not null,
    fullName      varchar(100) not null,
    email         varchar(50)  not null,
    updateByEmail boolean      not null
);

create table spittle
(
    id         integer primary key auto_increment,
    spitter    integer       not null,
    message    varchar(2000) not null,
    postedTime datetime      not null,
    foreign key (spitter) references spitter (id)
);
