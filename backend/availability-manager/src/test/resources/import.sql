
INSERT INTO Employees (anumber, name, team) VALUES ('A123456' ,'Juan Jose Lopez Agüera', 'Big Data');
INSERT INTO Employees (anumber, name, team) VALUES ('A234567' ,'Isaias Vazquez Nuñez', 'Big Data');
INSERT INTO Employees (anumber, name, team) VALUES ('A345678' ,'Antonio Perez Farfan', 'IA');
INSERT INTO Employees (anumber, name, team) VALUES ('A456789', 'Laura Martínez Gomez', 'IA');



--alreadyExists
--INSERT INTO Calendar_Items (id, item_type, description, start_date, end_date, employee_anumber, item_active) VALUES (134, 'AUSENCIA', '', '2024-07-18', '2024-07-25', 'A123456', true);
--overlapsItems
--INSERT INTO calendar_items (id, item_type, description, start_date, end_date, employee_anumber, item_active) VALUES (45, 'VACACIONES', '','2024-08-01', '2024-08-07', 'A456789', true);
--delete
INSERT INTO Calendar_Items (id, item_type, description, start_date, end_date, employee_anumber, item_active) VALUES (34, 'AUSENCIA', '', '2025-07-18', '2025-07-25', 'A123456', true);
--update
INSERT INTO Calendar_Items (id, item_type, description, start_date, end_date, employee_anumber, item_active) VALUES (3, 'AUSENCIA', '', '2025-07-18', '2025-07-25', 'A234567', true);

