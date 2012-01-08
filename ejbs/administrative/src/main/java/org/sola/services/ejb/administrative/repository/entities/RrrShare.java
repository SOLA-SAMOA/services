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
package org.sola.services.ejb.administrative.repository.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.ExternalEJB;
import org.sola.services.common.repository.entities.AbstractEntity;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;

/**
 * Entity representing the administrative.rrr_share table. 
 * @author soladev
 */
@Table(name = "rrr_share", schema = "administrative")
public class RrrShare extends AbstractVersionedEntity {

    @Id
    @Column(name = "id")
    private String id;
    // rrr_id is also part of the primary key in the rrr_share table, however it does not
    // need to be marked as @Id as the id column contains a unique value for the share. 
    @Column(name = "rrr_id")
    private String rrrId;
    @Column(name = "nominator")
    private Short nominator;
    @Column(name = "denominator")
    private Short denominator;
    @ExternalEJB(ejbLocalClass = PartyEJBLocal.class, loadMethod = "getParties")
    @ChildEntityList(parentIdField = "shareId", childIdField = "partyId",
    manyToManyClass = PartyForRrr.class, readOnly = true)
    private List<Party> rightHolderList;

    public RrrShare() {
        super();
    }

    public Short getDenominator() {
        return denominator;
    }

    public void setDenominator(Short denominator) {
        this.denominator = denominator;
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Short getNominator() {
        return nominator;
    }

    public void setNominator(Short nominator) {
        this.nominator = nominator;
    }

    public String getRrrId() {
        return rrrId;
    }

    public void setRrrId(String rrrId) {
        this.rrrId = rrrId;
    }

    public List<Party> getRightHolderList() {
        return rightHolderList;
    }

    public void setRightHolderList(List<Party> rightHolderList) {
        this.rightHolderList = rightHolderList;
    }

    /**
     * Plugs into the entity save process to set the rrr id on the PartyForRrr many to many entity. 
     * Additional processing of PartyForRrr is required because it also links Rrr directly to party. 
     * @param manyToMany The PartyForRrr many to many entity created to link the RrrShare and Party
     * @param child The Party being linked to the RrrShare
     * @return The updated PartyForRrr many to many entity. 
     */
    @Override
    public AbstractEntity initializeManyToMany(AbstractEntity manyToMany, AbstractEntity child) {
        if (manyToMany instanceof PartyForRrr) {
            ((PartyForRrr) manyToMany).setRrrId(getRrrId());
        }
        return manyToMany;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (getRowId() != null ? getRowId().hashCode() : 0);
        return hash;
    }

    /**
     * Overrides the default {@linkplain AbstractVersionedEntity#equals} method to perform equals
     * with the RowId rather than the id columns. This is because Dozer uses the equals method
     * to match transfer objects (TO's) to entity objects during translation of collections and 
     * lists and the Id for RrrShare is not exposed through the RrrShareTO object. 
     * @param object The object to compare with this one
     * @return true if the RowIds match
     */
    @Override
    public boolean equals(Object object) {
        boolean result = false;
        if (object != null && object.getClass() == this.getClass()) {
            String objectRowId = ((AbstractVersionedEntity) object).getRowId();
            result = this.getRowId().equals(objectRowId);
        }
        return result;
    }
}
