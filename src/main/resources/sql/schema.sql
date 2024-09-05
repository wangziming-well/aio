

drop table if exists dictionary;

create table dictionary (
    id int primary key auto_increment,
    word varchar(100) unique
);

