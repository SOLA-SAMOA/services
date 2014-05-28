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
package org.sola.services.ejb.cadastre.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

/**
 * Entity representing the cadastre.spatial_unit_change table.
 */
@Table(name = "spatial_unit_change", schema = "cadastre")
public class SpatialUnitChange extends AbstractVersionedEntity {

    private static final String GEOMETRY_TYPE_POINT = "POINT";
    /**
     * Parameter name used for the transaction id value.
     */
    public static final String QUERY_PARAMETER_TRANSACTIONID = "transactionId";
    /**
     * Where clause to retrieve SpatialUnitChange by transaction id. <p>> transaction_id =
     * #{transactionId}</p>
     */
    public static final String QUERY_WHERE_BYTRANSACTIONID = "transaction_id = "
            + "#{" + QUERY_PARAMETER_TRANSACTIONID + "}";
    @Id
    @Column
    private String id;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "spatial_unit_id")
    private String spatialUnitId;
    @Column
    private String label;
    @Column(name = "delete_on_approval")
    private boolean deleteOnApproval;
    //Translates the cadastre.level id to and from the cadastre.level name
    @Column(name = "level_id")
    @AccessFunctions(onSelect = "(SELECT l.\"name\" FROM cadastre.level l WHERE l.id = level_id)",
    onChange = "(SELECT l.id FROM cadastre.level l WHERE l.\"name\" = #{levelName})")
    private String levelName;
    @Column
    @AccessFunctions(onSelect = "st_asewkb(geom)",
    onChange = "get_geometry_with_srid(#{geom})")
    private byte[] geom;
    @Column(name = "geom_type", insertable = false, updatable = false)
    @AccessFunctions(onSelect = "geometrytype(geom)")
    private String geomType;
    @Column(name = "level", insertable = false, updatable = false)
    @AccessFunctions(onSelect = "level_id")
    private String levelId;

    public SpatialUnitChange() {
        super();
    }

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public String getLevelId() {
        return levelId;
    }

    public void setLevelId(String levelId) {
        this.levelId = levelId;
    }

    public boolean isDeleteOnApproval() {
        return deleteOnApproval;
    }

    public void setDeleteOnApproval(boolean deleteOnApproval) {
        this.deleteOnApproval = deleteOnApproval;
    }

    public String getSpatialUnitId() {
        return spatialUnitId;
    }

    public void setSpatialUnitId(String spatialUnitId) {
        this.spatialUnitId = spatialUnitId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGeomType() {
        return geomType;
    }

    public void setGeomType(String geomType) {
        this.geomType = geomType;
    }

    /**
     * Determines if the geometry is a point or not. Returns false if the geometry is null.
     */
    public boolean isPoint() {
        boolean result = false;
        if (getGeomType() != null) {
            result = getGeomType().equals(GEOMETRY_TYPE_POINT);
        }
        return result;
    }

    @Override
    public void preSave() {
        if (this.isNew() && this.getTransactionId() == null) {
            setTransactionId(LocalInfo.getTransactionId());
        }
        super.preSave();
    }
}
