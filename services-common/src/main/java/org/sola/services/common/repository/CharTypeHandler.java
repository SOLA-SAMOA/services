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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.repository;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * Custom type handler for the Character data type to overcome a defect in Mybatis. This type handler
 * must be registered in the Mybatis configuration file as follows
 * 
 * <pre>
 * {@code
  <typeHandlers>
        <!-- Custom type handler for dealing with the Character data type to overcome a Mybatis defect.-->
        <typeHandler javaType='java.lang.Character' handler='org.sola.services.common.repository.CharTypeHandler'/>
  </typeHandlers>}
 * </pre>
 * This type handler can be registered against the boxed Character data type, but not the 
 * primitive char data type. Ensure all character fields in the entities use the java.lang.Character 
 * data type and not the primitive char data type. 
 * @author soladev
 */
public class CharTypeHandler implements TypeHandler {

    @Override
    public Object getResult(ResultSet rs, String columnName) throws SQLException {
        String s = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            Character c = s.charAt(0);
            return new Character(c);
        }
    }

    @Override
    public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String s = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            Character c = s.charAt(0);
            return new Character(c);
        }
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException { 
        ps.setString(i, Character.toString(((Character) parameter).charValue()));
    }

}
