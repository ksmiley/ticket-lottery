-- Parse::SQL::Dia version 0.12                              
-- Documentation   http://search.cpan.org/dist/Parse-Dia-SQL/
-- Environment     Perl 5.010001, /opt/local/bin/perl        
-- Architecture    darwin-2level                             
-- Target Database mysql-innodb                              
-- Input file      database_uml.dia                          
-- Generated at    Sat Dec  5 19:20:34 2009                  

-- get_constraints_drop 
alter table studentInfo drop foreign key fk_student_curaddr ;
alter table adminInfo drop foreign key fk_admin_addr ;
alter table studentInfo drop foreign key fk_student_permaddr ;
alter table studentInfo drop foreign key fk_student_interaddr ;
alter table venueSection drop foreign key fk_venue_section ;
alter table venueRow drop foreign key fk_section_row ;
alter table studentGroup drop foreign key fk_group_owner ;
alter table lotteryEvent drop foreign key fk_lottery_venue ;
alter table lotterySeat drop foreign key fk_lottery_seats ;
alter table lotterySeat drop foreign key fk_availseat_realseat ;
alter table lotterySeat drop foreign key fk_availseat_student ;
alter table studentGroup drop foreign key fk_group_lottery ;
alter table lotteryRegistration drop foreign key fk_register_lottery ;
alter table lotteryRegistration drop foreign key fk_register_student ;
alter table lotteryRegistration drop foreign key fk_register_group ;
alter table lotterySeat drop foreign key fk_group_seats ;
alter table lotteryEvent drop foreign key fk_event_admin ;
alter table venueSeat drop foreign key fk_row_seat ;
drop index idx_username on studentInfo;
drop index idx_username on adminInfo;
drop index idx_sectionlabel on venueSection;
drop index idx_lottery on lotterySeat;
drop index idx_assigned on lotterySeat;
drop index idx_lottery_claimed on lotterySeat;
drop index idx_group on lotterySeat;
drop index idx_lottery on studentGroup;
drop index idx_owner on studentGroup;
drop index idx_group on lotteryRegistration;

-- get_permissions_drop 

-- get_view_drop

-- get_schema_drop
drop table if exists studentInfo;
drop table if exists mailingAddress;
drop table if exists adminInfo;
drop table if exists lotteryEvent;
drop table if exists venueInfo;
drop table if exists venueSection;
drop table if exists venueSeat;
drop table if exists lotterySeat;
drop table if exists studentGroup;
drop table if exists lotteryRegistration;
drop table if exists venueRow;

-- get_smallpackage_pre_sql 

-- get_schema_create
create table studentInfo (
   studentId         int          not null           ,
   username          varchar(30)  not null           ,
   password          varchar(100)  not null           ,
   active            smallint      default 1 not null,
   name              varchar(100) not null           ,
   email             varchar(100) not null           ,
   birthday          datetime                        ,
   dateEnrolled      datetime                        ,
   expectedGradDate  datetime                        ,
   college           varchar(50)                     ,
   degree            varchar(50)                     ,
   degreeType        varchar(20)                     ,
   permanentAddr     int                             ,
   currentAddr       int                             ,
   internationalAddr int                             ,
   loyaltyWeight     real          default 1         ,
   constraint pk_studentInfo primary key (studentId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table mailingAddress (
   addrId      int         auto_increment,
   line1       varchar(60)               ,
   line2       varchar(60)               ,
   city        varchar(30)               ,
   state       varchar(15)               ,
   zip         varchar(10)               ,
   country     varchar(50)               ,
   homePhone   varchar(20)               ,
   mobilePhone varchar(20)               ,
   constraint pk_mailingAddress primary key (addrId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table adminInfo (
   adminId  int          auto_increment     ,
   username varchar(30)  not null           ,
   password varchar(100)  not null           ,
   active   smallint      default 1 not null,
   name     varchar(100) not null           ,
   email    varchar(100) not null           ,
   homeAddr int                             ,
   constraint pk_adminInfo primary key (adminId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table lotteryEvent (
   lotteryId         int          auto_increment,
   displayName       varchar(100) not null      ,
   venue             int          not null      ,
   startTime         datetime     not null      ,
   endTime           datetime     not null      ,
   registerStartTime datetime     not null      ,
   distributionTime  datetime     not null      ,
   claimEndTime      datetime     not null      ,
   cancelEndTime     datetime     not null      ,
   saleEndTime       datetime     not null      ,
   createdBy         int          not null      ,
   constraint pk_lotteryEvent primary key (lotteryId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table venueInfo (
   venueId     int          auto_increment,
   name        varchar(100) not null      ,
   addrLine1   varchar(60)                ,
   addrLine2   varchar(60)                ,
   addrCity    varchar(30)                ,
   addrState   varchar(15)                ,
   addrZip     varchar(10)                ,
   phoneNumber varchar(20)                ,
   constraint pk_venueInfo primary key (venueId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table venueSection (
   sectionId int         auto_increment,
   venue     int         not null      ,
   label     varchar(30) not null      ,
   location  varchar(30)               ,
   position  int                       ,
   constraint pk_venueSection primary key (sectionId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table venueSeat (
   sectionId int not null,
   row       int not null,
   seatNo    int not null,
   constraint pk_venueSeat primary key (sectionId,row,seatNo)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table lotterySeat (
   lotteryId    int      not null  ,
   section      int      not null  ,
   row          int      not null  ,
   seatNo       int      not null  ,
   price        real               ,
   assignedTo   int      null      ,
   heldForGroup int      null      ,
   claimed      smallint  default 0,
   claimTime    datetime           ,
   paid         smallint  default 0,
   paidTime     datetime           ,
   constraint pk_lotterySeat primary key (lotteryId,section,row,seatNo)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table studentGroup (
   groupId int         auto_increment,
   lottery int         not null      ,
   name    varchar(60) not null      ,
   owner   int         not null      ,
   constraint pk_studentGroup primary key (groupId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table lotteryRegistration (
   studentId    int       not null,
   lotteryId    int       not null,
   groupId      int       null    ,
   registerTime timestamp         ,
   constraint pk_lotteryRegistration primary key (studentId,lotteryId)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;
create table venueRow (
   sectionId int         not null,
   row       int         not null,
   label     varchar(20) not null,
   position  int                 ,
   constraint pk_venueRow primary key (sectionId,row)
)   ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- get_view_create

-- get_permissions_create

-- get_inserts

-- get_smallpackage_post_sql

-- get_associations_create
create unique index idx_username on studentInfo (username) ;
create unique index idx_username on adminInfo (username) ;
create unique index idx_sectionlabel on venueSection (venue,label) ;
create index idx_lottery on lotterySeat (lotteryId) ;
create index idx_assigned on lotterySeat (assignedTo) ;
create index idx_lottery_claimed on lotterySeat (lotteryId,claimed) ;
create index idx_group on lotterySeat (heldForGroup) ;
create index idx_lottery on studentGroup (lottery) ;
create index idx_owner on studentGroup (owner) ;
create index idx_group on lotteryRegistration (groupId) ;
alter table studentInfo add constraint fk_student_curaddr 
    foreign key (currentAddr)
    references mailingAddress (addrId) ;
alter table adminInfo add constraint fk_admin_addr 
    foreign key (homeAddr)
    references mailingAddress (addrId) ;
alter table studentInfo add constraint fk_student_permaddr 
    foreign key (permanentAddr)
    references mailingAddress (addrId) ;
alter table studentInfo add constraint fk_student_interaddr 
    foreign key (internationalAddr)
    references mailingAddress (addrId) ;
alter table venueSection add constraint fk_venue_section 
    foreign key (venue)
    references venueInfo (venueId) on delete cascade;
alter table venueRow add constraint fk_section_row 
    foreign key (sectionId)
    references venueSection (sectionId) on delete cascade;
alter table studentGroup add constraint fk_group_owner 
    foreign key (owner)
    references studentInfo (studentId) ;
alter table lotteryEvent add constraint fk_lottery_venue 
    foreign key (venue)
    references venueInfo (venueId) ;
alter table lotterySeat add constraint fk_lottery_seats 
    foreign key (lotteryId)
    references lotteryEvent (lotteryId) ;
alter table lotterySeat add constraint fk_availseat_realseat 
    foreign key (section,row,seatNo)
    references venueSeat (sectionId,row,seatNo) ;
alter table lotterySeat add constraint fk_availseat_student 
    foreign key (assignedTo)
    references studentInfo (studentId) ;
alter table studentGroup add constraint fk_group_lottery 
    foreign key (lottery)
    references lotteryEvent (lotteryId) ;
alter table lotteryRegistration add constraint fk_register_lottery 
    foreign key (lotteryId)
    references lotteryEvent (lotteryId) ;
alter table lotteryRegistration add constraint fk_register_student 
    foreign key (studentId)
    references studentInfo (studentId) ;
alter table lotteryRegistration add constraint fk_register_group 
    foreign key (groupId)
    references studentGroup (groupId) ;
alter table lotterySeat add constraint fk_group_seats 
    foreign key (heldForGroup)
    references studentGroup (groupId) ;
alter table lotteryEvent add constraint fk_event_admin 
    foreign key (createdBy)
    references adminInfo (adminId) ;
alter table venueSeat add constraint fk_row_seat 
    foreign key (sectionId,row)
    references venueRow (sectionId,row) on delete cascade;
