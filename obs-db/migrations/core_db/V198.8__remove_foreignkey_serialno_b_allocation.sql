Drop procedure IF EXISTS removeForeignKey;
DELIMITER //
create procedure removeForeignKey() 
Begin
  IF NOT EXISTS (
     SELECT * FROM information_schema.COLUMNS
     WHERE TABLE_NAME = 'b_allocation'
     and TABLE_SCHEMA = DATABASE())THEN
ALTER TABLE b_allocation DROP FOREIGN KEY `fk_idtls_srno` ;

END IF;
END //
DELIMITER ;
call removeForeignKey();
Drop procedure IF EXISTS removeForeignKey;
