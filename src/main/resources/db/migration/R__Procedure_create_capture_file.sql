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
USE cfe_18;
DELIMITER //
CREATE OR REPLACE PROCEDURE insert_file_capture(meta_tag VARCHAR(48), retention_time VARCHAR(255),
                                                meta_category VARCHAR(48), application VARCHAR(48),
                                                capture_index VARCHAR(48), source_type VARCHAR(255),
                                                protocol VARCHAR(64), flow VARCHAR(255),
                                                tag_path VARCHAR(255), capture_path VARCHAR(255),
                                                file_processing_id INT)


BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
            RESIGNAL;
        END;
    START TRANSACTION;

    IF ((SELECT COUNT(id) FROM cfe_18.file_processing_type WHERE id = file_processing_id) = 0) THEN
        SELECT JSON_OBJECT('id', file_processing_id, 'message', 'Processing type does not exist') INTO @pt;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @pt;
    END IF;

    -- Check if tag_path matches with tag
    IF tag_path IS NOT NULL THEN
        SELECT MD5(tag_path) INTO @TagMd5;
        SELECT LPAD(@TagMd5, 8) INTO @FinalMd5;
        SELECT SUBSTRING_INDEX(tag_path, '/', -1) INTO @Finalstring;
        SELECT LEFT(@FinalString, 23) INTO @FinalString;
        SELECT CONCAT(@FinalMd5, '-', @FinalString) INTO @Final;
        IF @Final != meta_tag THEN
            SELECT JSON_OBJECT('id', @TagId, 'message', 'Tag mismatches with the given tag_path')
            INTO @tp;
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @tp;
        ELSE
            IF ((SELECT COUNT(id) FROM cfe_18.tags WHERE tag = @Final) = 0) THEN
                INSERT INTO cfe_18.tags(tag)
                VALUES (@Final);
                SELECT LAST_INSERT_ID() INTO @TagId;
            ELSE
                SELECT id INTO @TagId FROM cfe_18.tags WHERE tag = @Final;
            END IF;
        END IF;
    END IF;

    IF tag_path IS NULL THEN
        IF ((SELECT COUNT(id) FROM cfe_18.tags WHERE tag = meta_tag) = 0) THEN
            INSERT INTO cfe_18.tags(tag)
            VALUES (meta_tag);
            SELECT LAST_INSERT_ID() INTO @TagId;
        ELSE
            SELECT id INTO @TagId FROM cfe_18.tags WHERE tag = meta_tag;
        END IF;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_18.captureSourcetype WHERE captureSourceType = source_type) = 0) THEN
        INSERT INTO cfe_18.captureSourcetype(captureSourceType)
        VALUES (source_type);
        SELECT LAST_INSERT_ID() INTO @SourceId;
    ELSE
        SELECT id INTO @SourceId FROM cfe_18.captureSourcetype WHERE captureSourceType = source_type;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_18.captureIndex WHERE captureIndex = capture_index) = 0) THEN
        INSERT INTO cfe_18.captureIndex(captureIndex)
        VALUES (capture_index);
        SELECT LAST_INSERT_ID() INTO @IndexId;
    ELSE
        SELECT id INTO @IndexId FROM cfe_18.captureIndex WHERE captureIndex = capture_index;
    END IF;


    IF ((SELECT COUNT(id) FROM cfe_18.application WHERE app = application) = 0) THEN
        INSERT INTO cfe_18.application(app)
        VALUES (application);
        SELECT LAST_INSERT_ID() INTO @ApplicationId;
    ELSE
        SELECT id INTO @ApplicationId FROM cfe_18.application WHERE app = application;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_18.retentionTime WHERE retention = retention_time) = 0) THEN
        INSERT INTO cfe_18.retentionTime(retention)
        VALUES (retention_time);
        SELECT LAST_INSERT_ID() INTO @RetentionId;
    ELSE
        SELECT id INTO @RetentionId FROM cfe_18.retentionTime WHERE retention = retention_time;
    END IF;

    IF ((SELECT COUNT(id) FROM cfe_18.category WHERE category = meta_category) = 0) THEN
        INSERT INTO cfe_18.category(category)
        VALUES (meta_category);
        SELECT LAST_INSERT_ID() INTO @CategoryId;
    ELSE
        SELECT id INTO @CategoryId FROM cfe_18.category WHERE category = meta_category;
    END IF;

    IF ((SELECT COUNT(id) FROM flow.L7 WHERE app_protocol = protocol) = 0) THEN
        SELECT JSON_OBJECT('id', NULL, 'message', 'L7 is missing') INTO @L7;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @L7;
    ELSE
        SELECT id INTO @L7Id FROM flow.L7 WHERE app_protocol = protocol;
    END IF;

    IF ((SELECT COUNT(id) FROM flow.flows WHERE name = flow) = 0) THEN
        SELECT JSON_OBJECT('id', NULL, 'message', 'Flow is missing') INTO @Flow;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = @Flow;
    ELSE
        SELECT id INTO @FlowId FROM flow.flows WHERE name = flow;
    END IF;

    -- check if capture_meta_file row exists
    IF ((SELECT COUNT(cmf.id)
         FROM capture_meta_file cmf
         WHERE capturePath = capture_path
           AND (tagPath = tag_path OR (tag_path IS NULL AND tagPath IS NULL))
           AND processing_type_id = file_processing_id) = 0) THEN
        -- insert new one
        INSERT INTO cfe_18.capture_type(capture_type)
        VALUES ('CFE');
        SELECT LAST_INSERT_ID() INTO @CaptureTypeId;
        INSERT INTO cfe_18.capture_meta_file(id, capturePath, tagPath, processing_type_id, capture_type)
        VALUES (@CaptureTypeId, capture_path, tag_path, file_processing_id, 'CFE');
    ELSE
        -- if exists then select capture_meta_file ID for later
        SELECT cmf.id
        INTO @CaptureTypeId
        FROM capture_meta_file cmf
        WHERE capturePath = capture_path
          AND (tagPath = tag_path OR (tag_path IS NULL AND tagPath IS NULL))
          AND processing_type_id = file_processing_id;
    END IF;

    -- check if capture exists with current values
    IF ((SELECT COUNT(id)
         FROM cfe_18.capture_definition
         WHERE tag_id = @TagId
           AND application_id = @ApplicationId
           AND captureIndex_id = @IndexId
           AND retentionTime_id = @RetentionId
           AND captureSourcetype_id = @SourceId
           AND category_id = @CategoryId
           AND capture_type = 'CFE'
           AND capture_type_id = @CaptureTypeId
           AND L7_id = @L7Id
           AND flow_id = @FlowId) = 0) THEN

        -- insert new one
        INSERT INTO cfe_18.capture_definition (tag_id, application_id, captureIndex_id, retentionTime_id,
                                               captureSourcetype_id, category_id, capture_type, capture_type_id, L7_id,
                                               flow_id)
        VALUES (@TagId, @ApplicationId, @IndexId, @RetentionId, @SourceId, @CategoryId, 'CFE', @CaptureTypeId, @L7Id,
                @FlowId);
        SELECT LAST_INSERT_ID() INTO @c_id;
    ELSE
        -- select existing one
        SELECT id
        INTO @c_id
        FROM cfe_18.capture_definition
        WHERE tag_id = @TagId
          AND application_id = @ApplicationId
          AND captureIndex_id = @IndexId
          AND retentionTime_id = @RetentionId
          AND captureSourcetype_id = @SourceId
          AND category_id = @CategoryId
          AND capture_type = 'CFE'
          AND capture_type_id = @CaptureTypeId
          AND L7_id = @L7Id
          AND flow_id = @FlowId;
    END IF;
    COMMIT;
    -- return ID
    SELECT @c_id AS id;

END;
//
DELIMITER ;