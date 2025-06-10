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
USE flow;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_sink(protocol VARCHAR(20), ip_address VARCHAR(16), sink_port VARCHAR(5),
                                        flow_id INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    IF ((SELECT COUNT(id) FROM flow.L7 WHERE app_protocol = protocol) = 0) THEN
        INSERT INTO flow.L7(app_protocol)
        VALUES (protocol);
        SELECT LAST_INSERT_ID() INTO @ProtocolId;
    ELSE
        SELECT id INTO @ProtocolId FROM flow.L7 WHERE app_protocol = protocol;
    END IF;

    IF ((SELECT COUNT(id)
         FROM flow.capture_sink cs
         WHERE cs.L7_id = @ProtocolId
           AND cs.flow_id = flow_id
           AND cs.ip_address = ip_address
           AND cs.sink_port = sink_port) = 0) THEN
        INSERT INTO flow.capture_sink(L7_id, flow_id, ip_address, sink_port)
        VALUES (@ProtocolId, flow_id, ip_address, sink_port);
        SELECT LAST_INSERT_ID() AS id;
    ELSE
        SELECT id AS id
        FROM flow.capture_sink cs
        WHERE cs.L7_id = @ProtocolId
          AND cs.flow_id = flow_id
          AND cs.ip_address = ip_address
          AND cs.sink_port = sink_port;
    END IF;
    COMMIT;

END;
//
DELIMITER ;
