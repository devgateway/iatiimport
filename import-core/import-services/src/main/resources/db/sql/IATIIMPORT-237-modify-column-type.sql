SET bytea_output = 'hex';
create table uploaded_file2 as select id,author,created_date,file_data,file_name,CAST( substring(CAST ( session_id AS text) from 3) AS uuid) session_id,is_valid from uploaded_file;
alter table project drop constraint  fk_l9ctv87fdco00dext3b3pypjf;
alter table project drop constraint  fkabxrsnujmw12ikygyi0v0bsti;
drop table uploaded_file;
create table uploaded_file as select * from uploaded_file2;
alter table uploaded_file  add CONSTRAINT uploaded_file_pkey PRIMARY KEY (id);
alter table project add CONSTRAINT fk_l9ctv87fdco00dext3b3pypjf_foreing FOREIGN KEY (file_id) REFERENCES public.uploaded_file (id);
drop table uploaded_file2;
SET bytea_output = 'escape';