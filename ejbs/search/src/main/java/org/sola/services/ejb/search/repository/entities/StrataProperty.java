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
package org.sola.services.ejb.search.repository.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Id;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 * Entity used to retrieve summary details about strata properties. 
 */
public class StrataProperty extends AbstractReadOnlyEntity {

    @Id
    @Column(name = "ba_id")
    private String id;
    @Column(name = "ba_type_code")
    private String typeCode;
    @Column(name = "ba_name")
    private String name;
    @Column(name = "ba_name_firstpart")
    private String nameFirstpart;
    @Column(name = "ba_name_lastpart")
    private String nameLastpart;
    @Column(name = "ba_status_code")
    private String statusCode;
    @Column(name = "ba_transaction_id")
    private String transactionId;
    @Column(name = "ba_registration_date")
    private Date registrationDate;
    @Column(name = "official_area")
    private Integer officialArea;
    @Column(name = "unit_entitlement")
    private Integer unitEntitlement;
    @Column(name = "unit_parcel_type")
    private String unitParcelTypeCode;
    // Ticket #68 - Cancel Unit Development
    @Column(name = "pending_action_code")
    private String pendingActionCode;

    public StrataProperty() {
        super(); 
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        this.nameFirstpart = nameFirstpart;
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        this.nameLastpart = nameLastpart;
    }

    public Integer getOfficialArea() {
        return officialArea;
    }

    public void setOfficialArea(Integer officialArea) {
        this.officialArea = officialArea;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getUnitEntitlement() {
        return unitEntitlement;
    }

    public void setUnitEntitlement(Integer unitEntitlement) {
        this.unitEntitlement = unitEntitlement;
    }

    public String getUnitParcelTypeCode() {
        return unitParcelTypeCode;
    }

    public void setUnitParcelTypeCode(String unitParcelTypeCode) {
        this.unitParcelTypeCode = unitParcelTypeCode;
    }

    public String getPendingActionCode() {
        return pendingActionCode;
    }

    public void setPendingActionCode(String pendingActionCode) {
        this.pendingActionCode = pendingActionCode;
    }
}
