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
package org.sola.services.ejb.search.repository;

import javax.persistence.Column;
import javax.persistence.Entity;
import org.sola.services.common.entities.AbstractResultEntity;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 *
 * It checks if the validates
 * @author Elton Manoku
 */
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "TestSpatial.get",
    query = "select id, vl, st_asewkb(the_geom) as the_geom from test.test",
    readOnly = true,
    resultClass = TestSpatial.class),
    @NamedNativeQuery(name = "TestSpatial.get2",
    query = "select '1' as id, '' as vl, st_asewkb(st_geomfromtext('POINT(1 1)', 32702)) as the_geom",
    readOnly = true,
    resultClass = TestSpatial.class),
    @NamedNativeQuery(name = "TestSpatial.getRaw",
    query = "select '1' as id, '' as vl, st_geomfromtext('POINT(1 1)', 32702) as the_geom",
    readOnly = true,
    resultClass = TestSpatial.class),
    @NamedNativeQuery(name = "TestSpatial.get3",
    query = "select st_astext(st_geomfromwkb(?)) as vl",
    readOnly = true,
    resultSetMapping = "SingleObjectValueResult"),
    @NamedNativeQuery(name = "TestSpatial.get4",
    query = "select st_astext(?) as vl",
    readOnly = true,
    resultSetMapping = "SingleObjectValueResult")})
public class TestSpatial extends AbstractResultEntity {

    @Column(name = "the_geom")
    private byte[] theGeom;
    private String vl;
    

    /**
     * @return the theGeom
     */
    public byte[] getTheGeom() {
        return theGeom;
    }

    /**
     * @return the vl
     */
    public String getVl() {
        return vl;
    }

    /**
     * @param vl the vl to set
     */
    public void setVl(String vl) {
        this.vl = vl;
    }
    
    
}
