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

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

/**
 * Entity representing the cadastre.cadastre_object table.
 *
 * @author soladev
 */
@Table(name = "cadastre_object", schema = "cadastre")
public class CadastreObject extends AbstractVersionedEntity {

    public static final String PARAM_CADASTRE_OBJECT_ID = "cadObjId";
    public static final String PARAM_NAME_FIRSTPART = "namePart1";
    public static final String PARAM_NAME_LASTPART = "namePart2";
    public static final String PARAM_USER_NAME = "username";

    /**
     * WHERE clause to return current&pending CO's based on search string
     * compared to first part and last part
     */
    public static final String QUERY_WHERE_SEARCHBYALLPARTS = "(status_code= 'current' or status_code= 'pending') and "
            + "compare_strings(#{search_string}, name_firstpart || ' ' || name_lastpart)";
    /**
     * WHERE clause to return current CO's based on search string compared to
     * first part and last part
     */
    public static final String QUERY_WHERE_SEARCHBYPARTS = "status_code= 'current' and "
            + "compare_strings(#{search_string}, name_firstpart || ' ' || name_lastpart)";
    /**
     * WHERE clause to return current CO's intersecting the specified point
     */
    public static final String QUERY_WHERE_SEARCHBYPOINT = "type_code= #{type_code} "
            + "and status_code= 'current' and "
            + "ST_Intersects(geom_polygon, ST_SetSRID(ST_Point(#{x}, #{y}), #{srid}))";
    /**
     * WHERE clause to return CO's linked to the specified ba_unit.id
     */
    public static final String QUERY_WHERE_SEARCHBYBAUNIT = "id in "
            + " (select spatial_unit_id from administrative.ba_unit_contains_spatial_unit "
            + "where ba_unit_id = #{ba_unit_id})";
    /**
     * WHERE clause to return current CO's linked to the specified service.id
     */
    public static final String QUERY_WHERE_SEARCHBYSERVICE = "status_code= 'current' "
            + "and transaction_id in "
            + " (select id from transaction.transaction where from_service_id = #{service_id}) ";
    /**
     * WHERE clause to return CO's linked to the specified transaction.id
     */
    public static final String QUERY_WHERE_SEARCHBYTRANSACTION
            = "transaction_id = #{transaction_id}";
    /**
     * WHERE clause to return current CO's matching type type_code and within
     * distance of the specified geometry
     */
    public static final String QUERY_WHERE_SEARCHBYGEOM = "type_code=#{type_code} "
            + "and status_code= 'current' and "
            + "ST_DWithin(geom_polygon, get_geometry_with_srid(#{geom}), "
            + "system.get_setting('map-tolerance')::double precision)";
    /**
     * ORDER BY clause used to order search results for the Search by parts
     * queries. Uses regex to order cadastre objects by lot number.
     */
    public static final String QUERY_ORDER_BY_SEARCHBYPARTS
            = "lpad(regexp_replace(name_firstpart, '\\D*', '', 'g'), 5, '0') "
            + "|| name_firstpart || name_lastpart";

    /**
     * WHERE clause to return non LRS CO's that match the name parts
     */
    public static final String QUERY_WHERE_VALIDATENAME = "name_firstpart = #{" + PARAM_NAME_FIRSTPART + "} "
            + "AND name_lastpart = #{" + PARAM_NAME_LASTPART + "} AND COALESCE(source_reference, '') != 'LRS'"; 

    /**
     * QUERY to execute the change_parcel_name procedure.
     */
    public static final String QUERY_CHANGE_PARCEL_NAME = "SELECT cadastre.change_parcel_name( "
            + "#{" + PARAM_CADASTRE_OBJECT_ID + "}, "
            + "#{" + PARAM_NAME_FIRSTPART + "}, "
            + "#{" + PARAM_NAME_LASTPART + "}, "
            + "#{" + PARAM_USER_NAME + "})";
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "type_code")
    private String typeCode;
    @Column(name = "approval_datetime")
    private Date approvalDatetime;
    @Column(name = "historic_datetime")
    private Date historicDatetime;
    @Column(name = "source_reference")
    private String sourceReference;
    @Column(name = "name_firstpart")
    private String nameFirstpart;
    @Column(name = "name_lastpart")
    private String nameLastpart;
    @Column(name = "status_code", insertable = false, updatable = false)
    private String statusCode;
    @Column(name = "transaction_id", updatable = false)
    private String transactionId;
    @Column(name = "geom_polygon")
    @AccessFunctions(onSelect = "st_asewkb(geom_polygon)",
            onChange = "get_geometry_with_srid(#{geomPolygon})")
    private byte[] geomPolygon;
    @ChildEntityList(parentIdField = "spatialUnitId")
    private List<SpatialValueArea> spatialValueAreaList;

    /**
     * No-arg constructor
     */
    public CadastreObject() {
        super();
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getApprovalDatetime() {
        return approvalDatetime;
    }

    public void setApprovalDatetime(Date approvalDatetime) {
        this.approvalDatetime = approvalDatetime;
    }

    public byte[] getGeomPolygon() {
        return geomPolygon;
    }

    public void setGeomPolygon(byte[] geomPolygon) { //NOSONAR
        this.geomPolygon = geomPolygon; //NOSONAR
    }

    public Date getHistoricDatetime() {
        return historicDatetime;
    }

    public void setHistoricDatetime(Date historicDatetime) {
        this.historicDatetime = historicDatetime;
    }

    public String getNameFirstpart() {
        return nameFirstpart;
    }

    public void setNameFirstpart(String nameFirstpart) {
        this.nameFirstpart = nameFirstpart;
    }

    public String getNameLastpart() {
        return nameLastpart;
    }

    public void setNameLastpart(String nameLastpart) {
        this.nameLastpart = nameLastpart;
    }

    public String getSourceReference() {
        return sourceReference;
    }

    public void setSourceReference(String sourceReference) {
        this.sourceReference = sourceReference;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public List<SpatialValueArea> getSpatialValueAreaList() {
        // Loaded eagerly by the CommonRepository
        return spatialValueAreaList;
    }

    public void setSpatialValueAreaList(List<SpatialValueArea> spatialValueAreaList) {
        this.spatialValueAreaList = spatialValueAreaList;
    }

    /**
     * Sets the transaction Id on the entity prior to save.
     */
    @Override
    public void preSave() {
        if (this.isNew() && this.getTransactionId() == null) {
            setTransactionId(LocalInfo.getTransactionId());
        }

        super.preSave();
    }
}
