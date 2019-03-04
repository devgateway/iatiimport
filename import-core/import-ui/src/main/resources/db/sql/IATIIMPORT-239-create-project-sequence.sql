create sequence if not exists project_seq;
SELECT setval('project_seq', (SELECT MAX(id) FROM project));
