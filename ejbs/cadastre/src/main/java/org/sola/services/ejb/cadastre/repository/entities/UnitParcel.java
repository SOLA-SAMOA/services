/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.ejb.cadastre.repository.entities;

import javax.persistence.Column;
import javax.persistence.Table;
import org.sola.services.common.repository.AccessFunctions;

/**
 * Entity representing the cadastre.spatial_unit_change table.
 */
@Table(name = "cadastre_object", schema = "cadastre")
public class UnitParcel extends CadastreObject {

    /**
     * Parameter name used to retrieve unit parcels by the unit parcel group Id.
     */
    public static final String QUERY_PARAMETER_UNITPARCELGROUPID = "unitParcelGroupId";
    public static final String QUERY_PARAMETER_TRANSACTIONID = "transactionId";
    /**
     * Where clause to retrieve unit parcels by the Unit Parcel Group Id.
     */
    public static final String QUERY_WHERE_UNITPARCELSBYGROUP = "type_code != 'parcel' "
            + " AND EXISTS (SELECT sug.spatial_unit_id FROM cadastre.spatial_unit_in_group sug "
            + "             WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
            + "             AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})";
    /**
     * Where clause to retrieve underlying parcels by the Unit Parcel Group Id.
     */
    public static final String QUERY_WHERE_PARCELSBYGROUP = "type_code = 'parcel' "
            + " AND EXISTS (SELECT sug.spatial_unit_id FROM cadastre.spatial_unit_in_group sug "
            + "             WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
            + "             AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})";
    /**
     * Locates unit parcels using the id of the transaction that created them.
     */
    public static final String QUERY_WHERE_BYTRANSACTIONID =
            " transaction_id = #{" + QUERY_PARAMETER_TRANSACTIONID + "}";
    /**
     * Locates pending unit parcels.
     */
    public static final String QUERY_WHERE_BYPENDINGUNIT =
            " id IN (SELECT sug.spatial_unit_id FROM cadastre.spatial_unit_in_group sug "
            + " WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
            + " AND sug.unit_parcel_status_code = 'pending'"
            + " AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})";
    /**
     * Locates unit parcels that are marked for delete when the associated application is approved.
     */
    public static final String QUERY_WHERE_BYDELETEONAPPROVAL =
            " id IN (SELECT sug.spatial_unit_id FROM cadastre.spatial_unit_in_group sug "
            + " WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
            + " AND sug.delete_on_approval "
            + " AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})";
    /**
     * To retrieve this column, it is necessary to use the BYUNITPARCELGROUP query.
     */
    @Column(insertable = false, updatable = false, name = "deleteOnApproval")
    @AccessFunctions(onSelect = "(SELECT delete_on_approval FROM cadastre.spatial_unit_in_group sug "
    + "  WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
    + "  AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})")
    private boolean deleteOnApproval;
    @Column(insertable = false, updatable = false, name = "unitParcelStatusCode")
    @AccessFunctions(onSelect = "(SELECT sug.unit_parcel_status_code FROM cadastre.spatial_unit_in_group sug "
    + "  WHERE sug.spatial_unit_id = cadastre.cadastre_object.id "
    + "  AND sug.spatial_unit_group_id = #{" + QUERY_PARAMETER_UNITPARCELGROUPID + "})")
    private String unitParcelStatusCode;

    public UnitParcel() {
        super();
    }

    public boolean isDeleteOnApproval() {
        return deleteOnApproval;
    }

    public void setDeleteOnApproval(boolean deleteOnApproval) {
        this.deleteOnApproval = deleteOnApproval;
    }

    public String getUnitParcelStatusCode() {
        return unitParcelStatusCode;
    }

    public void setUnitParcelStatusCode(String unitParcelStatusCode) {
        this.unitParcelStatusCode = unitParcelStatusCode;
    }
}
