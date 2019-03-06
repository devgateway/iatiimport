drop sequence if exists mapping_template_seq;
create sequence mapping_template_seq;
SELECT setval('mapping_template_seq', (SELECT MAX(id) FROM field_mapping_template));
