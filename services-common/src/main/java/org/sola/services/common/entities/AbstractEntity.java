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
package org.sola.services.common.entities;

import java.io.Serializable;
import javax.persistence.Transient;
import org.sola.services.common.EntityAction;

/**
 * Base JPA entity class. EntityAction can be used to 
 * @author soladev
 */
public class AbstractEntity implements Serializable {

    @Transient
    private boolean saving = false;
    @Transient
    protected EntityAction entityAction = null;

    protected boolean isSaving() {
        return saving;
    }

    public void setSaving(boolean saving) {
        this.saving = saving;
    }

    public EntityAction getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityAction entityAction) {
        this.entityAction = entityAction;
    }

    /** 
     * Checks if the entity is marked for deletion.
     * @return true if the EntityAction is set to DELETE.
     */
    public boolean toDelete() {
        return getEntityAction() == EntityAction.DELETE;
    }

    /** Checks if the entity should be disassociated from its 
     * parent entity.
     * @return True if the EntityAction is set to DISASSOCIATE. 
     */
    public boolean toDisassocaite() {
        return getEntityAction() == EntityAction.DISASSOCIATE;
    }

    public boolean toRemove() {
        return toDelete() || toDisassocaite();
    }

    /**
     * This method is triggered at the beginning of the saveEntity process and can be overridden
     * in descendent classes to prepare the entity for save. Note that a super call to prepForSave
     * is required. Descendent classes that have child lists should also trigger this method on 
     * their children to ensure correct behavior of the delete and disassociate actions. 
     * @param saveSummary
     * @return 
     */
    public EntitySaveSummary prepForSave(EntitySaveSummary saveSummary) {
        setSaving(true);
        return saveSummary;
    }

    /**
     * This method is triggered after Merge has been executed in the saveEntity process. This can
     * be used to remove any join entities prior to the flush operation. 
     * @param saveSummary 
     */
    public void afterMerge(EntitySaveSummary saveSummary) {
    }

    /**
     * This method is triggered after the Flush has been executed in the saveEntity process. This 
     * can be used to trigger save on external entities marked for removal. 
     * @param saveSummary
     * @param originalEntity 
     */
    public void afterFlush(EntitySaveSummary saveSummary, AbstractEntity originalEntity) {
    }

    /**
     * This method is triggered at the end of the saveEntity process. It is used to reset 
     * the saving flag on the entity as well as clear the entity action. Descendent classes that
     * have child lists should also trigger the afterSave method on any children that executed
     * the prepForSave method. 
     */
    public void afterSave() {
        setSaving(false);
        setEntityAction(null);
    }
}
