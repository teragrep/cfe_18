/*
 * Integration main data management for Teragrep
 * Copyright (C) 2021  Suomen Kanuuna Oy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://github.com/teragrep/teragrep/blob/main/LICENSE>.
 *
 *
 * Additional permission under GNU Affero General Public License version 3
 * section 7
 *
 * If you modify this Program, or any covered work, by linking or combining it
 * with other code, such other code is not for that reason alone subject to any
 * of the requirements of the GNU Affero GPL version 3 as long as this Program
 * is the same Program as licensed from Suomen Kanuuna Oy without any additional
 * modifications.
 *
 * Supplemented terms under GNU Affero General Public License version 3
 * section 7
 *
 * Origin of the software must be attributed to Suomen Kanuuna Oy. Any modified
 * versions must be marked as "Modified version of" The Program.
 *
 * Names of the licensors and authors may not be used for publicity purposes.
 *
 * No rights are granted for use of trade names, trademarks, or service marks
 * which are in The Program if any.
 *
 * Licensee must indemnify licensors and authors for any liability that these
 * contractual assumptions impose on licensors and authors.
 *
 * To the extent this program is licensed as part of the Commercial versions of
 * Teragrep, the applicable Commercial License may apply to this file if you as
 * a licensee so wish it.
 */
USE cfe_00;
DELIMITER //
CREATE OR REPLACE PROCEDURE delete_hub(input_hub_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;
    IF ((SELECT COUNT(id) FROM cfe_00.hubs WHERE id = input_hub_id) = 0) THEN
        SELECT JSON_OBJECT('id', input_hub_id, 'message', 'Hub does not exist') INTO @h;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @h;
    END IF;

    -- check if there are hosts using the hub before deleting
    -- Without this check all the hosts connected to hub are deleted. This is due to host_type_cfe delete on cascade hosts.
    IF ((SELECT COUNT(htc.host_id)
         FROM cfe_00.host_type_cfe htc
         WHERE htc.hub_id = input_hub_id
           AND htc.host_id != (SELECT h.id
                           FROM location.host h
                                    INNER JOIN hubs h2 ON h.id = h2.host_id
                           WHERE h2.id = input_hub_id)) > 0) THEN
        SELECT JSON_OBJECT('id', input_hub_id, 'message', 'Hosts use the hub') INTO @ha;
        -- Signal user error due to user not removing hosts from Hub before deleting.
        SIGNAL SQLSTATE '23000' SET MESSAGE_TEXT = @ha;
    END IF;
    -- select the host id before deleting hub since it's not accessible later
    SELECT host_id INTO @HostId FROM cfe_00.hubs WHERE id = input_hub_id;
    DELETE FROM cfe_00.host_type_cfe WHERE hub_id = input_hub_id;
    DELETE FROM cfe_00.hubs WHERE id = input_hub_id;
    DELETE FROM location.host WHERE id = @HostId;
    COMMIT;

END;

//
DELIMITER ;