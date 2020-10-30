CREATE TABLE mydb."PEOPLE"
(
    "peopleId" integer NOT NULL,
    "firstName" varchar(45) DEFAULT NULL,
    "lasttName" varchar(45) DEFAULT NULL,
    "email" varchar(45) DEFAULT NULL,
	"age" int(11) DEFAULT NULL,
	PRIMARY KEY ("peopleId")
)