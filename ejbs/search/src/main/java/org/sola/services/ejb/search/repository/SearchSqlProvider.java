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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.search.repository;

import static org.apache.ibatis.jdbc.SqlBuilder.*;
import org.sola.services.ejb.search.repository.entities.BaUnitSearchResult;

/**
 *
 * @author soladev
 */
public class SearchSqlProvider {

    public static final String PARAM_APPLICATION_ID = "applicationId";
    private static final String APPLICATION_GROUP = "application";
    private static final String SERVICE_GROUP = "service";
    private static final String RRR_GROUP = "rrr";
    private static final String CHANGE_ACTION = "changed";

    public static String buildApplicationLogSql() {
        String sql;
        int sortClassifier = 1;

        // Application 
        BEGIN();
        SELECT("'" + APPLICATION_GROUP + "' AS record_group");
        SELECT("'" + APPLICATION_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app1.id AS record_id");
        SELECT("app1.rowversion AS record_sequence");
        SELECT("app1.nr AS nr");
        SELECT("CASE WHEN COALESCE(prev.action_code, 'null') = app1.action_code "
                + " THEN '" + CHANGE_ACTION + "' ELSE app1.action_code END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app1.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app1.change_user::text)"
                + " AS user_fullname");
        FROM("application.application app1 "
                + " LEFT JOIN application.application_historic prev "
                + " ON app1.id = prev.id AND (app1.rowversion - 1) = prev.rowversion");
        WHERE("app1.id = #{" + PARAM_APPLICATION_ID + "}");

        sql = SQL() + " UNION ";
        sortClassifier++;

        //Application History
        BEGIN();
        SELECT("'" + APPLICATION_GROUP + "' AS record_group");
        SELECT("'" + APPLICATION_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app_hist.id AS record_id");
        SELECT("app_hist.rowversion AS record_sequence");
        SELECT("app_hist.nr AS nr");
        SELECT("CASE WHEN COALESCE(prev.action_code, 'null') = app_hist.action_code "
                + " THEN '" + CHANGE_ACTION + "' ELSE app_hist.action_code END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app_hist.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app_hist.change_user::text)"
                + " AS user_fullname");
        FROM("application.application_historic app_hist "
                + " LEFT JOIN application.application_historic prev "
                + " ON app_hist.id = prev.id AND (app_hist.rowversion - 1) = prev.rowversion");
        WHERE("app_hist.id = #{" + PARAM_APPLICATION_ID + "}");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // Service
        BEGIN();
        SELECT("'" + SERVICE_GROUP + "' AS record_group");
        SELECT("ser1.request_type_code AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("ser1.id AS record_id");
        SELECT("ser1.rowversion AS record_sequence");
        SELECT("ser1.service_order::text AS nr");
        SELECT("CASE WHEN COALESCE(prev.action_code, 'null') = ser1.action_code "
                + " THEN '" + CHANGE_ACTION + "' ELSE ser1.action_code END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("ser1.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = ser1.change_user::text)"
                + " AS user_fullname");
        FROM("application.service ser1 "
                + " LEFT JOIN application.service_historic prev "
                + " ON ser1.id = prev.id AND (ser1.rowversion - 1) = prev.rowversion");
        WHERE("ser1.application_id = #{" + PARAM_APPLICATION_ID + "}");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // Service History
        BEGIN();
        SELECT("'" + SERVICE_GROUP + "' AS record_group");
        SELECT("ser_hist.request_type_code AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("ser_hist.id AS record_id");
        SELECT("ser_hist.rowversion AS record_sequence");
        SELECT("ser_hist.service_order::text AS nr");
        SELECT("CASE WHEN COALESCE(prev.action_code, 'null') = ser_hist.action_code "
                + " THEN '" + CHANGE_ACTION + "' ELSE ser_hist.action_code END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("ser_hist.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = ser_hist.change_user::text)"
                + " AS user_fullname");
        FROM("application.service ser");
        FROM("application.service_historic ser_hist"
                + " LEFT JOIN application.service_historic prev "
                + " ON ser_hist.id = prev.id AND (ser_hist.rowversion - 1) = prev.rowversion");
        WHERE("ser.application_id = #{" + PARAM_APPLICATION_ID + "}");
        WHERE("ser_hist.rowidentifier = ser.rowidentifier");

//        sql = sql + SQL() + " UNION ";
//        sortClassifier++;

//        // RRR
//        BEGIN();
//        SELECT("'" + RRR_GROUP + "' AS record_group");
//        SELECT("rrr.type_code AS record_type");
//        SELECT(sortClassifier + " as sort_classifier");
//        SELECT("rrr.id AS record_id");       
//        SELECT("rrr.rowversion AS record_sequence");
//        SELECT("rrr.nr::text AS nr");
//        SELECT("'changesMade'  AS action_code");
//        SELECT("note.notation_text AS notation");
//        SELECT("COALESCE(note.change_time, rrr.change_time) AS change_time");
//        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
//                + " FROM system.appuser"
//                + " WHERE appuser.username::text = COALESCE(note.change_user, rrr.change_user))"
//                + " AS user_fullname");
//        FROM("application.service ser");
//        FROM("transaction.transaction tran");
//        FROM("administrative.rrr LEFT JOIN administrative.notation note ON "
//                + " rrr.id = note.rrr_id");
//        WHERE("ser.application_id = #{" + PARAM_APPLICATION_ID + "}");
//        WHERE("tran.from_service_id = ser.id");
//        WHERE("rrr.transaction_id = tran.id");

        ORDER_BY("change_time, sort_classifier, nr");

        sql = sql + SQL();

        return sql;
    }

    /**
     * Uses the BA Unit Search parameters to build an appropriate SQL Query. This method does not
     * inject the search parameter values into the SQL as that would prevent the database from
     * performing statement caching.
     *
     * @param nameFirstPart The name first part search parameter value
     * @param nameLastPart The name last part search parameter value
     * @param owernName The owner name search parameter value
     * @return SQL String
     */
    public static String buildSearchBaUnitSql(String nameFirstPart,
            String nameLastPart, String owernName) {
        String sql;
        BEGIN();
        SELECT("DISTINCT prop.id");
        SELECT("prop.name");
        SELECT("prop.name_firstpart");
        SELECT("prop.name_lastpart");
        SELECT("prop.status_code");
        SELECT("(SELECT string_agg(COALESCE(p1.name, '') || ' ' || COALESCE(p1.last_name, ''), '::::') "
                + "FROM administrative.rrr rrr1, administrative.party_for_rrr pr1, party.party p1 "
                + "WHERE rrr1.ba_unit_id = prop.id "
                + "AND rrr1.status_code = 'current' "
                + "AND pr1.rrr_id = rrr1.id "
                + "AND p1.id = pr1.party_id ) AS rightholders");
        FROM("administrative.ba_unit prop");
        if (owernName != null) {
            FROM("administrative.rrr rrr");
            FROM("administrative.party_for_rrr pr");
            FROM("party.party p");
            WHERE("rrr.ba_unit_id = prop.id");
            WHERE("rrr.status_code = 'current'");
            WHERE("pr.rrr_id = rrr.id");
            WHERE("p.id = pr.party_id");
            WHERE("compare_strings(#{" + BaUnitSearchResult.QUERY_PARAM_OWNER_NAME
                    + "}, COALESCE(p.name, '') || ' ' || COALESCE(p.last_name, ''))");
        }
        if (nameFirstPart != null) {
            WHERE("compare_strings(#{" + BaUnitSearchResult.QUERY_PARAM_NAME_FIRSTPART
                    + "}, COALESCE(prop.name_firstpart, ''))");
        }
        if (nameLastPart != null) {
            WHERE("compare_strings(#{" + BaUnitSearchResult.QUERY_PARAM_NAME_LASTPART
                    + "}, COALESCE(prop.name_lastpart, ''))");
        }
        ORDER_BY(BaUnitSearchResult.QUERY_ORDER_BY + " LIMIT 100");
        sql = SQL();
        return sql;
    }
}
