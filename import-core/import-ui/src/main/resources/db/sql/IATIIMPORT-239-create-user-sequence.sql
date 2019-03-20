drop sequence if exists user_seq;
create sequence user_seq;
SELECT setval('user_seq', (SELECT MAX(id) FROM user_account));
