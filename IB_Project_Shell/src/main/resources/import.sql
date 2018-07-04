INSERT INTO users(email,password,certificate,active)VALUES('usera','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,true)
INSERT INTO users(email,password,certificate,active)VALUES('userb','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,true)
INSERT INTO users(email,password,certificate,active)VALUES('userc','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)
INSERT INTO users(email,password,certificate,active)VALUES('userd','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)
INSERT INTO users(email,password,certificate,active)VALUES('usere','$2a$04$kdC9Z02xAiBrw1vBlLusbOdsfHH7EH9D8c37ZGG1VzeL62ILUNM5q',NULL,false)


INSERT INTO authority(name)VALUES('ADMIN')
INSERT INTO authority(name)VALUES('REGULAR')

INSERT INTO user_authority(user_id,authority_id)VALUES(1,1)
INSERT INTO user_authority(user_id,authority_id)VALUES(1,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(2,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(3,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(4,2)
INSERT INTO user_authority(user_id,authority_id)VALUES(5,2)