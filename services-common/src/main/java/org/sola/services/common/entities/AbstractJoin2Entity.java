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
package org.sola.services.common.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;

/**
 * Provides base functionality for versioned join entities. Note that the @Id for 
 * the Join Entity must be identified on the concrete class as an Embedded PK type. 
 * This class uses the rowidentifier field to allow unique identification of join 
 * entities.
 * @author soladev
 */
@MappedSuperclass
public abstract class AbstractJoin2Entity extends AbstractVersionedEntity {

//    public static final String ROWID_QUERY = ".findByRowId";
//    @Basic
//    @Column(name = "rowidentifier")
//    private String rowId = null;
//
//    public String getRowId() {
//        if (rowId == null) {
//            rowId = java.util.UUID.randomUUID().toString();
//        }
//        return rowId;
//    }
//
//    public void setRowId(String id) {
//        this.rowId = id;
//    }

    public abstract AbstractJoin2PK getJoinKey(); 

    public abstract void setJoinKey(String joinColumn1, String joinColumn2);

    @Override
    public EntitySaveSummary prepForSave(EntitySaveSummary saveSummary) {
        if (isNew()) {
            // Make sure the rowId is set for the new entity. 
            getRowId();
        }

        saveSummary = super.prepForSave(saveSummary);
        if (getEntityAction() != null) {
            saveSummary.addActionedEntity(getEntityAction(), getRowId(), getClass());
        }

        return saveSummary;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += getRowId().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object.getClass() == this.getClass())) {
            return false;
        }
        AbstractJoin2Entity other = (AbstractJoin2Entity) object;
        if (!this.getRowId().equals(other.getRowId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + (this.isNew() ? "[New Row Id=" + getRowId() + "]" : "[Row Id=" + getRowId() + "]");
    }
}
