/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.repository.RepositoryUtility;

/**
 * The base class for all SOLA entities. This entity should be used to capture simple read only
 * query results. If the entity must update the database, then extend {@linkplain AbstractEntity}
 * or one of its descendents. 
 * @author soladev
 */
public abstract class AbstractReadOnlyEntity implements Serializable {

    private boolean loaded = false;
    private boolean getValueException = false;

    /**
     * @return Generates and returns a new UUID value. 
     */
    protected String generateId() {
        return java.util.UUID.randomUUID().toString();
    }

    /**
     * @return true if the entity was loaded from the database. 
     */
    public Boolean isLoaded() {
        return loaded;
    }

    /**
     * Sets a flag indicating the entity was loaded from the database.
     * @param loaded
     */
    public void setLoaded(Boolean loaded) {
        this.loaded = loaded;
    }

    /**
     * Flags if the entity has been saved to the database or not. 
     * @return true if the entity was not loaded from the database (i.e. !isLoaded()). 
     */
    public Boolean isNew() {
        return !isLoaded();
    }

    /**
     * Obtains the value from the field indicated by the entityInfo parameter using reflection. 
     * @param entityInfo Details of the field / column to get the value from. 
     * @return The value of the field or an exception if the field indicated by the entityInfo
     * does not exist. 
     */
    public Object getEntityFieldValue(AbstractEntityInfo entityInfo) {
        Object result = null;
        try {
            Method getter = this.getClass().getMethod(entityInfo.getterName());
            result = getter.invoke(this);
        } catch (Exception ex) {
            if (!getValueException) {
                // this.toString calls getEntityFieldValue. getValueException is used to avoid
                // a stack overflow if the exception is for an Id field. 
                getValueException = true;
                throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                        new Object[]{"Unable to get value from " + entityInfo.getterName()
                            + " for entity " + this.toString(), ex});
            }
        }
        return result;
    }

    /**
     * Sets the value of a field to the specified object using reflection. If the field indicated
     * by the entityInfo does not exist or the type of the value does not match the field type an
     * exception is raised. 
     * <p> Note that Mybatis treats char fields as strings. To avoid an unnecessary type mismatch
     * exception when setting a Character field, this method converts the string value to a Character. 
     * Type conversion from Integer to Short is also performed when necessary. 
     * </p>
     * @param entityInfo Details of the field / column to set the value to.
     * @param value The object to set the field to. 
     */
    public void setEntityFieldValue(AbstractEntityInfo entityInfo, Object value) {
        try {

            Class<?> fieldType = entityInfo.getFieldType();

            // Mybatis serves up Character and char fields as String, so check if the target field
            // is char and if the object is String, convert it to a char value. 
            if (value != null && String.class.isAssignableFrom(value.getClass())
                    && (Character.class.isAssignableFrom(fieldType)
                    || char.class.isAssignableFrom(fieldType))) {
                value = new Character(value.toString().charAt(0));
            }

            // Mybatis also has problems with Short serving these up as Integer
            if (value != null && Integer.class.isAssignableFrom(value.getClass())
                    && (Short.class.isAssignableFrom(fieldType)
                    || short.class.isAssignableFrom(fieldType))) {
                value = new Short(value.toString());
            }

            Method setter = this.getClass().getMethod(entityInfo.setterName(), fieldType);
            setter.invoke(this, value);
        } catch (Exception ex) {
            String valueType = "<null>";
            if (value != null) {
                valueType = value.getClass().getSimpleName();
            }

            throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                    new Object[]{"Unable to set value to " + entityInfo.setterName()
                        + " for entity " + this.toString(), "Value Type:" + valueType, ex});
        }

    }

    /**
     * @return Returns the value for the unique identifier for the entity (as indicated by the @Id
     * annotation) or null if the entity has 0 or more than 1 fields marked as @Id. 
     */
    public String getEntityId() {
        String result = null;
        if (getIdColumns().size() == 1) {
            result = getEntityFieldValue(getIdColumns().get(0)).toString();
        }
        return result;
    }

    /** 
     * @return  The list of fields on the entity marked with the JPA 
     * {@linkplain javax.persistence.Column} annotation. 
     */
    public List<ColumnInfo> getColumns() {
        return RepositoryUtility.getColumns(this.getClass());
    }

    /** 
     * @return  The list of fields on the entity marked with the JPA
     * {@linkplain javax.persistence.Id} annotation. 
     */
    public List<ColumnInfo> getIdColumns() {
        return RepositoryUtility.getIdColumns(this.getClass(), getColumns());
    }

    /**
     * Determines if the field indicated is an id column or not.  
     * @param fieldName The name of the field to check. 
     * @return true if the field is an id field on the entity. 
     */
    public Boolean isIdColumn(String fieldName) {
        return RepositoryUtility.isIdColumn(this.getClass(), fieldName);
    }

    /** 
     * @return  The list of fields on the entity marked with the 
     * {@linkplain org.sola.services.common.repository.ChildEntity} and/or 
     * {@linkplain org.sola.services.common.repository.ChildEntityList} annotation. 
     */
    public List<ChildEntityInfo> getChildEntityInfo() {
        return RepositoryUtility.getChildEntityInfo(this.getClass());
    }

    /**
     * Retrieves the ColumnInfo object for a specific column of the entity. 
     * @param fieldName The name of the field to obtain the column info for. 
     */
    public ColumnInfo getColumnInfo(String fieldName) {
        return RepositoryUtility.getColumnInfo(this.getClass(), fieldName);
    }

    /**
     * Retrieves the ChildEntityInfo object for a specific column of the entity. 
     * @param fieldName The name of the field to obtain the child entity info for. 
     */
    public ChildEntityInfo getChildEntityInfo(String fieldName) {
        return RepositoryUtility.getChildEntityInfo(this.getClass(), fieldName);
    }

    /**
     * @return The name of the database table (prefixed with the schema if specified) this entity
     * represents. This information is obtained from the JPA {@linkplain javax.persistence.Table}
     * annotation. 
     */
    public String getTableName() {
        return RepositoryUtility.getTableName(this.getClass());
    }

    /**
     * Allows the SQL parameters used to retrieve child entities to be set, overriding the default
     * join criteria used by {@linkplain CommonRepository}. This override is available for 
     * One to One, One to Many and Many to Many joins. 
     * <p> Override this method in the parent entity and populate the parameter map with the 
     * appropriate SQL parts (e.g. {@linkplain CommonSqlProvider#PARAM_WHERE_PART}) for the join. 
     * </p>
     * @param childInfo The ChildEntityInfo for the child being loaded. Can be used to help
     * differentiate different children of the parent class. 
     * @return The SQL Parameter Map to use for retrieving the child entities.
     */
    public Map<String, Object> getChildJoinSqlParams(ChildEntityInfo childInfo) {
        return null;
    }

    /**
     * Overridden to be consistent with the {@linkplain #equals}. 
     * @return 
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (ColumnInfo idColumnInfo : getIdColumns()) {
            Object idValue = getEntityFieldValue(idColumnInfo);
            hash += (idValue != null ? idValue.hashCode() : 0);
        }
        return hash;
    }

    /** 
     * Compares objects using the id columns of the entities. Note that the object must be the
     * same class as this entity. During TO to Entity translation, Dozer uses this method to 
     * match TO's to existing entities. As some TO's omit parent id values from association 
     * entities (e.g. see PartyRoleTO), the {@linkplain AbstractVersionedEntity#equals} overrides
     * this implementation to use the rowId value for comparison. 
     * @param object The Object to compare with this one. 
     * @return true if the values of the id columns on the entities match. 
     */
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object != null && object.getClass() == this.getClass()) {
            result = this.toString().equals(object.toString());
        }
        return result;
    }

    /**
     * Returns the class if the entity along with the values for any id columns marked on the
     * entity. 
     * @return 
     */
    @Override
    public String toString() {
        String result = this.getClass().getSimpleName();
        for (ColumnInfo idColumnInfo : getIdColumns()) {
            Object idValue = getEntityFieldValue(idColumnInfo);
            result += ", " + idColumnInfo.getFieldName() + "="
                    + (idValue == null ? "null" : idValue.toString());
        }
        return result;
    }
}
