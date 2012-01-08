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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.search.repository;

import org.sola.services.common.entities.AbstractResultEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;

/**
 * This entity is used to provide the queries for the layer in the gis component.
 * @author manoku
 */
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "SpatialResult.getParcels",
    query = "select co.id, co.name_firstpart || '/' || co.name_lastpart as label, "
        + " st_asewkb(co.geom_polygon) as the_geom from cadastre.cadastre_object co "
        + " where type_code= 'parcel' and status_code= 'current' and "
        + " ST_Intersects(co.geom_polygon, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class),
    @NamedNativeQuery(name = "SpatialResult.getParcelsPending",
    query = "select co.id, co.name_firstpart || '/' || co.name_lastpart as label, "
        + " st_asewkb(co.geom_polygon) as the_geom from cadastre.cadastre_object co "
        + " where type_code= 'parcel' and status_code= 'pending' and "
        + " ST_Intersects(co.geom_polygon, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class),
    @NamedNativeQuery(name = "SpatialResult.getSurveyControls",
    query = "select id, label, st_asewkb(geom) as the_geom from cadastre.survey_control where "
        + " ST_Intersects(geom, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class),
    @NamedNativeQuery(name = "SpatialResult.getRoads",
    query = "select id, label, st_asewkb(geom) as the_geom from cadastre.road where "
        + " ST_Intersects(geom, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class),
    @NamedNativeQuery(name = "SpatialResult.getPlaceNames",
    query = "select id, label, st_asewkb(geom) as the_geom from cadastre.place_name where "
        + " ST_Intersects(geom, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class),
    @NamedNativeQuery(name = "SpatialResult.getApplications",
    query = "select id, nr as label, st_asewkb(location) as the_geom from application.application where "
        + " ST_Intersects(location, SetSRID(ST_MakeBox3D(ST_Point(?1, ?2),ST_Point(?3, ?4)), ?5))",
    readOnly = true,
    resultClass = SpatialResult.class)
})
public class SpatialResult extends AbstractResultEntity {

    @Column(name = "label")
    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    @Column(name = "the_geom")
    private byte[] theGeom;

    public void setTheGeom(byte[] theGeom) {
        this.theGeom = theGeom;
    }

    public byte[] getTheGeom() {
        return theGeom;
    }
}
