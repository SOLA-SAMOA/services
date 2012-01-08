/**
 * ******************************************************************************************
 * Copyright (C) 2011 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.services.common.entities;

import java.lang.Override;
import org.hibernate.event.PreInsertEvent;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreUpdateEvent;
import org.hibernate.event.PreUpdateEventListener;
import org.sola.services.common.LocalInfo;

/**
 * Entity Listener for Hibernate PreInsert and PreUpdate events that allows setting of the 
 * changeUser field and rowVersion on an entity just prior to it being updated. Note that 
 * setting rowVersion before the update means that it is not necessary to retrieve the rowVersion 
 * after the update which would require an extra select. 
 * <p>
 * To configure the listener so that it is used within the persistence context, add the following
 * properties to the persistence.xml. 
 * <property name="hibernate.ejb.event.pre-update" value="org.sola.services.common.entities.EntityListener"/>
 * <property name="hibernate.ejb.event.pre-insert" value="org.sola.services.common.entities.EntityListener"/>
 * </p>
 * <p>
 * With JPA, it should be possible to use the @PrePersist and @PreUpdate callback events to perform
 * this update, however there appears to be a bug with Hibernate 3.5.0-Final (also tested 3.5.6-Final)
 * that prevents the @PreUpdate from succeeding. If the ChangeUser field is modified during 
 * @PreUpdate, Hibernate throws an OptimisticLockException.
 * </p>
 * <p>
 * Known Issues: This code will not work if an entity is marked with Hibernate dynamicUpdate/Insert
 * annotations e.g. @org.hibernate.annotations.Entity(dynamicUpdate = true, dynamicInsert = true).
 * It seems that updating the changeUser field in this Listener does not cause the field to be 
 * included in the dynamic update statement. 
 * </p>
 * <p>
 * For detailed description of this code refer to 
 * anshuiitk.blogspot.com/2010/11/hibernate-pre-database-operation-event.html. 
 * </p>
 * @author soladev
 */
public class EntityListener implements PreInsertEventListener, PreUpdateEventListener {

    /**
     * Hooks into the Hibernate PreInsert event to set the changeUser and rowVersion fields on 
     * insert. Refer to the hibernate documentation for further details. 
     * @param preInsertEvent
     * @return false
     */
    @Override
    public boolean onPreInsert(PreInsertEvent preInsertEvent) {
        if (preInsertEvent.getEntity() instanceof AbstractVersionedEntity) {
            AbstractVersionedEntity entity = (AbstractVersionedEntity) preInsertEvent.getEntity();
            String userName = LocalInfo.getUserName();
            String[] propertyNames = preInsertEvent.getPersister().getEntityMetamodel().getPropertyNames();
            Object[] state = preInsertEvent.getState();
            entity.setChangeUser(userName);
            setValue(state, propertyNames, "changeUser", userName);
            
            // RowVersion is not part of the insert, but it needs to be defaulted 
            // to the correct post insert value. 
            entity.setRowVersion(1);
            setValue(state, propertyNames, "rowVersion", 1);

        }
        return false; // If true, the object will be omitted from the insert. 
    }

    /**
     * Hooks into the Hibernate PreUpdate event to set the changeUser on update. Refer to hibernate
     * documentation for further details. 
     * @param preUpdateEvent
     * @return false
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent preUpdateEvent) {
        if (preUpdateEvent.getEntity() instanceof AbstractVersionedEntity) {
            AbstractVersionedEntity entity = (AbstractVersionedEntity) preUpdateEvent.getEntity();
            String userName = LocalInfo.getUserName();
            String[] propertyNames = preUpdateEvent.getPersister().getEntityMetamodel().getPropertyNames();
            Object[] state = preUpdateEvent.getState();
            entity.setChangeUser(userName);
            setValue(state, propertyNames, "changeUser", userName);
            
            // Increment the rowVersion to a new value. 
            int newRowVersion = entity.getRowVersion() + 1;
            entity.setRowVersion(newRowVersion);
            setValue(state, propertyNames, "rowVersion", newRowVersion);

        }
        return false; // If true, the object will be omitted from the update
    }

    /**
     * Updates the Hibernate State so the new value for changeUser is sent to the database. 
     * @param currentState Hibernate record state.
     * @param propertyNames Names of the properties to update.
     * @param propertyToSet The name of the property to set. 
     * @param value The value to set the named property to. 
     */
    private void setValue(Object[] currentState, String[] propertyNames,
            String propertyToSet, Object value) {
        int index = 0;
        for (String propertyName : propertyNames) {
            if (propertyName.equals(propertyToSet)) {
                currentState[index] = value;
                break;
            }
            index++;
        }
    }
}
