create sequence if not exists reporting_org_seq;
SELECT setval('reporting_org_seq', (SELECT MAX(id) FROM reporting_organization));
