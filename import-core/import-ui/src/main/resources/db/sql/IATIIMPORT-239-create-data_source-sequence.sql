drop sequence if exists datasource_seq;
create sequence  datasource_seq;
SELECT setval('datasource_seq', (SELECT MAX(id) FROM data_source));
