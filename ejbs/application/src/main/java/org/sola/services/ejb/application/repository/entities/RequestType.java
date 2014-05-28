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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.application.repository.entities;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.DefaultSorter;
import org.sola.services.common.repository.entities.AbstractCodeEntity;

/**
 *  Entity representing the application.request_type code table. This code entity includes
 *  some additional fields beyond the standard code, description, display_value and status used
 *  for most code entities. 
 * @author soladev
 */
@Table(name = "request_type", schema = "application")
@DefaultSorter(sortString="display_value")
public class RequestType extends AbstractCodeEntity {
    
    public static String CADASTRE_CHANGE = "cadastreChange";

    @Column(name = "nr_days_to_complete")
    private int nrDaysToComplete;
    @Column(name = "request_category_code")
    private String requestCategoryCode;
    @Column(name = "base_fee")
    private BigDecimal baseFee;
    @Column(name = "area_base_fee")
    private BigDecimal areaBaseFee;
    @Column(name = "value_base_fee")
    private BigDecimal valueBaseFee;
    @Column(name = "nr_properties_required")
    private int nrPropertiesRequired;
    @Column(name = "notation_template")
    private String notationTemplate;
    @Column(name = "type_action_code")
    private String typeActionCode;
    @Column(name = "rrr_type_code")
    private String rrrTypeCode;
    @ChildEntityList(parentIdField = "requestTypeCode")
    private List<RequestTypeRequiresSourceType> sourceTypeCodes;

    public RequestType() {
        super();
    }

    public BigDecimal getAreaBaseFee() {
        return areaBaseFee;
    }

    public void setAreaBaseFee(BigDecimal areaBaseFee) { 
        this.areaBaseFee = areaBaseFee;
    }

    public BigDecimal getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(BigDecimal baseFee) {
        this.baseFee = baseFee;
    }

    public String getNotationTemplate() {
        return notationTemplate;
    }

    public void setNotationTemplate(String notationTemplate) {
        this.notationTemplate = notationTemplate;
    }

    public int getNrDaysToComplete() {
        return nrDaysToComplete;
    }

    public void setNrDaysToComplete(int nrDaysToComplete) {
        this.nrDaysToComplete = nrDaysToComplete;
    }

    public int getNrPropertiesRequired() {
        return nrPropertiesRequired;
    }

    public void setNrPropertiesRequired(int nrPropertiesRequired) {
        this.nrPropertiesRequired = nrPropertiesRequired;
    }

    public String getRequestCategoryCode() {
        return requestCategoryCode;
    }

    public void setRequestCategoryCode(String requestCategoryCode) { 
        this.requestCategoryCode = requestCategoryCode;
    }

    public String getTypeActionCode() {
        return typeActionCode;
    }

    public void setTypeActionCode(String typeActionCode) {
        this.typeActionCode = typeActionCode;
    }

    public String getRrrTypeCode() {
        return rrrTypeCode;
    }

    public void setRrrTypeCode(String rrrTypeCode) {
        this.rrrTypeCode = rrrTypeCode;
    }

    public BigDecimal getValueBaseFee() {
        return valueBaseFee;
    }

    public void setValueBaseFee(BigDecimal valueBaseFee) {
        this.valueBaseFee = valueBaseFee;
    }

    public List<RequestTypeRequiresSourceType> getSourceTypeCodes() {
        return sourceTypeCodes;
    }

    public void setSourceTypeCodes(List<RequestTypeRequiresSourceType> sourceTypeCodes) {
        this.sourceTypeCodes = sourceTypeCodes;
    }
}
