CREATE SEQUENCE IF NOT EXISTS hibernate_sequence ;

CREATE TABLE IF NOT EXISTS user_account
(
  id bigint NOT NULL,
  first_name varchar (255),
  last_name varchar(255),
  pass_word varchar(255),
  created_date timestamp  ,
  user_name varchar(255),
  enabled boolean NOT NULL DEFAULT false,
  is_admin boolean NOT NULL DEFAULT false,
  CONSTRAINT user_account_pkey PRIMARY KEY (id )
)
;

CREATE TABLE IF NOT EXISTS uploaded_file (
  id bigint NOT NULL,
  file_name varchar (255),
  file_data blob,
  author varchar(255),
  created_date timestamp,
  PRIMARY KEY (`id`)
)
;
INSERT INTO user_account (id, first_name, last_name, pass_word, created_date, user_name, enabled, is_admin) VALUES (NEXTVAL('hibernate_sequence'), 'Regular', 'User', 'abc', '2015-03-24 12:00:00', 'user', true, false);
INSERT INTO user_account (id, first_name, last_name, pass_word, created_date, user_name, enabled, is_admin) VALUES (NEXTVAL('hibernate_sequence'), 'Admin', 'User', 'abc', '2015-03-24 12:00:00', 'admin', true, true);


