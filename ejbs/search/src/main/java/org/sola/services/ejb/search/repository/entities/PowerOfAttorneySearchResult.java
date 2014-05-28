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
import org.sola.services.common.repository.CommonSqlProvider;

public class PowerOfAttorneySearchResult extends SourceSearchResult {
    public static final String QUERY_PARAM_PERSON_NAME = "personName";
    public static final String QUERY_PARAM_ATTORNEY_NAME = "attorneyName";
    
    public static final String SEARCH_POWER_OF_ATTORNEY_QUERY = SELECT_PART 
            + ", p.person_name, p.attorney_name"
            + " FROM ((source.source AS s INNER JOIN source.power_of_attorney p ON s.id=p.id)"
            + " LEFT JOIN transaction.reg_status_type AS t on s.status_code = t.code) "
            + " LEFT JOIN source.administrative_source_type AS st ON s.type_code = st.code "
            + WHERE_PART 
            + " AND POSITION(COALESCE(#{" + QUERY_PARAM_PERSON_NAME + "}, '') IN COALESCE(p.person_name, '')) > 0"
            + " AND POSITION(COALESCE(#{" + QUERY_PARAM_ATTORNEY_NAME + "}, '') IN COALESCE(p.attorney_name, '')) > 0"
            + " LIMIT 101";
      
    @Column(name="attorney_name")
    private String attorneyName;
    @Column(name="person_name")
    private String personName;
    
    public PowerOfAttorneySearchResult(){
        super();
    }

    public String getAttorneyName() {
        return attorneyName;
    }

    public void setAttorneyName(String attorneyName) {
        this.attorneyName = attorneyName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
    
}
