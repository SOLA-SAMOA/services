/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.sola.services.common.repository.entities.AbstractEntity;

/**
 *
 * @author soladev
 */
public interface CommonMapper {

    /**
     * Refer to {@linkplain CommonSqlProvider#buildInsertSql}.
     */
    @InsertProvider(type = CommonSqlProvider.class, method = "buildInsertSql")
    <T extends AbstractEntity> int insert(T entity);

    /**
     * Refer to {@linkplain CommonSqlProvider#buildUpdateSql}.
     */
    @UpdateProvider(type = CommonSqlProvider.class, method = "buildUpdateSql")
    <T extends AbstractEntity> int update(T entity);

    /**
     * Refer to {@linkplain CommonSqlProvider#buildDeleteSql}.
     */
    @DeleteProvider(type = CommonSqlProvider.class, method = "buildDeleteSql")
    <T extends AbstractEntity> int delete(T entity);

    /**
     * Refer to {@linkplain CommonSqlProvider#buildGetEntitySql}.
     */
    @SelectProvider(type = CommonSqlProvider.class, method = "buildGetEntitySql")
    HashMap getEntity(Map params);

    /**
     * Refer to {@linkplain CommonSqlProvider#buildGetEntitySql}.
     */
    @SelectProvider(type = CommonSqlProvider.class, method = "buildGetEntityListSql")
    ArrayList<HashMap> getEntityList(Map params);

    @SelectProvider(type = CommonSqlProvider.class, method = "buildSelectSql")
    Object getScalar(Map params);

    @SelectProvider(type = CommonSqlProvider.class, method = "buildSelectSql")
    ArrayList<Object> getScalarList(Map params);

    @SelectProvider(type = CommonSqlProvider.class, method = "buildSql")
    ArrayList<HashMap> executeSql(Map params);

    /**
     * Refer to {@linkplain CommonSqlProvider#buildSql}.
     */
    @UpdateProvider(type = CommonSqlProvider.class, method = "buildSql")
    int bulkUpdate(Map params);
}
