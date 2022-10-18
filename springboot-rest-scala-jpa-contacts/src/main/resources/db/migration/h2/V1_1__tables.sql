CREATE TABLE audit (
  id identity NOT NULL AUTO_INCREMENT,
  action varchar(25) DEFAULT NULL,
  created_on timestamp DEFAULT NULL,
  created_by varchar(255) NOT NULL,
  entity_id bigint(20) DEFAULT NULL,
  entity_type varchar(25) DEFAULT NULL,
  entity varchar(1000) NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE contact (
  id identity  AUTO_INCREMENT NOT NULL,
  contact_ssn varchar(50) DEFAULT NULL,
  first_name varchar(50) DEFAULT NULL,
  last_name varchar(50) DEFAULT NULL,
  birth_date date DEFAULT NULL,
  married bit DEFAULT NULL,
  children int DEFAULT NULL,
  created_by varchar(255) NOT NULL,
  modified_by varchar(255) NOT NULL,
  created_on timestamp NOT NULL,
  modified_on timestamp	 DEFAULT NULL,
  version bigint DEFAULT NULL,
  PRIMARY KEY (id)
);
