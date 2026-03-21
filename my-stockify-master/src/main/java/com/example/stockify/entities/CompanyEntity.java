package com.example.stockify.entities;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "company",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "companyName")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {

    @Id
    @Column(name="nseCode" , length = 50)
    @NotBlank(message = "NSE code is required")
    private String nseCode ;

    @Column(name="companyName" , length = 100)
    @NotBlank(message = "Company name should not be null")
    private String companyName;

    @Column(name = "stockPrice")
    @NotNull(message = "Stock price should not be null ")
    @Positive(message = "Stock price should be always positive ")
    private Float stockPrice ;


    @Column(name = "yearLow")
    @PositiveOrZero(message = "year low cannot be negative")
    private Integer yearLow;

    @Column(name = "yearHigh")
    @PositiveOrZero(message = "year High cannot be negative")
    private Integer yearHigh;

    @Column(name = "description", columnDefinition = "TEXT" , length = 500)
    private String description;

    @Column(name = "bseCode", length = 50)
    private String bseCode;


}



//#create table company (
//        nse_code varchar(50) not null,
//bse_code varchar(50),
//company_name varchar(100) not null,
//description TEXT,
//stock_price float(23) not null,
//year_high integer,
//year_low integer,
//primary key (nse_code)
//    ) engine=InnoDB
//Hibernate:
//create table portfolio (
//        id integer not null auto_increment,
//        average_price float(23) not null,
//investment float(23),
//quantity integer not null,
//stock_name varchar(100) not null,
//username varchar(50),
//primary key (id)
//    ) engine=InnoDB
//Hibernate:
//create table transactions (
//        id integer not null auto_increment,
//        amount float(23),
//price float(23) not null,
//quantity integer not null,
//stock_name varchar(100) not null,
//trade_type varchar(20),
//txn_time datetime(6),
//type varchar(20) not null,
//username varchar(50),
//primary key (id)
//    ) engine=InnoDB
//Hibernate:
//create table users (
//        username varchar(50) not null,
//aadhar varchar(12),
//dob date,
//email varchar(255),
//income float(53),
//name varchar(30),
//pan varchar(10) not null,
//password varchar(255),
//phone bigint not null,
//primary key (username)
//    ) engine=InnoDB
//Hibernate:
//create table wallet (
//        username varchar(50) not null,
//amount float(23) not null,
//primary key (username)
//    ) engine=InnoDB
//Hibernate:
//alter table company
//drop index UKh7w1mkrsh1wcg5dkv6wrvam5m
//Hibernate:
//alter table company
//add constraint UKh7w1mkrsh1wcg5dkv6wrvam5m unique (company_name)
//Hibernate:
//alter table users
//drop index UK6dotkott2kjsp8vw4d0m25fb7
//Hibernate:
//alter table users
//add constraint UK6dotkott2kjsp8vw4d0m25fb7 unique (email)
//Hibernate:
//alter table users
//drop index UKdu5v5sr43g5bfnji4vb8hg5s3
//Hibernate:
//alter table users
//add constraint UKdu5v5sr43g5bfnji4vb8hg5s3 unique (phone)
//Hibernate:
//alter table users
//drop index UK6lisc1l1gp3km53sgs4y8b844
//Hibernate:
//alter table users
//add constraint UK6lisc1l1gp3km53sgs4y8b844 unique (aadhar)
//Hibernate:
//alter table users
//drop index UKeynn6fsc6tiakrei854umn1j9
//Hibernate:
//alter table users
//add constraint UKeynn6fsc6tiakrei854umn1j9 unique (pan)
//references users (username)
//Hibernate:
//alter table portfolio
//add constraint FK6gmnkr2bm75192eeije3c4507
//foreign key (username)
//references users (username)
//Hibernate:
//alter table transactions
//add constraint FKtcoyt1ovyl2aewet8vi7fkfta
//foreign key (username)
//references users (username)
//Hibernate:
//alter table wallet
//add constraint FKbvdjwhiy1vdqhow3ha61401cx
//foreign key (username)
