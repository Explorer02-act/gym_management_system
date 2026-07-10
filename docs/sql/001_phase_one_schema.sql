alter table member add column if not exists created_at datetime(6);
alter table member add column if not exists updated_at datetime(6);
alter table member add column if not exists photo_url varchar(255);

alter table membership add column if not exists created_at datetime(6);
alter table membership add column if not exists updated_at datetime(6);
alter table membership add column if not exists plan_id bigint;

alter table attendance add column if not exists created_at datetime(6);
alter table attendance add column if not exists updated_at datetime(6);

create table if not exists membership_plan (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    active bit not null,
    actual_price decimal(38,2),
    badge varchar(255),
    description varchar(255),
    display_price decimal(38,2),
    duration_months integer,
    name varchar(255),
    primary key (id)
);

create table if not exists payment (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    amount decimal(38,2),
    payment_date date,
    payment_mode varchar(255),
    transaction_id varchar(255),
    member_id bigint,
    membership_id bigint,
    primary key (id)
);

create table if not exists gym_settings (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    gpay_number varchar(255),
    gym_name varchar(255),
    gym_phone varchar(255),
    qr_image_url varchar(255),
    primary key (id)
);

create table if not exists admin (
    id bigint not null auto_increment,
    created_at datetime(6),
    updated_at datetime(6),
    active bit not null,
    name varchar(255),
    password_hash varchar(255),
    role varchar(255),
    username varchar(255),
    primary key (id)
);

insert into membership_plan (active, actual_price, badge, description, display_price, duration_months, name)
select true, 3000.00, 'Starter Plan', 'Starter Plan', 2000.00, 1, '1 MONTH'
where not exists (select 1 from membership_plan where name = '1 MONTH');

insert into membership_plan (active, actual_price, badge, description, display_price, duration_months, name)
select true, 7000.00, 'Most Popular', 'Most Popular', 5000.00, 3, '3 MONTHS'
where not exists (select 1 from membership_plan where name = '3 MONTHS');

insert into membership_plan (active, actual_price, badge, description, display_price, duration_months, name)
select true, 15000.00, 'Best Value', 'Best Value', 10000.00, 6, '6 MONTHS'
where not exists (select 1 from membership_plan where name = '6 MONTHS');

insert into membership_plan (active, actual_price, badge, description, display_price, duration_months, name)
select true, 30000.00, 'Recommended', 'Recommended', 18000.00, 12, '12 MONTHS'
where not exists (select 1 from membership_plan where name = '12 MONTHS');
