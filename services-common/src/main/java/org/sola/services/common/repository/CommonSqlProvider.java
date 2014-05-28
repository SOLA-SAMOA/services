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

import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import java.util.ArrayList;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;
import java.util.List;
import java.util.Map;
import org.sola.services.common.repository.entities.ColumnInfo;
import org.sola.services.common.repository.entities.AbstractEntity;
import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Provides methods for generating common SQL statements that can be used in Mybatis mapper classes
 * through Provider annotations. The SQL statements are created using the Mybatis SqlBuilder methods. 
 * @author soladev
 */
public class CommonSqlProvider {

    public static final String PARAM_ENTITY_CLASS = "sql_param_entityClass";
    public static final String PARAM_EXCLUDE_LIST = "sql_param_excludeList";
    public static final String PARAM_LANGUAGE_CODE = "sql_param_languageCode";
    public static final String PARAM_WHERE_PART = "sql_param_where";
    public static final String PARAM_LIMIT_PART = "sql_param.limit";
    public static final String PARAM_ORDER_BY_PART = "sql_param_orderBy";
    public static final String PARAM_SELECT_PART = "sql_param_select";
    public static final String PARAM_FROM_PART = "sql_param_from";
    public static final String PARAM_QUERY = "sql_param_query";

    /**
     * Uses the column information from the entityClass to generate the appropriate SELECT clause
     * for the entity. This includes aliasing columns that do not match the entity field name with 
     * the entity field name to ensure Mybatis is able to use its default column to field mapping logic.
     * <p> This method also ensures any fields marked with the {@linkplain Localized} annotation call
     * the get_translation function and also allows specified fields to be excluded from the SELECT
     * clause</p>
     * @param <T> Generic type for the entity. It must extent {@linkplain AbstractReadOnlyEntity}.
     * @param entityClass The entity class to build the select for. 
     * @param localized If true, the call to the get_translation function will be included for
     * any columns annotated with the {@linkplain Localized} annotation. Note that this assumes
     * the Mapper function includes a parameter called {@code language}.  
     * @param excludeList A list of field names to exclude from the SELECT clause. 
     */
    public static <T extends AbstractReadOnlyEntity> void buildSelectClauseSql(Class<T> entityClass,
            Boolean localized, List<String> excludeList) {

        if (excludeList == null) {
            excludeList = new ArrayList<String>();
        }

        for (ColumnInfo columnInfo : RepositoryUtility.getColumns(entityClass)) {
            if (!excludeList.contains(columnInfo.getFieldName())) {
                if (localized && columnInfo.isLocalized()) {
                    SELECT("get_translation(" + columnInfo.getColumnName() + ", #{"
                            + PARAM_LANGUAGE_CODE + "}) as " + columnInfo.getColumnName().toLowerCase());
                } else if (columnInfo.getOnSelectFunction() != null) {
                    SELECT(columnInfo.getOnSelectFunction() + " as " + columnInfo.getColumnName().toLowerCase());
                } else {
                    SELECT(columnInfo.getColumnName());
                }
            }
        }
    }

    /**
     * Creates the UPDATE command based on the column information of the entity. Fields 
     * that are marked with {@code updatable = false} in the {@linkplain javax.persistence.Column} 
     * annotation are excluded from the UPDATE statement. 
     * <p>The where clause in the UPDATE statement in constrained using all id columns (i.e. 
     * columns marked with the {@linkplain javax.persistence.Id} annotation). 
     * @param <T> The generic type for the entity. Must extend {@linkplain AbstractEntity}
     * @param entity The entity to build the update statement for. 
     * @return The UPDATE statement to execute for the entity. 
     */
    public static <T extends AbstractEntity> String buildUpdateSql(T entity) {

        BEGIN();
        UPDATE(entity.getTableName());
        for (ColumnInfo columnInfo : entity.getColumns()) {
            if (columnInfo.isUpdatable()) {
                // Only include fields that are updatable in the update statement. 
                if (columnInfo.getOnChangeFunction() == null) {
                    SET(columnInfo.getColumnName() + "=#{" + columnInfo.getFieldName() + "}");
                } else {
                    // Use the specified database function to update the data for this field.
                    SET(columnInfo.getColumnName() + " = " + columnInfo.getOnChangeFunction());
                }
            }
        }
        for (ColumnInfo idColumnInfo : entity.getIdColumns()) {
            WHERE(idColumnInfo.getColumnName() + "=#{" + idColumnInfo.getFieldName() + "}");
        }
        return SQL();
    }

    /**
     * Creates the INSERT command based on the column information of the entity. Fields with null values
     * and fields marked with {@code insertable = false} in the {@linkplain javax.persistence.Column} 
     * annotation are excluded from the INSERT statement.  
     * <p>Excluding fields with null values ensures any default values set by the database will be
     * correctly assigned. Where a field is omitted from the insert either because it is null or 
     * because it is marked {@code insertable = false}, the entity will be flagged to force refresh
     * after the insert to pick up any default values that may have been set by the database. 
     * @param <T> The generic type for the entity. Must extend {@linkplain AbstractEntity}
     * @param entity The entity to build the insert statement for. 
     * @return The INSERT statement to execute for the entity. 
     */
    public static <T extends AbstractEntity> String buildInsertSql(T entity) {

        BEGIN();
        INSERT_INTO(entity.getTableName());
        for (ColumnInfo columnInfo : entity.getColumns()) {
            // Don't insert null values as sending null will prevent any database default values
            // from being assigned. Also do not send fields excluded from the insert. 
            if (entity.getEntityFieldValue(columnInfo) != null && columnInfo.isInsertable()) {
                if (columnInfo.getOnChangeFunction() == null) {
                    VALUES(columnInfo.getColumnName(), "#{" + columnInfo.getFieldName() + "}");
                } else {
                    // Use the specified database function to insert the data for this field.
                    VALUES(columnInfo.getColumnName(), columnInfo.getOnChangeFunction());
                }
            } else {
                // There may be database defaults set for some values, so force refresh of the 
                // entity after the save has completed. 
                entity.setForceRefresh(true);
            }
        }
        return SQL();
    }

    /**
     * Uses the column information of the entity to create a DELETE command. The DELETE statement
     * is constrained by all id columns (i.e. those columns marked with the 
     * {@linkplain javax.persistence.Id} annotation.)
     * @param <T> The generic type for the entity. Must extend {@linkplain AbstractEntity}
     * @param entity The entity to build the DELETE statement for. 
     * @return The DELETE statement to execute for the entity. 
     */
    public static <T extends AbstractEntity> String buildDeleteSql(T entity) {

        BEGIN();
        DELETE_FROM(entity.getTableName());
        for (ColumnInfo idColumnInfo : entity.getIdColumns()) {
            WHERE(idColumnInfo.getColumnName() + "=#{" + idColumnInfo.getFieldName() + "}");
        }
        return SQL();
    }

    /**
     * Builds a complete SELECT statement for the entity using the column information. This select
     * is constrained by the value of the id field. 
     * @param <T> The generic type of the entity to build the select statement for. Must extend
     * {@linkplain AbstractEntity}. 
     * @param parms HashMap of parameters for the select. Recognized parameters are; 
     * <p>{@code entityClass} - The entity class to build the query for. Required parameter.</p> 
     * <p>{@code id} - The value of the id to use for the select. Required parameter.</p> 
     * <p>{@code languageCode} - The language to translate the localized fields into translated.</p> 
     * <p>{@code excludeList} - The list of fields that should be excluded from the SELECT clause. 
     *                          Optional parameter.</p> 
     * @return The SELECT statement for the entity. 
     */
    public static <T extends AbstractEntity> String buildGetEntitySql(Map params) {

        String sql = (String) params.get(PARAM_QUERY);
        if (sql == null || sql.isEmpty()) {
            Class<T> entityClass = (Class<T>) params.get(PARAM_ENTITY_CLASS);
            if (entityClass == null) {
                // Entity Class is null throw and exception as the Select generation will fail anyway
                throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                        new Object[]{"Entity class has not been provided for SQL SELECT generation."});
            }
            String fromClause = (String) params.get(PARAM_FROM_PART);
            String whereClause = (String) params.get(PARAM_WHERE_PART);
            String orderByClause = (String) params.get(PARAM_ORDER_BY_PART);
            Boolean localized = false;
            List<String> excludeList = new ArrayList();

            if (params.containsKey(PARAM_LANGUAGE_CODE)) {
                localized = true;
            }

            if (params.containsKey(PARAM_EXCLUDE_LIST)) {
                excludeList = (List<String>) params.get(PARAM_EXCLUDE_LIST);
            }

            BEGIN();
            String selectPart = (String)params.get(PARAM_SELECT_PART);
            if (selectPart == null){
                buildSelectClauseSql(entityClass, localized, excludeList);
            }else{
                SELECT(selectPart);
            }
            if (fromClause != null && !fromClause.isEmpty()) {
                FROM(fromClause);
            } else {
                FROM(RepositoryUtility.getTableName(entityClass));
            }
            if (whereClause != null && !whereClause.isEmpty()) {
                WHERE(whereClause);
            }
            if (orderByClause != null && !orderByClause.isEmpty()) {
                ORDER_BY(orderByClause);
            }

            sql = SQL();

            if (params.containsKey(PARAM_LIMIT_PART)) {
                // Limit the number of results to return. 
                sql = sql + " LIMIT " + params.get(PARAM_LIMIT_PART).toString();
            }
        }
        return sql;
    }

    public static <T extends AbstractEntity> String buildGetEntityListSql(Map params) {
        Class<T> entityClass = (Class<T>) params.get(PARAM_ENTITY_CLASS);
        if(entityClass!=null && !params.containsKey(PARAM_ORDER_BY_PART) && 
                RepositoryUtility.getSorterExpression(entityClass) != null){
            params.put(PARAM_ORDER_BY_PART, RepositoryUtility.getSorterExpression(entityClass));
        }
        return buildGetEntitySql(params);
    }

    public static String buildSelectSql(Map params) {

        // Check if a full query has been provided
        String sql = (String) params.get(PARAM_QUERY);

        if (sql == null || sql.isEmpty()) {

            String fromClause = (String) params.get(PARAM_FROM_PART);
            String whereClause = (String) params.get(PARAM_WHERE_PART);
            String orderByClause = (String) params.get(PARAM_ORDER_BY_PART);

            BEGIN();
            SELECT((String) params.get(PARAM_SELECT_PART));

            if (fromClause != null && !fromClause.isEmpty()) {
                // From may not be provided if selecting from a sequence or system table. 
                FROM((String) params.get(PARAM_FROM_PART));
            }

            if (whereClause != null && !whereClause.isEmpty()) {
                WHERE(whereClause);
            }
            if (orderByClause != null && !orderByClause.isEmpty()) {
                ORDER_BY(orderByClause);
            }

            sql = SQL();

            if (params.containsKey(PARAM_LIMIT_PART)) {
                // Limit the number of results to return. 
                sql = sql + " LIMIT " + params.get(PARAM_LIMIT_PART).toString();
            }
        }

        return sql;
    }

    public static String buildSql(Map params) {
        String sql = (String) params.get(PARAM_QUERY);
        return sql;
    }
}
