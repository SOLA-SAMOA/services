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
package org.sola.services.common.test.repository;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

/**
 *
 * @author soladev
 */
public class MockSqlSession implements SqlSession {

    private Map<String, Object> mappers = new HashMap<String, Object>();

    @Override
    public Object selectOne(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Object selectOne(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List selectList(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List selectList(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List selectList(String string, Object o, RowBounds rb) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map selectMap(String string, String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map selectMap(String string, Object o, String string1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map selectMap(String string, Object o, String string1, RowBounds rb) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void select(String string, Object o, ResultHandler rh) {
    }

    @Override
    public void select(String string, ResultHandler rh) {
    }

    @Override
    public void select(String string, Object o, RowBounds rb, ResultHandler rh) {
    }

    @Override
    public int insert(String string) {
        return 1;
    }

    @Override
    public int insert(String string, Object o) {
        return 1;
    }

    @Override
    public int update(String string) {
        return 1;
    }

    @Override
    public int update(String string, Object o) {
        return 1;
    }

    @Override
    public int delete(String string) {
        return 1;
    }

    @Override
    public int delete(String string, Object o) {
        return 1;
    }

    @Override
    public void commit() {
    }

    @Override
    public void commit(boolean bln) {
    }

    @Override
    public void rollback() {
    }

    @Override
    public void rollback(boolean bln) {
    }

    @Override
    public void close() {
    }

    @Override
    public void clearCache() {
    }

    @Override
    public Configuration getConfiguration() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        if (mappers.containsKey(type.getSimpleName())) {
            return (T) mappers.get(type.getSimpleName());
        } else {
            throw new RuntimeException("Mock Mapper " + type.getSimpleName() + " not loaded.");
        }
    }

    @Override
    public Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
   public List<BatchResult> flushStatements() {    
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public <T, U extends T> void addMapper(Class<T> typeKey, U mockMapper) {
        mappers.put(typeKey.getSimpleName(), mockMapper);
    }
}
