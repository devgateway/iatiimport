create sequence if not exists user_seq;
SELECT setval('user_seq', (SELECT MAX(id) FROM user_account));
