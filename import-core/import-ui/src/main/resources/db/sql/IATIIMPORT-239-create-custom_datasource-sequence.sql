create sequence if not exists custom_datasource_seq;
SELECT setval('custom_datasource_seq', (SELECT MAX(id) FROM custom_data_source));
