use cfe_18;

/*
  This file works as an example of idea behind g_x_g triggers.

 This is a template for cant_add_existing_tag_into_group in before_insert_triggers.sql file

 Workflow
    1. get all tags
    2. link tags according to a capture_group
    3. compare capture_groups against host_groups
    4. check all hosts in that group to see if they exist already on said tag


 Utilizing the test data below will prove the logic is sound when existing tag is inserted into an existing group.
  Trigger does not care if the tag is new or if the capture group is new.

 Any tag included in capture group 1 or 2 is inserted to other group. This is due to both capture groups sharing same host,
  but with different unique tags. Meaning there is no rule breaking or collisions occurring.

  */

call flow.insert_flow('flow1');
call flow.insert_sink('1','1','1',1);
call cfe_18.insert_relp_capture('1','1','1','1','1','1','1','flow1');
call cfe_18.insert_relp_capture('2','2','2','2','2','2','1','flow1');
call cfe_18.insert_relp_capture('3','3','3','3','3','3','1','flow1');
call cfe_18.insert_relp_capture('5','5','5','5','5','5','1','flow1');
call cfe_18.insert_relp_capture('5','5','5','5','5','5','1','flow1');
call cfe_18.insert_relp_capture('6','6','6','6','6','6','1','flow1');
call cfe_18.insert_relp_capture('7','7','7','7','7','7','1','flow1');
call cfe_18.insert_relp_capture('8','8','8','8','8','8','1','flow1');

insert into location.host (id, MD5, fqhost, host_type) VALUES (1,'1','1','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (2,'2','2','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (3,'3','3','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (4,'4','4','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (5,'5','5','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (6,'6','6','RELP');
insert into location.host (id, MD5, fqhost, host_type) VALUES (7,'7','7','RELP');
insert into location.host_group(host_type, groupName) VALUES ('relp','group1');
insert into location.host_group(host_type, groupName) VALUES ('relp','group2');
insert into location.host_group(host_type, groupName) VALUES ('relp','group3');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (1,1,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (1,2,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (1,3,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (2,4,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (2,3,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (2,5,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (2,6,'RELP');
insert into location.host_group_x_host(host_group_id, host_id, host_type) VALUES (3,7,'RELP');

insert into cfe_18.capture_def_group(capture_def_group_name, capture_type, flow_id) VALUES ('group1','RELP',1);
insert into cfe_18.capture_def_group(capture_def_group_name, capture_type, flow_id) VALUES ('group2','RELP',1);
insert into cfe_18.capture_def_group(capture_def_group_name, capture_type, flow_id) VALUES ('group3','RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (1,1,1,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (2,1,2,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (3,2,3,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (4,2,4,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (5,2,5,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (6,3,6,'RELP',1);
insert into cfe_18.capture_def_group_x_capture_def(capture_def_id, capture_def_group_id, tag_id, capture_type, flow_id) values (7,3,7,'RELP',1);

insert into cfe_18.host_groups_x_capture_def_group(host_group_id, capture_group_id) VALUES (1,1);
insert into cfe_18.host_groups_x_capture_def_group(host_group_id, capture_group_id) VALUES (2,2);
insert into cfe_18.host_groups_x_capture_def_group(host_group_id, capture_group_id) VALUES (3,3);


-- Select all tags into temporary table
CREATE OR REPLACE TEMPORARY TABLE all_existing_tags AS
select tag_id, capture_def_group_id
from capture_def_group_x_capture_def taqs_with_groups;

-- check all tags
select *
from all_existing_tags;

-- During insertion select all groups that have said tag id and include the group being linked to in order to include said group in the process.
-- self join duplicates rows. Added distinct for clarity. No performance difference here.

CREATE OR REPLACE TEMPORARY TABLE groups_where_new_tag_exists AS
SELECT distinct(ldgxld.capture_def_group_id)
from capture_def_group_x_capture_def ldgxld
         inner JOIN all_existing_tags ttdwst ON ldgxld.capture_def_group_id = ttdwst.capture_def_group_id
where ldgxld.tag_id=3
or ldgxld.capture_def_group_id=3;


-- debug
select *
from groups_where_new_tag_exists;

-- Select host_groups which are already linked to capture_groups
CREATE OR REPLACE TEMPORARY TABLE host_groups_linked_to_capture_groups AS
SELECT DISTINCT hgxldg.host_group_id
from host_groups_x_capture_def_group hgxldg
         INNER JOIN groups_where_new_tag_exists gwnte
                    ON gwnte.capture_def_group_id = hgxldg.capture_group_id;

-- debug, show host groups
SELECT *
FROM host_groups_linked_to_capture_groups;

-- Select hosts from host_groups - NOT DISTINCT here. Important for check.
CREATE OR REPLACE TEMPORARY TABLE hosts AS
select hgxh.host_id
from location.host_group_x_host hgxh
         INNER JOIN host_groups_linked_to_capture_groups tthwcd
                    ON hgxh.host_group_id = tthwcd.host_group_id;

-- Debug
SELECT *
from hosts;

-- Does if count = distinct to count all. Distinct count is for unique amount and regular count for overall. If regular count is higher then there are duplicates.
select if(count(DISTINCT hgxh.host_id) = count(hgxh.host_id), true, false)
from location.host_group_x_host hgxh
         INNER JOIN host_groups_linked_to_capture_groups tthwcd
                    ON hgxh.host_group_id = tthwcd.host_group_id;



