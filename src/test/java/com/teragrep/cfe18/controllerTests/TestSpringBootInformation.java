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
package com.teragrep.cfe18.controllerTests;


import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.flyway.clean-disabled=false")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MigrateDatabaseExtension.class)
class TestSpringBootInformation {


    protected static String DBUNIT_DRIVER_CLASS = System.getProperty("tests.dbunit.driver.class");
    protected static String DBUNIT_CONNECTION_URL = System.getProperty("tests.dbunit.connection.url");
    protected static String DBUNIT_USERNAME = System.getProperty("tests.dbunit.username");
    protected static String DBUNIT_PASSWORD = System.getProperty("tests.dbunit.password");

    protected String readWriteToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiaWF0IjoxNzkyMDY3NzI4fQ.O-l6AK4abKQod211_sFe8Fcc-jJEMeRDDqCOFoXvAg5p0aqlg-Aqqekhp56D3UoxHm8pxkqaGqBrnNGOrHYER5GGyguR0bQSmCsJbROJg0vJKr7iZzqOZzPEj-4KcOfWS1ZlAmU2fmmws5LJ-7ckFqdrhbzEQclanReWPtIwVqpITTtoH3KaacC7KL7UGntvbP2i5YKV8dI2oGsoOM1W5ADUuR24VDh0aumqMuVhJ8drkLJiqTHSCEOegCSkXZCUgC8t8kYtvaPNkOTEeLcMPYM2trjx0AnpPbvdMoo8TgDLQq80OoLZd5pSyIbZmBWGJmLQV7rZdxZo2PpjgOamlQ";
    protected String readToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWFkaW5nU3ViamVjdCIsImlhdCI6MTc5MjA2NzcyOH0.PqYVnKJAERh9Lxatg9hMo6dYUMz_LOzwnhUBSzE-i3i2V7RbJWdlj1JbhcLKd1-IUXE0RCJBsf1e2MofjLL65NUuA_cuTPmS9B1UmyCWI4SBuNXPxUFoPk-xCAEpEWBzfzVR9uhHrqKfFc4mQ3TLI_CwfanpX1fmtmtnm3RLnh0jJ5c2SfcxMs44MSdPkbiF0XyVojo9L6ue0eiC8WxD1wifhwc4c30PzbJ53LVB8E12xrSZ6LD2CYMn39sUgOn15h9OLBTSUnlFx3YO0kxqyIgu6RrtJwWLP34RgssojOYDiwmuP68Lh2Q34YYRtp_aPhCWaMM--DJJf7R9vFD8tA";
}
