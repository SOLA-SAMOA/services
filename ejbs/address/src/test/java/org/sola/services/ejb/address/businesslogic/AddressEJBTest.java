/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.ejb.address.businesslogic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.address.repository.entities.Address;

/**
 *
 * @author soladev
 */
public class AddressEJBTest extends AbstractEJBTest {

    private static String ADDR_MODULE_NAME = "sola-address-1_0-SNAPSHOT";

    public AddressEJBTest() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Validates the Dynamic SQL statements generated for the Address entity
     */
    @Test
    public void testAddressDynamicSQL() throws Exception {

        System.out.println(">>> Test Address Dynamic SQL");
        
        // Table name
        System.out.println("Address tablename");
        Address addr = new Address();
        addr.setDescription("TestAddr");
        addr.setRowVersion(1);
        addr.setChangeUser("Jim");
        assertEquals("address.address", addr.getTableName());

        // Dynamic Insert
        System.out.println("Address INSERT Sql");
        String addressInsert = "INSERT INTO address.address" + System.getProperty("line.separator")
                + " (id, description, rowversion, change_user, rowidentifier)" + System.getProperty("line.separator")
                + "VALUES (#{id}, #{description}, #{rowVersion}, #{changeUser}, #{rowId})";
        assertEquals(addressInsert, CommonSqlProvider.buildInsertSql(addr));

        // Dynamic Update
        System.out.println("Address UPDATE Sql");
        String addressUpdate = "UPDATE address.address" + System.getProperty("line.separator")
                + "SET id=#{id}, description=#{description}, ext_address_id=#{extAddressId}, "
                + "rowversion=#{rowVersion}, change_user=#{changeUser}, rowidentifier=#{rowId}" + System.getProperty("line.separator")
                + "WHERE (id=#{id})";
        assertEquals(addressUpdate, CommonSqlProvider.buildUpdateSql(addr));

        // Dynamic Delete
        System.out.println("Address DELETE Sql");
        String addressDelete = "DELETE FROM address.address" + System.getProperty("line.separator")
                + "WHERE (id=#{id})";
        assertEquals(addressDelete, CommonSqlProvider.buildDeleteSql(addr));

        // Dynamic findById
        System.out.println("Address findById Sql");
        String addressSelectById = "SELECT id, description, ext_address_id, rowversion, change_user, rowidentifier" + System.getProperty("line.separator")
                + "FROM address.address" + System.getProperty("line.separator")
                + "WHERE (id = #{id})";
        Map paramMap = new HashMap();
        paramMap.put(CommonSqlProvider.PARAM_ENTITY_CLASS, Address.class);
        paramMap.put(CommonSqlProvider.PARAM_WHERE_PART, "id = #{id}");
        assertEquals(addressSelectById, CommonSqlProvider.buildGetEntitySql(paramMap));

    }
    
    
}
