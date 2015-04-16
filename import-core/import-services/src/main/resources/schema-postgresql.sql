CREATE SEQUENCE hibernate_sequence;

CREATE TABLE user_account
(
  id                       BIGINT  NOT NULL,
  first_name               CHARACTER VARYING(255),
  last_name                CHARACTER VARYING(255),
  pass_word                CHARACTER VARYING(255),
  created_date              TIMESTAMP WITHOUT TIME ZONE,
  user_name                CHARACTER VARYING(255),
  enabled                  BOOLEAN NOT NULL DEFAULT FALSE,
  is_admin                  BOOLEAN NOT NULL DEFAULT FALSE,
  CONSTRAINT user_account_pkey PRIMARY KEY (id)
);

INSERT INTO public.user_account (id, first_name, last_name, pass_word, created_date, user_name, enabled, is_admin) VALUES (nextval( 'hibernate_sequence'), 'Regular', 'User', 'abc', '2015-03-24 12:00:00', 'user', TRUE, FALSE);
INSERT INTO public.user_account (id, first_name, last_name, pass_word, created_date, user_name, enabled, is_admin) VALUES (nextval( 'hibernate_sequence'), 'Admin', 'User', 'abc', '2015-03-24 12:00:00', 'admin', TRUE, TRUE);

 -- postgresql
DROP TABLE IF EXISTS oauth_access_token;

CREATE TABLE public.oauth_access_token (
  token_id          TEXT PRIMARY KEY NOT NULL,
  token             BYTEA,
  authentication_id TEXT,
  user_name         TEXT,
  client_id         TEXT,
  authentication    TEXT,
  refresh_token     TEXT
);

