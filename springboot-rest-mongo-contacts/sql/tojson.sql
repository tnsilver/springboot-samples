SELECT
	CONCAT("[",
		GROUP_CONCAT(
			CONCAT("{ssn:' ",contact_ssn,"',"),
			CONCAT("firstName:' ",first_name,"',"),
			CONCAT("lastName:' ",last_name,"',"),
			CONCAT("birthDate:' ",date_of_birth,"',"),
			CONCAT("married:' ",married,"',"),
			CONCAT(",children:' ",children,"'}")),"]") AS json
FROM contact INTO OUTFILE '/var/lib/mysql-files/data.json';

