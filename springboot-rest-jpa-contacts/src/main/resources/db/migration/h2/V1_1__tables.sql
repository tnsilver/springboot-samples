CREATE TABLE audit (
  id IDENTITY NOT NULL PRIMARY KEY,
  action varchar(50) DEFAULT NULL,
  created_on timestamp DEFAULT NULL,
  created_by varchar(255) NOT NULL,
  entity_id bigint DEFAULT NULL,
  entity_type varchar(255) DEFAULT NULL,
  entity varchar(1024) NOT NULL
);
CREATE TABLE contact (
  id IDENTITY NOT NULL PRIMARY KEY,
  contact_ssn varchar(50) DEFAULT NULL,
  first_name varchar(50) DEFAULT NULL,
  last_name varchar(50) DEFAULT NULL,
  birth_date date DEFAULT NULL,
  married BOOLEAN DEFAULT NULL,
  children int DEFAULT NULL,
  created_by varchar(255) NOT NULL,
  modified_by varchar(255) NOT NULL,
  created_on timestamp NOT NULL,
  modified_on timestamp	 DEFAULT NULL,
  version bigint DEFAULT NULL
);
