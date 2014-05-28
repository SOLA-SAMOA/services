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
package org.sola.services.common.repository.entities;

/**
 * Used to capture the details from the {@linkplain javax.persistence.Column},
 * {@linkplain javax.persistence.Id}, 
 * {@linkplain org.sola.services.common.repository.AccessFunctions} and 
 * {@linkplain org.sola.services.common.repository.Localized} annotations to assist processing
 * of entity fields into table columns.  
 * @author soladev
 */
public class ColumnInfo extends AbstractEntityInfo {

    private String columnName;
    private Boolean idColumn;
    private Boolean localized;
    private Boolean insertable;
    private Boolean updatable;
    private String onSelectFunction;
    private String onChangeFunction;

    /**
     * Default constructor. 
     */
    public ColumnInfo() {
    }

    /**
     * 
     * @param columnName The database table column the field is mapped to. 
     * @param fieldName The name of the field on the entity mapped to the column. 
     * @param fieldType The data type of the field 
     * @param idColumn Flag that indicates if the columns is an Id column
     * @param localized Flag to indicate if the text of the column can be localized using the
     * get_translation database function. 
     * @param insertable Flag to indicate if the data in the field can be included in an insert
     * for the entity. 
     * @param updatable Flag to indicate if the data in the field can be included in an update 
     * for the entity. 
     */
    public ColumnInfo(String columnName, String fieldName, Class<?> fieldType,
            Boolean idColumn, Boolean localized, Boolean insertable, Boolean updatable) {
        this.columnName = columnName;
        setFieldName(fieldName);
        setFieldType(fieldType);
        this.idColumn = idColumn;
        this.localized = localized;
        this.insertable = insertable;
        this.updatable = updatable;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Boolean isIdColumn() {
        return idColumn;
    }

    public void setIdColumn(Boolean idColumn) {
        this.idColumn = idColumn;
    }

    public Boolean isLocalized() {
        return localized;
    }

    public void setLocalized(Boolean localized) {
        this.localized = localized;
    }

    public Boolean isInsertable() {
        return insertable;
    }

    public void setInsertable(Boolean insertable) {
        this.insertable = insertable;
    }

    public Boolean isUpdatable() {
        return updatable;
    }

    public void setUpdatable(Boolean updatable) {
        this.updatable = updatable;
    }

    /**
     * Database function to use for the field when inserting or updating the entity. 
     */
    public String getOnChangeFunction() {
        return onChangeFunction;
    }

    public void setOnChangeFunction(String onChangeFunction) {
        this.onChangeFunction = onChangeFunction;
    }

    /**
     * Database function to use for the field when selecting the entity. 
     */
    public String getOnSelectFunction() {
        return onSelectFunction;
    }

    public void setOnSelectFunction(String onSelectFunction) {
        this.onSelectFunction = onSelectFunction;
    }
}
