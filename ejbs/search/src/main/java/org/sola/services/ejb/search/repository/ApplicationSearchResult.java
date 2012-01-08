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
 package org.sola.services.ejb.search.repository;

import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.sola.services.common.entities.AbstractResultEntity;

@Entity

@NamedNativeQueries({
    @NamedNativeQuery(name = "ApplicationSummary.getAssigned",
    query = ApplicationSearchResult.QUERY
    + "WHERE u.username IS NOT NULL AND a.status_code != 'completed' "
    + "ORDER BY a.lodging_datetime DESC "
    + "LIMIT 100",
    readOnly = true,
    resultClass = ApplicationSearchResult.class),

    @NamedNativeQuery(name = "ApplicationSummary.getUnassigned",
    query = ApplicationSearchResult.QUERY 
        + "WHERE u.username IS NULL AND a.status_code != 'completed' "
        + "ORDER BY a.lodging_datetime DESC "
        + "LIMIT 100",
    readOnly = true,
    resultClass = ApplicationSearchResult.class),
    
    @NamedNativeQuery(name = "ApplicationSummary.searchApplications",
    query = ApplicationSearchResult.QUERY 
        + "WHERE a.lodging_datetime BETWEEN ?2 AND ?3 "
        + "AND lower(a.nr) LIKE lower(?4) "
        + "AND lower(p2.name) LIKE lower(?5) "
        + "AND ("
        + "(lower (COALESCE(p.name, '') || ' ' || COALESCE(p.last_name, '')) LIKE lower(?6))"
        + "OR (lower (COALESCE(p.name, '')) LIKE lower(?6))"
        + "OR (lower (COALESCE(p.last_name, '')) LIKE lower(?6))"
        + ") "
        + "ORDER BY a.lodging_datetime DESC "
        + "LIMIT 100",
    readOnly = true,
    resultClass = ApplicationSearchResult.class)
})

public class ApplicationSearchResult extends AbstractResultEntity {
    
    protected final static String QUERY = "SELECT a.id, a.nr, a.lodging_datetime, a.expected_completion_date, "
            + "a.assigned_datetime, get_translation(ast.display_value,?1) AS status, "
            + "(COALESCE(u.first_name, '') || ' ' || COALESCE(u.last_name, '')) AS assignee_name, "
            + "a.assignee_id, (COALESCE(p.name, '') || ' ' || COALESCE(p.last_name, '')) AS contact_person, "
            + "p.id AS contact_person_id, "
            + "(COALESCE(p2.name, '') || ' ' || COALESCE(p2.last_name, '')) AS agent, "
            + "p2.id AS agent_id, "
            + "(SELECT string_agg(tmp.display_value, ',') FROM "
            + "  (SELECT get_translation(display_value,?1) as display_value FROM application.service aps INNER JOIN application.request_type rt "
            + "  ON aps.request_type_code = rt.code WHERE aps.application_id = a.id ORDER BY aps.service_order) tmp "
            + "  ) AS service_list, a.fee_paid "
            + "FROM (application.application a LEFT JOIN application.application_status_type ast on a.status_code = ast.code) "
            + "LEFT JOIN system.appuser u ON a.assignee_id = u.id "
            + "LEFT JOIN party.party p ON a.contact_person_id = p.id "
            + "LEFT JOIN party.party p2 ON a.agent_id = p2.id ";
    
    @Column(name = "nr")
    private String nr;
    @Column(name = "status")
    private String status;
    @Column(name = "lodging_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lodgingDatetime;
    @Column(name = "expected_completion_date")
    @Temporal(TemporalType.DATE)
    private Date expectedCompletionDate;
    @Column(name = "assigned_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignedDatetime;
    @Column(name = "assignee_name")
    private String assigneeName;
    @Column(name = "assignee_id")
    private String assigneeId;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "agent")
    private String agent;
    @Column(name = "agent_id")
    private String agentId;
    @Column(name = "contact_person_id")
    private String contactPersonId;
    @Column(name = "service_list")
    private String serviceList;
    @Column(name = "fee_paid")
    private Boolean feePaid;

    public Boolean getFeePaid() {
        return feePaid;
    }

    public void setFeePaid(Boolean feePaid) {
        this.feePaid = feePaid;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getContactPersonId() {
        return contactPersonId;
    }

    public void setContactPersonId(String contactPersonId) {
        this.contactPersonId = contactPersonId;
    }

    public String getAgent() {
        return agent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public Date getAssignedDatetime() {
        return assignedDatetime;
    }

    public void setAssignedDatetime(Date assignedDatetime) {
        this.assignedDatetime = assignedDatetime;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Date getExpectedCompletionDate() {
        return expectedCompletionDate;
    }

    public void setExpectedCompletionDate(Date expectedCompletionDate) {
        this.expectedCompletionDate = expectedCompletionDate;
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

    public String getServiceList() {
        return serviceList;
    }

    public void setServiceList(String serviceList) {
        this.serviceList = serviceList;
    }
}
