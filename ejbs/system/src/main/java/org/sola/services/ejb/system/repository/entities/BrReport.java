/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.services.ejb.system.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.Localized;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 * Entity representing the system.br_report table. 
 * @author soladev
 */
@Table(name = "br_report", schema = "system")
public class BrReport extends  AbstractReadOnlyEntity {

    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "technical_type_code")
    private String technicalTypeCode;
    @Localized
    @Column(name = "feedback")
    private String feedback;
    @Column(name = "body")
    private String body;
    @Localized
    @Column(name = "description")
    private String description;
    @Column(name = "severity_code")
    private String severityCode;
    @Column(name = "moment_code")
    private String momentCode;
    @Column(name = "target_code")
    private String targetCode;
    @Column(name = "target_request_type_code")
    private String targetRequestTypeCode;
    @Column(name = "target_rrr_type_code")
    private String targetRrrTypeCode;
    @Column(name = "order_of_execution")
    private Integer orderOfExecution;
        
    
    
    
    public BrReport() {
        super();
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }
    
    
//    public String getId() {
//        return id;
//    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTechnicalTypeCode() {
        return technicalTypeCode;
    }

    public void setTechnicalTypeCode(String technicalTypeCode) {
        this.technicalTypeCode = technicalTypeCode;
    }
    
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
    
     public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(String severitycode) {
        this.severityCode = severitycode;
    }
    
    public String getMomentCode() {
        return momentCode;
    }

    public void setMomentCode(String momentcode) {
        this.momentCode = momentcode;
    }
    
     public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetcode) {
        this.targetCode = targetcode;
    }
    
    public String getTargetRequestTypeCode() {
        return targetRequestTypeCode;
    }

    public void setTargetRequestTypeCode(String targetrequesttypecode) {
        this.targetRequestTypeCode = targetrequesttypecode;
    }
    public String getTargetRrrTypeCode() {
        return targetRrrTypeCode;
    }

    public void setTargetRrrTypeCode(String targetrrrtypecode) {
        this.targetRrrTypeCode = targetrrrtypecode;
    }
    
    public Integer getOrderOfExecution() {
        return orderOfExecution;
    }

    public void setOrderOfExecution(Integer orderofexecution) {
        this.orderOfExecution = orderofexecution;
    }
    
}
