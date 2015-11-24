ALTER TABLE b_ticket_details ADD COLUMN `Assign_from` VARCHAR(100)  DEFAULT NULL AFTER `assigned_to`;

ALTER TABLE b_ticket_master ADD COLUMN `issue` VARCHAR(205)  DEFAULT NULL AFTER `assigned_to`;

ALTER TABLE b_ticket_details ADD COLUMN `status` VARCHAR(45)  DEFAULT NULL AFTER `Assign_from`;

