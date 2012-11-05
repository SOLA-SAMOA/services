/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.administrative.repository.entities;

import javax.persistence.Column;
import javax.persistence.Table;
import org.sola.services.common.repository.DefaultSorter;
import org.sola.services.common.repository.entities.AbstractCodeEntity;

/**
 * Entity representing the administrative.rrr_type code table.
 *
 * @author soladev
 */
@Table(name = "rrr_type", schema = "administrative")
@DefaultSorter(sortString = "display_value")
public class RrrType extends AbstractCodeEntity {

    public static final String UNIT_ENTITLEMENT_TYPE = "unitEntitlement";
    public static final String BODY_CORPORATE_RULES_TYPE = "bodyCorpRules";
    public static final String ADDRESS_FOR_SERVICE_TYPE = "addressForService";
    @Column(name = "rrr_group_type_code")
    private String rrrGroupTypeCode;
    @Column(name = "is_primary")
    private boolean primary;
    @Column(name = "share_check")
    private boolean shareCheck;
    @Column(name = "party_required")
    private boolean partyRequired;

    public RrrType() {
        super();
    }

    public boolean isPartyRequired() {
        return partyRequired;
    }

    public void setPartyRequired(boolean partyRequired) {
        this.partyRequired = partyRequired;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isShareCheck() {
        return shareCheck;
    }

    public void setShareCheck(boolean shareCheck) {
        this.shareCheck = shareCheck;
    }

    public String getRrrGroupTypeCode() {
        return rrrGroupTypeCode;
    }

    public void setRrrGroupTypeCode(String rrrGroupTypeCode) {
        this.rrrGroupTypeCode = rrrGroupTypeCode;
    }
}
