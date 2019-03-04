create sequence if not exists mapping_template_seq;
SELECT setval('mapping_template_seq', (SELECT MAX(id) FROM field_mapping_template));
