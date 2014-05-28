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
package org.sola.services.ejb.search.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

@Table(name = "br", schema = "system")
public class BrSearchResult extends AbstractReadOnlyEntity {

    public static final String SELECT_QUERY = 
            "SELECT DISTINCT b.id, b.display_name, b.technical_type_code, get_translation(b.feedback,#{lang}) AS feedback "
            + "FROM system.br b LEFT JOIN system.br_validation bv ON b.id = bv.br_id "
            + "WHERE POSITION(LOWER(COALESCE(#{displayName}, '')) IN LOWER(COALESCE(b.display_name, ''))) > 0 "
            + "AND (COALESCE(#{technicalTypeCode}, '') = '' OR LOWER(COALESCE(#{technicalTypeCode}, '')) = LOWER(COALESCE(b.technical_type_code, ''))) "
            + "AND (COALESCE(#{targetCode}, '') = '' OR LOWER(COALESCE(#{targetCode}, '')) = LOWER(COALESCE(bv.target_code, ''))) "
            + " ORDER BY b.display_name";
    @Id
    @Column
    private String id;
    
    @Column(name="display_name")
    private String displayName;
    
    @Column(name="technical_type_code")
    private String technicalTypeCode;
    
    @Column
    private String feedback;
    
    public BrSearchResult() {
        super();
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
}
