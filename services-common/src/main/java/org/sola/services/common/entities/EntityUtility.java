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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.entities;

import java.util.Iterator;
import java.util.List;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.EntityAction;
import org.sola.services.common.LocalInfo;

/**
 *
 * @author soladev
 */
public class EntityUtility {

    /** 
     * Returns a reference to the EJB identified by the ejbClass. 
     * <p>
     * This method requires LocalInfo to be populated with the target EJB during the onInvoke
     * process (see org.sola.services.common.ejbs.AbstractEJB). If LocalInfo does not contain the 
     * EJB reference, verify the parent EJB for the process (the first EJB accessed) includes a 
     * private field reference to the target EJB. e.g. 
     * @EJB
     * private PartyEJBLocal partyEJB; 
     * </p>
     * @param <T>
     * @param ejbClass
     * @return 
     */
    public static <T> T getEJB(Class<T> ejbClass, AbstractEntity entity) {
        T ejb = LocalInfo.get(ejbClass.getSimpleName(), ejbClass);
        if (ejb == null) {
            throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                    // Capture the specific details so they are added to the log
                    new Object[]{"EJB:" + ejbClass.getSimpleName(), "Entity:" + entity.toString()});

        }
        return ejb;
    }

    /**
     * Determines whether a child entity from a different EJB context can be loaded. Used to 
     * provide lazy loading functionality for child entities from different EJB contexts.
     * <p>
     * canLoadChild will return false if:
     * 1. This entity is in the process of being saved
     * 2. The childId null
     * 3. The childEntity.getId() equals the childId (i.e. the child is already loaded)
     * </p>
     * <p>
     * If the entity is saving, do not load the child entity to avoid unnecessary save
     * processing on the child entity.
     * </p>
     * @param childEntity The child entity from this entity that should be checked for loading
     * @param childId The id field from this entity used as the foreign key to the child entity
     * @return True if the child entity should be loaded otherwise false. 
     */
    public static boolean canLoadChild(AbstractIdEntity childEntity,
            String childId, boolean isSaving) {
        boolean canLoad = !isSaving && childId != null;
        if (canLoad) {
            canLoad = childEntity == null || childEntity.getId() == null
                    || !childEntity.getId().equals(childId);
        }
        return canLoad;
    }

    /** 
     * Can load if not saving and join list has data, but the child list is 
     * empty
     * @param childEntityList
     * @param joinList
     * @param isSaving
     * @return 
     */
    public static boolean canLoadChildList(List childEntityList,
            List joinList, boolean isSaving) {

        boolean canLoad = !isSaving
                && (joinList != null && !joinList.isEmpty())
                && (childEntityList == null || childEntityList.isEmpty());
        return canLoad;
    }

    public static <T extends AbstractIdEntity> void removeListIdEntity(List<T> entityList, String entityId) {
        for (Iterator<T> it = entityList.iterator(); it.hasNext();) {
            AbstractIdEntity idEntity = it.next();
            if (idEntity.getId().equals(entityId)) {
                it.remove();
                break;
            }
        }
    }

    public static <T extends AbstractJoin2Entity> void removeJoinEntity(
            List<T> entityList, String entityRowId) {
        for (Iterator<T> it = entityList.iterator(); it.hasNext();) {
            AbstractJoin2Entity joinEntity = it.next();
            if (joinEntity.getRowId().equals(entityRowId)) {
                it.remove();
                break;
            }
        }
    }

    public static <T extends AbstractJoin2Entity> T findJoinEntity(
            List<T> joinEntityList, String joinColumn2Id) {
        T result = null;
        if (joinEntityList != null) {
            for (AbstractJoin2Entity joinEntity : joinEntityList) {
                if (joinEntity.getJoinKey().getJoinColumn2() != null
                        && joinEntity.getJoinKey().getJoinColumn2().equals(joinColumn2Id)) {
                    result = (T) joinEntity;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * It searches in a list of entities that inherit from AbstractVersionedEntity for a match based
     * in rowId (rowidentifier). In principle can be used for all kinds of Entities (Id or Join) 
     * because both of these kind of entities have rowId.
     * @Author 2011-09-16 Elton
     * @param <T>
     * @param entityList The list of Entities to search
     * @param rowId The rowId that will be used for matching
     * @return 
     */
    public static <T extends AbstractVersionedEntity> T 
            findVersionedEntity(List<T> entityList, String rowId) {
        T result = null;
        if (entityList != null) {
            for (AbstractVersionedEntity entity : entityList) {
                if (entity.getRowId().equals(rowId)) {
                    result = (T) entity;
                    break;
                }
            }
        }
        return result;
    }

    public static <T extends AbstractVersionedEntity> void removeAbstractVersionedEntity(
            List<T> entityList, String entityRowId) {
        for (Iterator<T> it = entityList.iterator(); it.hasNext();) {
            AbstractVersionedEntity entity = it.next();
            if (entity.getRowId().equals(entityRowId)) {
                it.remove();
                break;
            }
        }
    }

    public static <T extends AbstractIdEntity> T findIdEntity(List<T> idEntityList,
            String entityId) {
        T result = null;
        if (idEntityList != null) {
            for (AbstractIdEntity idEntity : idEntityList) {
                if (idEntity.getId().equals(entityId)) {
                    result = (T) idEntity;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Prepares a Join Entity for saving. This includes creating a new join entity if the entity
     * is new as well as setting the EntityAction to Delete if the entity is flagged for Delete or
     * Disassociate.
     * Overloaded version of prepareJoinEntity that creates an instance of the join entity
     * using the Join Entity Class. 
     * @param <T>
     * @param joinEntityClass
     * @param joinEntityList
     * @param entity
     * @param parentId
     * @param saveSummary
     * @return 
     */
    public static <T extends AbstractJoin2Entity> T prepareJoinEntity(Class<T> joinEntityClass,
            List<T> joinEntityList, AbstractIdEntity idEntity, String parentId,
            EntitySaveSummary saveSummary) {
        T newJoinEntity = null;
        try {
            newJoinEntity = joinEntityClass.newInstance();
        } catch (Exception ex) {
            throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                    new Object[]{"Unable to create joinEntity " + joinEntityClass.getName()}, ex);
        }
        return prepareJoinEntity(joinEntityList, newJoinEntity, idEntity, parentId, saveSummary);
    }

    /**
     * Prepares a Join Entity for saving. This includes creating a new join entity if the entity
     * is new as well as setting the EntityAction to Delete if the entity is flagged for Delete or
     * Disassociate. Allows a new JoinEntity to be passed into the method in case the Join Entity
     * has additional fields that need to be set prior to adding the join entity to the managed
     * collection. 
     * @param <T>
     * @param joinEntityList
     * @param newJoinEntity
     * @param idEntity
     * @param parentId
     * @param saveSummary
     * @return 
     */
    public static <T extends AbstractJoin2Entity> T prepareJoinEntity(
            List<T> joinEntityList, T newJoinEntity, AbstractIdEntity idEntity, String parentId,
            EntitySaveSummary saveSummary) {

        T joinEntity = EntityUtility.findJoinEntity(joinEntityList, idEntity.getId());
        if (idEntity.isNew() || joinEntity == null) {
            // Configure the new joinEntity 
            joinEntity = newJoinEntity;
            joinEntity.setJoinKey(parentId, idEntity.getId());
            joinEntity.prepForSave(saveSummary);
            joinEntityList.add(joinEntity);
        } else {
            if (idEntity.toRemove()) {
                // Mark the join for deletion
                joinEntity.setEntityAction(EntityAction.DELETE);
            }
            joinEntity.prepForSave(saveSummary);
        }
        return joinEntity;
    }
}
