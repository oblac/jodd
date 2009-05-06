
------------
-- SELECT --
------------

SELECT LastName, FirstName FROM Persons;
SELECT * FROM Persons;
SELECT DISTINCT LastName FROM Persons;

SELECT * FROM Persons WHERE City='Sandnes';
SELECT * FROM Persons WHERE FirstName LIKE 'O%';



------------
-- INSERT --
------------

INSERT INTO table_name (col1, col2, ...) VALUES (value1, value2);


UPDATE table_name
SET column_name = new_value
WHERE column_name = some_value

DELETE FROM table_name
WHERE column_name = some_value
