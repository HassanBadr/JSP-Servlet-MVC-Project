
<!-- Oracle -->

create table student(

id      number(11),
first_name varchar2(50),
last_name  varchar2(50),
email       varchar2(50),
CONSTRAINT student_pk PRIMARY KEY (id)
);

  
  CREATE SEQUENCE student_sequence INCREMENT by 2;
  
  <!--hibernate Annote @SequenceGenerator(name = "id_seq", sequenceName = "student_sequence",allocationSize=1) -->

  
  <!-- use this TRIGGER if you are not use hibernate  -->  
  CREATE OR REPLACE TRIGGER student_on_insert
  BEFORE INSERT ON student
  FOR EACH ROW
BEGIN
  SELECT student_sequence.nextval
  INTO :new.id
  FROM dual;
END;