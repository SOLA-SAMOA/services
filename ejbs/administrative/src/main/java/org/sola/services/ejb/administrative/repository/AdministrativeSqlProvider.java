/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.ejb.administrative.repository;

import static org.apache.ibatis.jdbc.SqlBuilder.*;

/**
 * Class used to build complex SQL queries for use by other entities.
 *
 * @author soladev
 */
public class AdministrativeSqlProvider {

    public static final String PARAM_TRANSACTION_ID = "transactionId";
    public static final String PARAM_UNIT_PARCEL_GROUP_ID = "unitParcelGroupId";
    public static final String PARAM_BA_UNIT_ID_LIST = "baUnitIdList";
    public static final String PARAM_CURRENT_USER = "currentUser";

    public static String buildGetUnitEntitlementSql() {

        String sql = " WITH total_ent AS ( "
                + " SELECT CAST(SUM(ent.share) AS integer) AS total "
                + " FROM   administrative.ba_unit_target tar, "
                + "        administrative.rrr ent "
                + " WHERE  tar.transaction_id = #{" + PARAM_TRANSACTION_ID + "} "
                + " AND    ent.ba_unit_id = tar.ba_unit_id "
                + " AND    ent.status_code = 'current' "
                + " AND    ent.type_code = 'unitEntitlement' "
                + " AND    ent.share IS NOT NULL "
                + " AND    ent.share > 0) ";

        BEGIN();
        SELECT("pfr.party_id");
        SELECT("s.id AS share_id");
        SELECT("s.nominator");
        SELECT("s.denominator");
        SELECT("CAST(ent.share AS integer) AS entitlement");
        SELECT("t.total AS ent_total");

        FROM("administrative.ba_unit_target tar");
        FROM("administrative.rrr r");
        FROM("administrative.rrr ent");
        FROM("administrative.rrr_share s");
        FROM("administrative.party_for_rrr pfr");
        FROM("total_ent t");

        WHERE("tar.transaction_id = #{" + PARAM_TRANSACTION_ID + "}");
        WHERE("r.ba_unit_id = tar.ba_unit_id");
        WHERE("r.status_code = 'current'");
        WHERE("r.is_primary = true");
        WHERE("ent.ba_unit_id = r.ba_unit_id");
        WHERE("ent.status_code = 'current'");
        WHERE("ent.type_code = 'unitEntitlement'");
        WHERE("ent.share IS NOT NULL");
        WHERE("ent.share > 0");
        WHERE("s.rrr_id = r.id");
        WHERE("pfr.share_id = s.id");

        ORDER_BY("s.id");

        sql = sql + SQL();
        return sql;
    }

    /**
     * Creates the SQL String used to call the create strata properties stored
     * procedure.
     */
    public static String buildCreateStrataProperties() {

        String sql = "SELECT administrative.create_strata_properties( "
                + "#{" + PARAM_UNIT_PARCEL_GROUP_ID + "}, "
                + "string_to_array(#{" + PARAM_BA_UNIT_ID_LIST + "}, ','), "
                + "#{" + PARAM_TRANSACTION_ID + "}, "
                + "#{" + PARAM_CURRENT_USER + "})";
        return sql;
    }

    /**
     * Creates the SQL String used to flag strata properties for termination
     */
    public static String buildTerminateStrataProperties() {
        String result;
        result = "INSERT INTO administrative.ba_unit_target (ba_unit_id, transaction_id, change_user) ";
        BEGIN();
        SELECT("b.id");
        SELECT("#{" + PARAM_TRANSACTION_ID + "} ");
        SELECT("#{" + PARAM_CURRENT_USER + "} ");
        FROM("administrative.ba_unit b");
        WHERE("b.id = ANY (string_to_array(#{" + PARAM_BA_UNIT_ID_LIST + "}, ','))");
        WHERE("b.type_code = 'strataUnit'");
        WHERE("b.status_code = 'current'");
        WHERE("NOT EXISTS (SELECT t.ba_unit_id t FROM administrative.ba_unit_target t WHERE t.ba_unit_id = b.id)");
        result += SQL();
        return result;
    }

    /**
     * Creates the SQL String used to remove the flag to terminate strata
     * properties
     */
    public static String buildUndoTerminateStrataProperties() {
        String result;
        BEGIN();
        DELETE_FROM("administrative.ba_unit_target tar");
        WHERE("tar.ba_unit_id = ANY (string_to_array(#{" + PARAM_BA_UNIT_ID_LIST + "}, ','))");
        WHERE("tar.transaction_id = #{" + PARAM_TRANSACTION_ID + "}");
        WHERE("EXISTS (SELECT t.id "
                + " FROM transaction.transaction t, "
                + "      administrative.ba_unit b "
                + " WHERE t.status_code = 'pending' "
                + " AND   b.type_code = 'strataUnit' "
                + " AND   t.id = tar.transaction_id "
                + " AND   b.id = tar.ba_unit_id )");
        result = SQL();
        return result;
    }
}
