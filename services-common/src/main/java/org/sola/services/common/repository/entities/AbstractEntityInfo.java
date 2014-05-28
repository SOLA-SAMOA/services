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
 * Provides fields common to descendent EntityInfo classes. 
 * @see ColumnInfo
 * @see ChildEntityInfo
 * @author soladev
 */
public abstract class AbstractEntityInfo {

    private String fieldName;
    private Class<?> fieldType;

    /** 
     * The name of the entity field this entity information relates to. 
     * @return 
     */
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * The type of the field. This could be any type including a primitive type through to a List
     * type. 
     * @return 
     */
    public Class<?> getFieldType() {
        return fieldType;
    }

    public void setFieldType(Class<?> fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Returns the name of the getter for this field based on the field name. The getter method is
     * the field name prefixed with "get", unless the field is a boolean type, in which case the
     * field name is prefixed with "is". 
     */
    public String getterName() {
        String getterPrefix = "get";
        if (getFieldType() == boolean.class) {
            getterPrefix = "is";
        }
        return getterPrefix + getFieldName().substring(0, 1).toUpperCase() + getFieldName().substring(1);
    }

    /**
     * Returns the name of the setter for this field based on the field name. The setter method is
     * the field name prefixed with "set". 
     */
    public String setterName() {
        return "set" + getFieldName().substring(0, 1).toUpperCase() + getFieldName().substring(1);
    }
}
