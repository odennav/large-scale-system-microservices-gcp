CREATE KEYSPACE oms WITH replication = {'class':'SimpleStrategy', 'replication_factor' : #REPLICATION_FACTOR#}; 

CREATE TABLE oms.UserAuth (id varchar, name varchar, password varchar, roles list<text>, emailId varchar, PRIMARY KEY(id));
CREATE TABLE oms.UserAddress (userProfileId varchar, type varchar, id int, street varchar, area varchar, city varchar, state varchar, country varchar, contactName varchar, contactTelephone varchar, contactEmail varchar, PRIMARY KEY(userProfileId, type, id));
CREATE TABLE oms.UserProfile (id varchar, name varchar, email varchar, PRIMARY KEY(id));
CREATE TABLE oms.Product (id varchar, name varchar, price float, imageUrl varchar, PRIMARY KEY(id));
CREATE TABLE oms.CartLine (cartLineId int, cartId varchar, productId varchar, quantity float, PRIMARY KEY(cartId, cartLineId));
CREATE TABLE oms.OrderLine (userId varchar, id varchar, orderLineId int, productId varchar, quantity float, status varchar, createdDate date, createdTime time, PRIMARY KEY(userId, id, orderLineId));
CREATE TABLE oms.Inventory (productId varchar, quantity float, PRIMARY KEY(productId));

INSERT INTO oms.UserAuth (id, name, password, roles, emailId) values ('admin','Admin','$2a$12$/E4.9dBmbgkHyd4Sz4WNP.eu.KCejt1.sqr7OrSjjGaow4CXmIYUi', ['Admin','User'],'admin@test.com');
