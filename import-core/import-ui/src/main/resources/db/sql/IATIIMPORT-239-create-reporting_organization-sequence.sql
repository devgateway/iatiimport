drop sequence if exists reporting_org_seq;
create sequence reporting_org_seq;
SELECT setval('reporting_org_seq', (SELECT MAX(id) FROM reporting_organization));
