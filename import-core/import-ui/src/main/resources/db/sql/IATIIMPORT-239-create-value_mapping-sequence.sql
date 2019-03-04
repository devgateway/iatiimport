create sequence if not exists value_mapping_seq;
SELECT setval('value_mapping_seq', (SELECT MAX(id) FROM value_mapping_template));
