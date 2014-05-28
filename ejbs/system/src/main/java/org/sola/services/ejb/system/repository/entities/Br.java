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

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.Localized;
import org.sola.services.common.repository.entities.AbstractEntity;

@Table(name = "br", schema = "system")
public class Br  extends AbstractEntity {
    
    @Id
    @Column
    private String id;
    @Column(name="display_name")
    private String displayName;
    @Column(name="technical_type_code")
    private String technicalTypeCode;
    @Column
    @Localized
    private String feedback;
    @Column
    private String description;
    @Column(name="technical_description")
    private String technicalDescription;
    @ChildEntityList(parentIdField = "brId")
    private List<BrDefinition> brDefinitionList;
    @ChildEntityList(parentIdField = "brId")
    private List<BrValidation> brValidationList;
    
    public Br(){
        super();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTechnicalDescription() {
        return technicalDescription;
    }

    public void setTechnicalDescription(String technicalDescription) {
        this.technicalDescription = technicalDescription;
    }
    
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTechnicalTypeCode() {
        return technicalTypeCode;
    }

    public void setTechnicalTypeCode(String technicalTypeCode) {
        this.technicalTypeCode = technicalTypeCode;
    }

    public List<BrDefinition> getBrDefinitionList() {
        brDefinitionList = brDefinitionList == null ? new ArrayList<BrDefinition>() : brDefinitionList;
        return brDefinitionList;
    }

    public void setBrDefinitionList(List<BrDefinition> brDefinitionList) {
        this.brDefinitionList = brDefinitionList;
    }

    public List<BrValidation> getBrValidationList() {
        brValidationList = brValidationList == null ? new ArrayList<BrValidation>() : brValidationList;
        return brValidationList;
    }

    public void setBrValidationList(List<BrValidation> brValidationList) {
        this.brValidationList = brValidationList;
    }
}
