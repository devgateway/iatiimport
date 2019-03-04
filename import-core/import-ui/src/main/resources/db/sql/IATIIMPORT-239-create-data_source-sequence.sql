create sequence if not exists datasource_seq;
SELECT setval('datasource_seq', (SELECT MAX(id) FROM data_source));
