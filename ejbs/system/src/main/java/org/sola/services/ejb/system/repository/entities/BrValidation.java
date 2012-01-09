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
package org.sola.services.ejb.system.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.entities.AbstractEntity;

/**
 *
 * @author soladev
 */
@Table(name = "br_validation", schema = "system")
public class BrValidation extends AbstractEntity {

    public static final String SEVERITY_CRITICAL = "critical";
    public static final String SEVERITY_WARNING = "warning";
    public static final String SEVERITY_MEDIUM = "medium";
    
    // Query Parameters
    public static final String QUERY_PARAMETER_TARGETCODE = "targetCode";
    public static final String QUERY_PARAMETER_MOMENTCODE = "momentCode";
    public static final String QUERY_PARAMETER_REQUESTTYPE = "requestType";
    public static final String QUERY_PARAMETER_RRRTYPE = "rrrType";
    
    // Order by column
    public static final String QUERY_ORDERBY_ORDEROFEXECUTION = "order_of_execution";
    
    // Where clauses
    public static final String QUERY_WHERE_FORAPPLICATION = "target_code='application' "
            + "AND target_application_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "}";
    public static final String QUERY_WHERE_FORSERVICE = "target_code='service' "
            + "AND target_service_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "} "
            + "AND (target_request_type_code=#{" + QUERY_PARAMETER_REQUESTTYPE + "} "
            + "OR target_request_type_code is null)";
    public static final String QUERY_WHERE_FORRRR = "target_code='rrr' "
            + "AND target_reg_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "} "
            + "AND (target_rrr_type_code=#{" + QUERY_PARAMETER_RRRTYPE + "} "
            + "OR target_rrr_type_code is null)";
//    public static final String QUERY_WHERE_FORBAUNIT = "target_code='ba_unit' "
//            + "AND target_reg_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "}";
//    public static final String QUERY_WHERE_FORSOURCE = "target_code='source' "
//            + "AND target_reg_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "}";
    
        public static final String QUERY_WHERE_FOR_TRANSACTION = "target_code=#{targetCode} "
            + "AND target_reg_moment=#{" + QUERY_PARAMETER_MOMENTCODE + "} "
                + "AND (target_request_type_code is null "
                + " OR target_request_type_code= #{" + QUERY_PARAMETER_REQUESTTYPE +  "})";
    
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "br_id")
    private String brId;
    @Column(name = "severity_code")
    private String severityCode;
    @Column(name="target_code")
    private String targetCode;
    @Column(name="target_application_moment")
    private String targetApplicationMoment;
    @Column(name="target_service_moment")
    private String targetServiceMoment;
    @Column(name="target_reg_moment")
    private String targetRegMoment;
    @Column(name="target_request_type_code")
    private String targetRequestTypeCode;
    @Column(name="target_rrr_type_code")
    private String targetRrrTypeCode;
    @Column(name="order_of_execution")
    private int orderOfExecution;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrId() {
        return brId;
    }

    public void setBrId(String brId) {
        this.brId = brId;
    }

    public String getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(String severityCode) {
        this.severityCode = severityCode;
    }

    public int getOrderOfExecution() {
        return orderOfExecution;
    }

    public void setOrderOfExecution(int orderOfExecution) {
        this.orderOfExecution = orderOfExecution;
    }

    public String getTargetApplicationMoment() {
        return targetApplicationMoment;
    }

    public void setTargetApplicationMoment(String targetApplicationMoment) {
        this.targetApplicationMoment = targetApplicationMoment;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public String getTargetRegMoment() {
        return targetRegMoment;
    }

    public void setTargetRegMoment(String targetRegMoment) {
        this.targetRegMoment = targetRegMoment;
    }

    public String getTargetRrrTypeCode() {
        return targetRrrTypeCode;
    }

    public void setTargetRrrTypeCode(String targetRrrTypeCode) {
        this.targetRrrTypeCode = targetRrrTypeCode;
    }

    public String getTargetServiceMoment() {
        return targetServiceMoment;
    }

    public void setTargetServiceMoment(String targetServiceMoment) {
        this.targetServiceMoment = targetServiceMoment;
    }

    public String getTargetRequestTypeCode() {
        return targetRequestTypeCode;
    }

    public void setTargetRequestTypeCode(String targetRequestTypeCode) {
        this.targetRequestTypeCode = targetRequestTypeCode;
    }

}
