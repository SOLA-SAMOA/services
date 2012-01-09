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
import javax.persistence.Id;

import javax.persistence.MappedSuperclass;
import org.sola.services.common.EntityAction;

/**
 *
 * @author soladev
 */
@MappedSuperclass
public abstract class AbstractIdEntity extends AbstractVersionedEntity {

    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private String id; // = java.util.UUID.randomUUID().toString();

    public String getId() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public EntitySaveSummary prepForSave(EntitySaveSummary saveSummary) {
        if (isNew()) {
            // Capture entity for refresh post merge
            getId(); // Set the Id
            setEntityAction(EntityAction.INSERT); 
        }

        saveSummary = super.prepForSave(saveSummary);
        if (getEntityAction() != null) {
            saveSummary.addActionedEntity(getEntityAction(), getId(), getClass());
        }

        return saveSummary;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += getId().hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || !(object.getClass() == this.getClass())) {
            return false;
        }
        AbstractIdEntity other = (AbstractIdEntity) object;
        if (!this.getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + (this.isNew() ? "[New Id=" + getId() + "]" : "[id=" + getId() + "]");
    }
}
