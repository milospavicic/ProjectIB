INSERT INTO users(email,password,certificate,active)VALUES('123','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,true)
INSERT INTO users(email,password,certificate,active)VALUES('1234','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,true)
INSERT INTO users(email,password,certificate,active)VALUES('1','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)
INSERT INTO users(email,password,certificate,active)VALUES('2','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)
INSERT INTO users(email,password,certificate,active)VALUES('12','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)


INSERT INTO authority(name)VALUES('ADMIN')
INSERT INTO authority(name)VALUES('REGULAR')

INSERT INTO user_authority(user_id,authority_id)VALUES(1,1)
INSERT INTO user_authority(user_id,authority_id)VALUES(1,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(2,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(3,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(4,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(5,2)