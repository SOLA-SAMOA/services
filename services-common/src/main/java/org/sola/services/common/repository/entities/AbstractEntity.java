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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sola.common.DateUtility;
import org.sola.services.common.EntityAction;

/**
 * The base class used for all entities that can be used to insert, update or
 * delete rows from tables.
 *
 * @author soladev
 */
public abstract class AbstractEntity extends AbstractReadOnlyEntity {

    private boolean saving = false;
    private boolean removed = false;
    private boolean updateBeforeDelete = false;
    private boolean forceRefresh = false;
    protected EntityAction entityAction = null;
    private Map<String, Object> originalValues = new HashMap<String, Object>();

    /**
     * Snapshots the values assigned to the columns of the entity. Used during
     * {@linkplain #isModified()} to determine if any data modifications have
     * occurred to the entity or not.
     */
    private void snapshotValues() {
        originalValues.clear();
        for (ColumnInfo columnInfo : getColumns()) {
            originalValues.put(columnInfo.getFieldName(), getEntityFieldValue(columnInfo));
        }
    }

    /**
     * Overrides the default setLoaded functionality to take a snapshot of the
     * the values retrieved from the database to enable later comparison during
     * the {@linkplain #isModified()} check.
     *
     * @param loaded
     */
    @Override
    public void setLoaded(Boolean loaded) {
        super.setLoaded(loaded);
        if (loaded) {
            snapshotValues();
        }
    }

    /**
     * Checks the current values on the entity against the original values
     * retrieved from the database to determine if the entity has been modified.
     *
     * @return True if the entity is new or at least one of its fields has been
     * changed.
     */
    public Boolean isModified() {
        boolean result = false;
        if (originalValues.isEmpty()) {
            if (isNew() || !noAction()) {
                // No original values have been captured for the entity. If it is a new entity or
                // the EntityAction has been explicitly set, then flag the entity as modified. 
                // If the entity is not new and the EntityAction has not be set, then it is not
                // possible to safely determine if it has been modified, so return false.  
                result = true;
            }
        } else {
            for (ColumnInfo columnInfo : getColumns()) {
                Object newValue = getEntityFieldValue(columnInfo);
                Object originalValue = originalValues.get(columnInfo.getFieldName());
                if (Date.class.isAssignableFrom(columnInfo.getFieldType())) {
                    // Treat dates as a special case as the resolution of a date
                    // retrieved from the Db can be up to millisecond. 
                    result = !DateUtility.areEqual((Date) originalValue, (Date) newValue);
                } else if ((newValue != null && !newValue.equals(originalValue))
                        || (originalValue != null && !originalValue.equals(newValue))) {
                    result = true;
                }
                if (result) {
                    // DEBUG: Uncomment this section to find out the fields that are causing an entity
                    // to be updated. 
//                    System.err.println("Change detected on " + this.getTableName()
//                            + ", column=" + columnInfo.getFieldName()
//                            + ", old val=" + originalValue + ", new val="
//                            + newValue);
                    // One of the columns has been updated. Break out of the loop.
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Checks if the id values on the entity have changed since the entity was
     * retrieved from the database. Uses the snapshot data to make this
     * determination. If the entity is new, then the false is returned. Used by
     * the saveEnttity process to determine if it is safe/valid to update the
     * entity or not.
     *
     * @return True if the id of the entity has been changed.
     */
    public Boolean hasIdChanged() {
        boolean result = false;
        if (!originalValues.isEmpty()) {
            for (ColumnInfo columnInfo : getIdColumns()) {
                Object newValue = getEntityFieldValue(columnInfo);
                Object originalValue = originalValues.get(columnInfo.getFieldName());
                if ((newValue != null && !newValue.equals(originalValue))
                        || (originalValue != null && !originalValue.equals(newValue))) {
                    // One of the columns has been updated. Indicate the entity is modified. 
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Indicates if the entity must be updated prior to deletion. This ensures
     * the name of the user that caused the delete to occur is correctly
     * recorded in the history table.
     *
     * @see #setUpdateBeforeDelete
     * @see AbstractVersionedEntity#preSave()
     * @return true if the entity requires update prior to deletion.
     */
    public Boolean isUpdateBeforeDelete() {
        return updateBeforeDelete;
    }

    /**
     * Setter for UpdateBeforeDelete.
     *
     * @see #isUpdateBeforeDelete()
     * @see AbstractVersionedEntity#preSave()
     * @param updateBeforeDelete
     */
    public void setUpdateBeforeDelete(boolean updateBeforeDelete) {
        this.updateBeforeDelete = updateBeforeDelete;
    }

    /**
     * Flag to indicate whether the entity has been disassociated from its
     * parent or deleted. This value is set during the
     * {@linkplain org.sola.services.common.repository.CommonRepositoryImpl#saveEntity}
     * process and is available after the save has completed. Note that
     * {@linkplain #entityAction} is cleared by the save process preventing use
     * of {@linkplain #toDelete()} to check if the entity was deleted or
     * disassociated.
     *
     * @see #toDelete()
     * @see org.sola.services.common.repository.CommonRepositoryImpl#saveEntity
     */
    public Boolean isRemoved() {
        return removed;
    }

    /**
     * Setter for the removed flag. Set during the
     * {@linkplain org.sola.services.common.repository.CommonRepositoryImpl#saveEntity}
     * process.
     *
     * @param removed
     * @see AbstractCodeEntity#isRemoved()
     */
    public void setRemoved(Boolean removed) {
        this.removed = removed;
    }

    /**
     * Flag to indicate if the entity is currently in the process of being
     * saved. This can be used to restrict actions (such as loading of child
     * entities) during the save process.
     *
     * @see org.sola.services.common.repository.CommonRepositoryImpl#saveEntity
     * @return
     */
    public Boolean isSaving() {
        return saving;
    }

    /**
     * Setter for the saving flag.
     *
     * @see #isSaving()
     * @param saving
     */
    public void setSaving(Boolean saving) {
        this.saving = saving;
    }

    /**
     * Flag to indicate if the entity should be refreshed at the completion of
     * the save process. This flag is set automatically by
     * {@linkplain org.sola.services.common.repository.CommonSqlProvider#buildInsertSql}
     * where there could be fields populated with default values.
     */
    public boolean isForceRefresh() {
        return forceRefresh;
    }

    /**
     * Setter for the forceRefresh flag.
     *
     * @see AbstractCodeEntity#isForceRefresh()
     * @param forceRefresh
     */
    public void setForceRefresh(boolean forceRefresh) {
        this.forceRefresh = forceRefresh;
    }

    /**
     * Retrieves the entity action for the entity. The entity action is used to
     * determine whether the entity should be inserted, updated, deleted or
     * disassociated from its parent entity.
     */
    public EntityAction getEntityAction() {
        return entityAction;
    }

    /**
     * Sets the entity action. Note that this method may not change the entity
     * action if it is already set with an action considered to have higher
     * priority. The EntityAction priority order is DELETE, DISASSOCIATE,
     * INSERT, UPDATE.
     *
     * @param entityAction
     */
    public void setEntityAction(EntityAction entityAction) {
        if (entityAction != null) {
            if (entityAction == EntityAction.DELETE) {
                markForDelete();
            } else if (entityAction == EntityAction.DISASSOCIATE) {
                markForDisassociate();
            } else {
                markForSave();
            }
        }
    }

    /**
     * Checks if the entity is marked for deletion.
     *
     * @return true if the EntityAction is set to DELETE.
     */
    public Boolean toDelete() {
        return getEntityAction() == EntityAction.DELETE;
    }

    /**
     * Checks if the entity should be disassociated from its parent entity.
     *
     * @return True if the EntityAction is set to DISASSOCIATE.
     */
    public Boolean toDisassociate() {
        return getEntityAction() == EntityAction.DISASSOCIATE;
    }

    /**
     * Checks if the entity is being removed from its parent entity.
     *
     * @return True if EntityAction is set to DELETE or DISASSOCIATE
     */
    public Boolean toRemove() {
        return toDelete() || toDisassociate();
    }

    /**
     * Indicates if the entity is to be inserted.
     *
     * @return True if the EntityAction is set to INSERT
     */
    public Boolean toInsert() {
        return getEntityAction() == EntityAction.INSERT;
    }

    /**
     * Indicates if the entity is to be updated.
     *
     * @return True if the EntityAction is set to UPDATE
     */
    public Boolean toUpdate() {
        return getEntityAction() == EntityAction.UPDATE;
    }

    /**
     * Indicates if there is no entity action set for the entity.
     *
     * @return True if the EntityAction is set to null.
     */
    public Boolean noAction() {
        return getEntityAction() == null;
    }

    /**
     * Resets the entity action to null.
     */
    public void resetEntityAction() {
        this.entityAction = null;
    }

    /**
     * Flags the entity for save. If the entity is not already flagged for
     * removal ({@linkplain #toRemove()}, it will set the EntityAction to INSERT
     * if the entity {@linkplain #isNew} otherwise it will set the action is set
     * to UPDATE.
     */
    public void markForSave() {
        if (!toInsert() && !toRemove()) {
            if (isNew()) {
                this.entityAction = EntityAction.INSERT;
            } else {
                this.entityAction = EntityAction.UPDATE;
            }
        }
    }

    /**
     * Sets the entity action to DELETE.
     */
    public void markForDelete() {
        this.entityAction = EntityAction.DELETE;
    }

    /**
     * Sets the entity action to DISASSOCIATE if it is not already set to
     * DELETE.
     */
    public void markForDisassociate() {
        if (!toDelete()) {
            this.entityAction = EntityAction.DISASSOCIATE;
        }
    }

    /**
     * Method executed by
     * {@linkplain org.sola.services.common.repository.CommonRepositoryImpl#saveEntity}
     * prior to saving the entity. Can be overridden in descendent classes to
     * provide additional save processing. Ensure super.preSave() is called if
     * this method is overridden.
     */
    public void preSave() {
    }

    /**
     * Method executed by
     * {@linkplain org.sola.services.common.repository.CommonRepositoryImpl#saveEntity}
     * after saving the entity. Can be overridden in descendent classes to
     * provide additional save processing. Ensure super.preSave() is called if
     * this method is overridden.
     */
    public void postSave() {
    }

    /**
     * Plugs into the entity save process to optionally allow additional
     * processing of many to many entities created during the save process. This
     * could be to set additional fields on the many to many using non id values
     * from the parent or the child.
     * <p>
     * This method is triggered on the parent entity during
     * {@linkplain org.sola.services.common.repository.CommonRepositoryImpl#createManyToManyEntity}.
     *
     * @param manyToMany The many to many entity created to link the parent to
     * the child.
     * @param child The child entity being linked to the parent
     * @return The updated many to many entity.
     */
    public AbstractEntity initializeManyToMany(AbstractEntity manyToMany, AbstractEntity child) {
        return manyToMany;
    }

    /**
     * Configures an entity so that it can be cloned when inserted into the
     * database. Resets the entity id values to null. Also steps through all
     * child entities to set them cloneable as well.
     *
     * Note that it may be necessary to extend this method in descendent classes
     * so that specific entity fields (like statusCode and transactionId) can be
     * reset to the appropriate value (i.e. null)
     */
    public void makeCloneable() {
        // Make the entity appear to be a new entity
        originalValues.clear();
        this.entityAction = EntityAction.INSERT;
        this.setLoaded(false);

        // Reset the Id values to null
        for (ColumnInfo columnInfo : this.getIdColumns()) {
            this.setEntityFieldValue(columnInfo, null);
        }

        // Apply the same procedure to all child entities. 
        for (ChildEntityInfo childInfo : this.getChildEntityInfo()) {
            if (AbstractEntity.class.isAssignableFrom(childInfo.getEntityClass())) {
                if (childInfo.isListField()) {
                    List<AbstractEntity> childList = (List<AbstractEntity>) this.getEntityFieldValue(childInfo);
                    if (childList != null && childList.size() > 0) {
                        for (AbstractEntity child : childList) {
                            if (!child.isNew()) {
                                child.makeCloneable();
                            }
                        }
                    }
                } else {
                    AbstractEntity child = (AbstractEntity) this.getEntityFieldValue(childInfo);
                    if (child != null && !child.isNew()) {
                        child.makeCloneable();
                    }

                }
            }
        }
    }
}
