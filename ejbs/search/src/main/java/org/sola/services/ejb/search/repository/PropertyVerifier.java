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

import org.sola.services.common.entities.AbstractResultEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 *
 * It checks different parameters for property object.
 * 
 */
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "PropertyVerifier.verify",
    query = "select id,  " +
    "(select count(*)>0 from administrative.ba_unit_contains_spatial_unit bcs " +
    " where ba.id = bcs.ba_unit_id ) as has_location," +
    "(select coalesce(string_agg(nr, ','), '') from application.application a " +
    " inner join application.application_property ap on a.id = ap.application_id " +
    " where  ap.name_firstpart= ba.name_firstpart and ap.name_lastpart= ba.name_lastpart) " +
    " as applications_where_found from administrative.ba_unit ba where type_code = 'basicPropertyUnit' " +
    " and ba.name_firstpart = ?1 and ba.name_lastpart = ?2", 
    readOnly= true,
    resultClass = PropertyVerifier.class)})
public class PropertyVerifier extends AbstractResultEntity {
    
    @Column(name="has_location")
    private boolean hasLocation;
    
    @Column(name="applications_where_found")
    private String applicationsWhereFound;

    /**
     * @return Returns true if property object has location on the map and false if not.
     */
    public boolean isHasLocation() {
        return hasLocation;
    }

    /**
     * @return Returns comma separated list of applications, which are currently in progress for a given property
     */
    public String getApplicationsWhereFound() {
        return applicationsWhereFound;
    }
}
