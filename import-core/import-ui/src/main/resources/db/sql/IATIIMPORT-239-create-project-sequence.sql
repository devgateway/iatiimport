drop sequence if exists project_seq;
create sequence project_seq;
SELECT setval('project_seq', (SELECT MAX(id) FROM project));
