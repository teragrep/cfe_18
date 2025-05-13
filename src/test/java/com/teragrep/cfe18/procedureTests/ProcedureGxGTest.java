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
package com.teragrep.cfe18.procedureTests;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.jupiter.api.Assertions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/*
Tests for host group x capture group procedure
Uses dataset procedureGxGTestData.xml which is copied version of triggerTestData.xml
 */
public class ProcedureGxGTest extends DBUnitbase {

    public ProcedureGxGTest(String name) {
        super(name);
    }

    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(Files.newInputStream(Paths.get("src/test/resources/XMLProcedureGxG/procedureGxGTestData.xml")));
    }


    public void testProcedureAddLinkageSuccess() throws Exception {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new File("src/test/resources/XMLProcedureGxG/procedureGxGTestDataExpected1.xml"));
        ITable expectedTable1 = expectedDataSet.getTable("cfe_18.host_groups_x_capture_def_group");

        CallableStatement stmnt = conn.prepareCall("{call cfe_18.insert_g_x_g(?,?)}");
        stmnt.setInt(1, 1); //Host group id
        stmnt.setInt(2, 6); //Capture group id
        stmnt.execute();

        ITable actualTable1 = databaseConnection.createQueryTable("result", "select * from cfe_18.host_groups_x_capture_def_group");

        Assertion.assertEqualsIgnoreCols(expectedTable1, actualTable1, new String[]{"id"});

    }


    public void testProcedureGxGNoHost() throws Exception {
        SQLException state = Assertions.assertThrows(SQLException.class, () -> {
            CallableStatement stmnt = conn.prepareCall("{call cfe_18.insert_g_x_g(?,?)}");
            stmnt.setInt(1, 100);
            stmnt.setInt(2, 2);
            stmnt.execute();
        });
        Assertions.assertEquals("45000", state.getSQLState());
    }

    public void testProcedureGxGNoCapture() throws Exception {
        SQLException state = Assertions.assertThrows(SQLException.class, () -> {
            CallableStatement stmnt = conn.prepareCall("{call cfe_18.insert_g_x_g(?,?)}");
            stmnt.setInt(1, 1);
            stmnt.setInt(2, 200);
            stmnt.execute();
        });
        Assertions.assertEquals("45000", state.getSQLState());
    }

    /*
    Testataan että tieto voidaan kerätä oikeassa muodossa käyttäen capture_group nimeä
 */

    public void testProcedureGxGRetrieveById() throws Exception {
        CallableStatement stmnt = conn.prepareCall("{CALL cfe_18.select_linkage(?,?)}");
        stmnt.setInt(1, 1);
        stmnt.setString(2, null);
        ResultSet rs = stmnt.executeQuery();
        rs.next(); // Forward to next
        Assertions.assertEquals(1, rs.getInt("host_group_id"));
        Assertions.assertEquals(1, rs.getInt("capture_group_id"));
        Assertions.assertEquals(1, rs.getInt("id"));
    }



    /*
    Testataan että linkitys ei onnistu jos capture type ei matchaa host typen kanssa
    Custom error = '42000', 'type mismatch between host group and cature group'.
     */
    public void testProcedureGxGTypeMismatch() throws Exception {
        SQLException state = Assertions.assertThrows(SQLException.class, () -> {
            CallableStatement stmnt = conn.prepareCall("{CALL cfe_18.insert_g_x_g(?,?)}");
            stmnt.setInt(1, 1);
            stmnt.setInt(2, 7);
            stmnt.execute();
        });

        Assertions.assertEquals("45000", state.getSQLState());
    }
}