CREATE TABLE `audit` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `action` varchar(25) DEFAULT NULL,
  `created_on` datetime DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `entity_type` varchar(25) DEFAULT NULL,
  `entity` text CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE `contact` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contact_ssn` varchar(50) DEFAULT NULL,
  `first_name` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `last_name` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `married` bit(1) DEFAULT NULL,
  `children` int(20) DEFAULT NULL,
  `created_by` varchar(255) NOT NULL,
  `modified_by` varchar(255) NOT NULL,
  `created_on` datetime NOT NULL,
  `modified_on` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=latin1;
