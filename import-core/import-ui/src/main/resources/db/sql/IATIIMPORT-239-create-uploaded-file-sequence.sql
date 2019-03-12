drop sequence if exists uploaded_file_seq;
create sequence uploaded_file_seq;
SELECT setval('uploaded_file_seq', (SELECT MAX(id) FROM uploaded_file));
