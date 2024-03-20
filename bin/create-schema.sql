alter user postgres password 'postgres';

create database oms;

\c oms

create table UserAuth(id varchar primary key, emailId varchar, name varchar, password varchar(120));
create table UserRole(id varchar, role varchar, primary key (id, role));
alter table UserRole add constraint userrole_id_fk FOREIGN KEY (id) REFERENCES UserAuth (id) on delete cascade;
create table UserProfile(id varchar primary key, email varchar, name varchar);
alter table UserProfile add constraint userprofile_id_fk FOREIGN KEY (id) REFERENCES UserAuth (id) on delete cascade;
create table UserAddress(userProfileId varchar, type varchar, id integer, street varchar, area varchar, city varchar, state varchar, country varchar, contactEmail varchar, contactName varchar, contactTelephone varchar, primary key (userProfileId, type, id)); 

create table Product(id varchar, name varchar, imageurl varchar, price numeric, primary key (id));

create table Cart(id varchar primary key);
create table CartLine(cartId varchar, cartLineId integer, productId varchar, quantity integer, primary key (cartId, cartLineId));
alter table CartLine add constraint cartline_cartid_fk FOREIGN KEY (cartId) REFERENCES Cart (id) on delete cascade;
alter table CartLine add constraint cartline_productid_fk FOREIGN KEY (productId) REFERENCES Product (id) on delete cascade;

create table OrderMaster(id varchar, userId varchar, status varchar, createdDate date, createdTime time, primary key (id));
alter table OrderMaster add constraint order_userid_fk FOREIGN KEY (userId) REFERENCES UserAuth (id) on delete cascade;
create index order_user_id on OrderMaster (userId);
create table OrderLine(orderId varchar, orderLineId varchar, productId varchar, quantity integer, primary key (orderId, orderLineId));
alter table OrderLine add constraint orderline_orderid_fk FOREIGN KEY (orderId) REFERENCES OrderMaster (id) on delete cascade;
alter table OrderLine add constraint orderline_productid_fk FOREIGN KEY (productId) REFERENCES Product (id) on delete cascade;

create table Inventory(productId varchar, quantity numeric, primary key (productId));
alter table Inventory add constraint inventory_productid_fk FOREIGN KEY (productId) REFERENCES Product (id) on delete cascade;

INSERT INTO UserAuth (id, name, password, emailId) values ('admin','Admin','$2a$12$/E4.9dBmbgkHyd4Sz4WNP.eu.KCejt1.sqr7OrSjjGaow4CXmIYUi', 'admin@test.com');
INSERT INTO UserRole (id, role) VALUES ('admin','Admin');
