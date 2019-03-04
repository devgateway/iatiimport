create sequence if not exists uploaded_file_seq;
SELECT setval('uploaded_file_seq', (SELECT MAX(id) FROM uploaded_file));
