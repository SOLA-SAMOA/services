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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.sola.services.common.LocalInfo;

/**
 * Extends AbstractEntity and provides functionality to maintain row version (used for
 * optimisitic locking) and change user. The change action is also maintained, although this field
 * is only identify entities that require deletion. i.e. by setting this field to 'd', an update
 * will be sent to the DB allowing the EntityListener to log the entity for deletion as a post
 * save action.  
 * 
 * Both row version and change action are updated by database trigger and must be retrieved from
 * the database after each update using the Generated annotation functionality. Attempting to 
 * adjust the row version or change action in a PostPersist or PostUpdate action simply causes a
 * new update to be issued to the database. 
 * @author soladev
 */
@MappedSuperclass
public abstract class AbstractVersionedEntity extends AbstractEntity {

    public static final String ROWID_QUERY = ".findByRowId";
        
    @Basic
    @Column(name = "rowversion", insertable = false)
    // @Generated(GenerationTime.ALWAYS)
    private int rowVersion;
    @Column(name = "change_user")
    private String changeUser;

    @Basic
    @Column(name = "rowidentifier", insertable = false, updatable=false)
    private String rowId = null;

    public AbstractVersionedEntity() {
        super();
    }

    public String getRowId() {
        if (rowId == null) {
            rowId = java.util.UUID.randomUUID().toString();
        }
        return rowId;
    }

    public void setRowId(String id) {
        this.rowId = id;
    }


    public int getRowVersion() {
        return rowVersion;
    }

    public void setRowVersion(int rowVersion) {
        this.rowVersion = rowVersion;
    }

    public String getChangeUser() {

        return changeUser;
    }

    public void setChangeUser(String changeUser) {
        this.changeUser = changeUser;
    }

    /**
     * Indicates if the Entity is new and has yet to be saved to the database. 
     * Note that the initial row version assigned to an entity on insert is 1. 
     * @return true if the entity has yet to be saved to the database. 
     */
    public boolean isNew() {
        return rowVersion < 1;
    }

    @Override
    public EntitySaveSummary prepForSave(EntitySaveSummary saveSummary) {
        saveSummary = super.prepForSave(saveSummary);

        if (isNew()) {
            // If there is a new entity, set the flag so the entity can be refreshed after the
            // insert to in case there are trigger populated default values.
            saveSummary.setEntityToInsert(true);
        }

        if (toDelete()) {
            // Set the changeUser field explicitly. This will cause an update of the 
            // entity prior to the delete and ensure the user that triggered the delete
            // is recorded (i.e. it is not possible to set the changeUser during a SQL
            // Delete operation). Note that an update will not be triggered if the
            // deleting user is the same as existing changeUser for the record, but this is
            // a desirable optimization. Note if any other data on the record is 
            // different to the value in the DB during the merge, an update will be 
            // triggered before the delete. 
            setChangeUser(LocalInfo.getUserName());
            saveSummary.setEntityToDelete(true);
        }
        return saveSummary;
    }
    
    public void setIds(){
        
    }
}
