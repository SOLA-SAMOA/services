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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sola.services.common.repository.entities.AbstractCodeEntity;
import org.sola.services.common.repository.entities.AbstractEntity;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;
import org.sola.services.common.repository.entities.ChildEntityInfo;

/**
 *
 * @author soladev
 */
public interface CommonRepository {

    /**
     * The default name for the mybatis Configuration File. 
     */
    public static final String CONNECT_CONFIG_FILE_NAME = "mybatisConnectionConfig.xml";

    void setDbConnectionManager(DatabaseConnectionManager dbConnectionManager);

    <T extends AbstractEntity> T saveEntity(T entity);

    <T> T getScalar(Class<T> scalarClass, Map params);

    <T> List<T> getScalarList(Class<T> scalarClass, Map params);

    List<String> getChildIdList(ChildEntityInfo childInfo, String parentId);

    <T extends AbstractReadOnlyEntity> T refreshEntity(T entity);

    <T extends AbstractReadOnlyEntity> T getEntity(Class<T> entityClass, Map params);

    <T extends AbstractReadOnlyEntity> T getEntity(Class<T> entityClass, String id);

    <T extends AbstractReadOnlyEntity> T getEntity(Class<T> entityClass, String id, String lang);

    <T extends AbstractReadOnlyEntity> T getEntity(Class<T> entityClass,
            String whereClause, Map params);

    <T extends AbstractReadOnlyEntity> List<T> getEntityList(Class<T> entityClass,
            Map params);

    <T extends AbstractReadOnlyEntity> List<T> getEntityList(Class<T> entityClass);

    <T extends AbstractReadOnlyEntity> List<T> getEntityList(Class<T> entityClass,
            String whereClause, Map params);

    <T extends AbstractReadOnlyEntity, V extends AbstractReadOnlyEntity> List<V> getChildEntityList(
            T parentEntity, Class<V> childEntityClass, ChildEntityInfo childInfo);

    <T extends AbstractCodeEntity> List<T> getCodeList(Class<T> codeListClass,
            String languageCode);

    <T extends AbstractCodeEntity> T getCode(Class<T> codeListClass,
            String entityCode, String languageCode);

    <T extends AbstractReadOnlyEntity> List<T> getEntityListByIds(Class<T> entityClass,
            List<String> ids);

    <T extends AbstractReadOnlyEntity> List<T> getEntityListByIds(Class<T> entityClass,
            List<String> ids, Map params);

    void clearLoadInhibitors();

    void setLoadInhibitors(Class<?>[] entityClasses);

    ArrayList<HashMap> executeFunction(Map params);

    <T extends AbstractReadOnlyEntity> List<T> executeFunction(Map params, Class<T> entityClass);

    ArrayList<HashMap> executeSql(Map params);
    
    int bulkUpdate(Map params); 
}
