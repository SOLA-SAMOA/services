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
package org.sola.services.ejb.application.repository.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.ChildEntity;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.ExternalEJB;
import org.sola.services.common.repository.RepositoryUtility;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;
import org.sola.services.ejb.source.businesslogic.SourceEJBLocal;
import org.sola.services.ejb.source.repository.entities.Source;
import org.sola.services.ejb.source.repository.entities.SourceStatusChanger;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;

/**
 *
 * @author soladev
 */
@Table(name = "application", schema = "application")
public class Application extends AbstractVersionedEntity {

    public static final String ACTION_LODGED = "lodged";
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "nr")
    private String nr;
    @Column(name = "lodging_datetime")
    private Date lodgingDatetime;
    @Column(name = "expected_completion_date")
    private Date expectedCompletionDate;
    @Column(name = "assigned_datetime", insertable = false, updatable = false)
    private Date assignedDatetime;
    @Column(name = "assignee_id", insertable = false, updatable = false)
    private String assigneeId;
    @Column(name = "action_code", insertable = false, updatable = false)
    private String actionCode;
    @Column(name = "action_notes")
    private String actionNotes;
    @Column(name = "contact_person_id")
    private String contactPersonId;
    @Column(name = "agent_id")
    private String agentId;
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode = null;
    @AccessFunctions(onSelect = "st_asewkb(location)",
    onChange = "get_geometry_with_srid(#{location})")
    @Column(name = "location")
    private byte[] location;
    @Column(name = "new_lots")
    private int newLots;
    @Column(name = "services_fee")
    private BigDecimal servicesFee;
    @Column(name = "tax")
    private BigDecimal tax;
    @Column(name = "total_fee")
    private BigDecimal totalFee;
    @Column(name = "total_amount_paid")
    private BigDecimal totalAmountPaid;
    @Column(name = "fee_paid")
    private boolean feePaid;
    @Column(name = "receipt_reference")
    private String receiptRef;
    @ExternalEJB(ejbLocalClass = PartyEJBLocal.class,
    loadMethod = "getParty", saveMethod = "saveParty")
    @ChildEntity(childIdField = "contactPersonId")
    private Party contactPerson;
    @ExternalEJB(ejbLocalClass = PartyEJBLocal.class, loadMethod = "getParty")
    @ChildEntity(childIdField = "agentId")
    private Party agent;
    @ChildEntityList(parentIdField = "applicationId")
    private List<Service> serviceList;
    @ChildEntityList(parentIdField = "applicationId")
    private List<ApplicationProperty> propertyList;
    @ExternalEJB(ejbLocalClass = SourceEJBLocal.class,
    loadMethod = "getSources", saveMethod = "saveSource")
    @ChildEntityList(parentIdField = "applicationId", childIdField = "sourceId",
    manyToManyClass = ApplicationUsesSource.class)
    private List<Source> sourceList;

    public Application() {
        super();
    }

    private String generateApplicationNumber() {
        String result = "";
        SystemEJBLocal systemEJB = RepositoryUtility.tryGetEJB(SystemEJBLocal.class);
        if (systemEJB != null) {
            // Samoa Customization - send through the request type code and the ba unit id. The
            // Ba Unit id is required to determine the correct application number for unit plans. 
            String requestTypeCode = "unknown";
            String baUnitId = "notspecified"; 
            if (getServiceList() != null && getServiceList().size() > 0) {
                requestTypeCode = getServiceList().get(0).getRequestTypeCode();
            }
            if (getPropertyList() != null && getPropertyList().size() > 0) {
                for (ApplicationProperty property : getPropertyList()) {
                    if (property.getBaUnitId() != null) {
                        baUnitId = property.getBaUnitId(); 
                    }
                }
            }
            HashMap<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("requestTypeCode", requestTypeCode);
            params.put("baUnitId", baUnitId);
            Result newNumberResult = systemEJB.checkRuleGetResultSingle("generate-application-nr", params);
            if (newNumberResult != null && newNumberResult.getValue() != null) {
                result = newNumberResult.getValue().toString();
            }
        }
        return result;
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getActionNotes() {
        return actionNotes;
    }

    public void setActionNotes(String actionNotes) {
        this.actionNotes = actionNotes;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Date getAssignedDatetime() {
        return assignedDatetime;
    }

    public void setAssignedDatetime(Date assignedDatetime) {
        this.assignedDatetime = assignedDatetime;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
    }

    public boolean isFeePaid() {
        return feePaid;
    }

    public void setFeePaid(boolean feePaid) {
        this.feePaid = feePaid;
    }

    public byte[] getLocation() {
        return location;
    }

    public void setLocation(byte[] location) { //NOSONAR
        this.location = location; //NOSONAR
    }

    public Date getLodgingDatetime() {
        return lodgingDatetime;
    }

    public void setLodgingDatetime(Date lodgingDatetime) {
        this.lodgingDatetime = lodgingDatetime;
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public int getNewLots() {
        return newLots;
    }

    public void setNewLots(int newLots) {
        this.newLots = newLots;
    }

    public BigDecimal getServicesFee() {
        return servicesFee;
    }

    public void setServicesFee(BigDecimal servicesFee) {
        this.servicesFee = servicesFee;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public String getReceiptRef() {
        return receiptRef;
    }

    public void setReceiptRef(String receiptRef) {
        this.receiptRef = receiptRef;
    }

    public Party getAgent() {
        return agent;
    }

    public void setAgent(Party agent) {
        this.agent = agent;
        if (agent != null) {
            this.setAgentId(agent.getId());
        }
    }

    public Party getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Party contactPerson) {
        this.contactPerson = contactPerson;
        if (contactPerson != null) {
            this.setContactPersonId(contactPerson.getId());
        }
    }

    public List<ApplicationProperty> getPropertyList() {
        propertyList = propertyList == null ? new ArrayList<ApplicationProperty>() : propertyList;
        return propertyList;
    }

    public void setPropertyList(List<ApplicationProperty> propertyList) {
        this.propertyList = propertyList;
    }

    public List<Service> getServiceList() {
        serviceList = serviceList == null ? new ArrayList<Service>() : serviceList;
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public List<Source> getSourceList() {
        sourceList = sourceList == null ? new ArrayList<Source>() : sourceList;
        return sourceList;
    }

    public void setSourceList(List<Source> sourceList) {
        this.sourceList = sourceList;
    }

    @Override
    public void preSave() {

        if (isNew() && getNr() == null) {
            setNr(generateApplicationNumber());
        }

        // Set the reference id on the source so that it is possible to 
        // generate a number for the source that uses the Application number
        if (getSourceList() != null && getSourceList().size() > 0) {
            for (Source source : getSourceList()) {
                source.setLaNrReferenceId(getId());
            }
        }

        super.preSave();
    }
}
