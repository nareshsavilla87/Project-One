DROP PROCEDURE  IF EXISTS ticket_detail_assign;
DELIMITER //
CREATE PROCEDURE ticket_detail_assign() 
BEGIN
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME ='b_ticket_details'
      AND COLUMN_NAME ='Assign_from') THEN
ALTER TABLE b_ticket_details ADD COLUMN Assign_from VARCHAR(100) DEFAULT NULL AFTER  `assigned_to`;
END IF;
 IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME ='b_ticket_details'
      AND COLUMN_NAME ='status') THEN
ALTER TABLE b_ticket_details ADD COLUMN status VARCHAR(100) DEFAULT NULL AFTER  `Assign_from`;
END IF;
IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME ='b_ticket_master'
      AND COLUMN_NAME ='issue') THEN
ALTER TABLE b_ticket_master ADD COLUMN issue VARCHAR(250) DEFAULT NULL AFTER  `assigned_to`;
END IF;
END //
DELIMITER ;
call ticket_detail_assign();
DROP PROCEDURE  IF EXISTS ticket_detail_assign;
