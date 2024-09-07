
drop table if exists  dictionary;

create table dictionary (
    id int primary key auto_increment,
    word varchar(100) unique not null
);
drop table if exists  momo_notepad;

create table momo_notepad(
    id  int primary key auto_increment,
    cloud_id varchar(100) unique not null ,
    title varchar(100) not null ,
    brief varchar(100) not null ,
    tags varchar(100),
    type varchar(20),
    status varchar(20) ,
    create_time datetime ,
    update_time datetime
);

drop table if exists notepad_dictionary;
create table notepad_dictionary(
    notepad_id int,
    dict_id int,
    unique key (notepad_id,dict_id)
)