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

import java.lang.reflect.ParameterizedType;
import org.sola.services.common.ejbs.AbstractEJBLocal;

/**
 * Used to capture the details from the {@linkplain org.sola.services.common.repository.ChildEntity},
 * {@linkplain org.sola.services.common.repository.ChildEntityList} and 
 * {@linkplain org.sola.services.common.repository.ExternalEJB} annotations to assist load and save 
 * processing of the entity. 
 * @author soladev
 */
public class ChildEntityInfo extends AbstractEntityInfo {

    private Boolean insertBeforeParent;
    private Boolean cascadeDelete;
    private ParameterizedType parameterTypes;
    private String parentIdField;
    private String childIdField;
    private Class<? extends AbstractEntity> manyToManyClass;
    private Boolean readOnly;
    private Class<? extends AbstractEJBLocal> EJBLocalClass;
    private String loadMethod;
    private String saveMethod;

    /**
     * Default constructor. 
     */
    public ChildEntityInfo() {
    }

    /** 
     * Constructor used to capture details for One to One relationships.
     * @param fieldName The name of the field on the entity that references the child entity. 
     * @param fieldType The data type of the field (i.e. Class of the child entity)
     * @param insertBeforeParent Flag to indicate if the child entity is to be inserted before
     *                           the parent entity. 
     * @param parentIdField The name of the field on the child entity that contains the parent 
     *                      id value. Required if insertBeforeParent = false. 
     * @param childIdField The name of the field on the parent entity that contains the child id 
     *                     value. Required if insertBeforeParent = true. 
     * @param readOnly Used to indicate the child entity should not be included in the save
     *                 of the parent entity. 
     */
    public ChildEntityInfo(String fieldName, Class<?> fieldType, Boolean insertBeforeParent,
            String parentIdField, String childIdField, boolean readOnly) {
        setFieldName(fieldName);
        setFieldType(fieldType);
        this.insertBeforeParent = insertBeforeParent;
        this.readOnly = readOnly;
        this.cascadeDelete = false;


        if (!parentIdField.isEmpty()) {
            this.parentIdField = parentIdField;
        }
        if (!childIdField.isEmpty()) {
            this.childIdField = childIdField;
        }
    }

    /**
     * Constructor used for One to Many and Many to Many relationships. 
     * @param fieldName The name of the field on the entity that references the child entity list. 
     * @param fieldType The data type of the field (i.e. generic parameterized list)
     * @param parameterTypes The ParameterizedType of the field. Required to determine the actual
     *                       type of the child entity class.  
     * @param parentIdField  For One to Many, the name of the field on the child entity that contains
     *                       the parent id value. For Many to Many, the name of the field on the 
     *                       Many to Many entity that contains the parent id value. 
     * @param childIdField   For Many to Many, the name of the field on the Many to Many entity
     *                       that contains the child id value. Not required for One to Many. 
     * @param manyToManyClass The class of the Many to Many entity. 
     * @param cascadeDelete Flag that indicates whether the child or Many to Many association should
     *                      be cascade deleted when the parent is deleted (or child) is deleted. 
     * @param readOnly Used for Many to Many and One to Many to indicate the child entity should 
     *                 not be included in the save of the parent entity. 
     */
    public ChildEntityInfo(String fieldName, Class<?> fieldType, ParameterizedType parameterTypes,
            String parentIdField, String childIdField, Class<? extends AbstractEntity> manyToManyClass,
            Boolean cascadeDelete, Boolean readOnly) {
        setFieldName(fieldName);
        setFieldType(fieldType);
        this.parameterTypes = parameterTypes;
        this.insertBeforeParent = false;
        this.cascadeDelete = cascadeDelete;
        this.readOnly = readOnly;
        if (!parentIdField.isEmpty()) {
            this.parentIdField = parentIdField;
        }
        if (!childIdField.isEmpty()) {
            this.childIdField = childIdField;
        }
        if (!manyToManyClass.equals(AbstractEntity.class)) {
            this.manyToManyClass = manyToManyClass;
        }
    }

    public Boolean isInsertBeforeParent() {
        return insertBeforeParent;
    }

    public void setInsertBeforeParent(Boolean insertBeforeParent) {
        this.insertBeforeParent = insertBeforeParent;
    }

    public Boolean isCascadeDelete() {
        return cascadeDelete;
    }

    public void setCascadeDelete(Boolean cascadeDelete) {
        this.cascadeDelete = cascadeDelete;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public ParameterizedType getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(ParameterizedType parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getParentIdField() {
        return parentIdField;
    }

    public void setParentIdField(String parentIdField) {
        this.parentIdField = parentIdField;
    }

    public String getChildIdField() {
        return childIdField;
    }

    public void setChildIdField(String childIdField) {
        this.childIdField = childIdField;
    }

    public Class<? extends AbstractEntity> getManyToManyClass() {
        return manyToManyClass;
    }

    public void setManyToManyClass(Class<? extends AbstractEntity> manyToManyClass) {
        this.manyToManyClass = manyToManyClass;
    }

    /**
     * @return The EJB Local Interface used to load or save the child entity or entity list. 
     */
    public Class<? extends AbstractEJBLocal> getEJBLocalClass() {
        return EJBLocalClass;
    }

    public void setEJBLocalClass(Class<? extends AbstractEJBLocal> EJBLocalClass) {
        this.EJBLocalClass = EJBLocalClass;
    }

    /**
     * @return The name of the load method on the EJB Local Interface used to retrieve the child
     * entity or entity list. If the method returns a single entity, it should take a single string 
     * parameter (i.e. id of the entity). If the load method returns a list of entities, it should
     * take a List of Strings as its parameter (i.e a list of the child ids to load). 
     */
    public String getLoadMethod() {
        return loadMethod;
    }

    public void setLoadMethod(String loadMethod) {
        this.loadMethod = loadMethod;
    }

    /**
     * @return The name of the save method on the EJB Local Interface used to save the child entity.  
     *The method should accept the entity to save as its parameter and return the updated entity.  
     */
    public String getSaveMethod() {
        return saveMethod;
    }

    public void setSaveMethod(String saveMethod) {
        this.saveMethod = saveMethod;
    }

    /**
     * @return True if the fieldType is Iterable (i.e a list type)
     */
    public Boolean isListField() {
        Boolean result = false;
        if (getFieldType() != null) {
            result = Iterable.class.isAssignableFrom(getFieldType());
        }
        return result;
    }

    /**
     *  @return True if the manyToManyClass is set. 
     */
    public Boolean isManyToMany() {
        return manyToManyClass != null;
    }

    /**
     *  @return True if the EJBLocalClass is set. 
     */
    public Boolean isExternalEntity() {
        return EJBLocalClass != null;
    }

    /**
     * @return The class if the entity represented by the field. If the field is a list type, the
     * generic type of the list is obtained and returned as the entity class. 
     */
    public Class<?> getEntityClass() {
        Class<?> entityClass = null;
        if (isListField()) {
            if (getParameterTypes() != null) {
                entityClass = (Class<?>) getParameterTypes().getActualTypeArguments()[0];
            }
        } else if (getFieldType() != null) {
            entityClass = getFieldType();
        }
        return entityClass;
    }
}
