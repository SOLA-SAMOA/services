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

import java.util.List;
import static org.apache.ibatis.jdbc.SqlBuilder.*;
import org.sola.services.ejb.search.repository.entities.BaUnitSearchResult;

/**
 *
 * @author soladev
 */
public class SearchSqlProvider {

    public static final String PARAM_APPLICATION_ID = "applicationId";
    public static final String PARAM_ID_LIST = "idVal";
    public static final String PARAM_SERVICE_ID = "serviceId";
    public static final String PARAM_UNIT_PARCEL_GROUP_NAME = "unitParcelGroupName";
    private static final String APPLICATION_GROUP = "application";
    private static final String SERVICE_GROUP = "service";
    private static final String RRR_GROUP = "rrr";
    private static final String PROPERTY_GROUP = "Property";
    private static final String SOURCE_GROUP = "Source";
    private static final String AGENT_GROUP = "Agent";
    private static final String CONTACT_PERSON_GROUP = "Contact person";
    private static final String CHANGE_ACTION = "changed";
    private static final String ADDED_PROPERTY = "ADDED PROPERTY: ";
    private static final String DELETED_PROPERTY = "DELETED PROPERTY: ";
    private static final String ADDED_SOURCE = "ADDED DOCUMENT: Ref#";
    private static final String DELETED_SOURCE = "REMOVED DOCUMENT: Ref#";
    private static final String ADDED_AGENT = "ADDED AGENT: ";
    private static final String DELETED_AGENT = "REMOVED AGENT: ";
    private static final String ADDED_CONTACT_PERSON = "ADDED CONTACT PERSON: ";
    private static final String DELETED_CONTACT_PERSON = "REMOVED CONTACT PERSON: ";

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

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // Property
        BEGIN();
        SELECT("'" + PROPERTY_GROUP + "' AS record_group");
        SELECT("'" + PROPERTY_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("prop1.id AS record_id");
        SELECT("prop1.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("replace(prop1.change_action,'i','" + ADDED_PROPERTY + "')||prop1.name_firstpart||'/'||prop1.name_lastpart AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("prop1.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = prop1.change_user::text)"
                + " AS user_fullname");
        FROM("application.application_property prop1 ");
        WHERE("prop1.application_id = #{" + PARAM_APPLICATION_ID + "}");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // Application property History
        BEGIN();
        SELECT("'" + PROPERTY_GROUP + "' AS record_group");
        SELECT("'" + PROPERTY_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("prop_hist.id AS record_id");
        SELECT("prop_hist.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("CASE WHEN prop_hist.change_action = 'i' then replace(prop_hist.change_action,'i','" + ADDED_PROPERTY + "')||' - '|| prop_hist.name_firstpart|| '/'||prop_hist.name_lastpart"
                + "  WHEN prop_hist.change_action = 'd' then replace(prop_hist.change_action,'d','" + DELETED_PROPERTY + "')||' - '|| prop_hist.name_firstpart|| '/'||prop_hist.name_lastpart"
                + "  END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("prop_hist.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = prop_hist.change_user::text)"
                + " AS user_fullname");
        FROM("application.application_property_historic prop_hist");
        WHERE("prop_hist.application_id = #{" + PARAM_APPLICATION_ID + "}");


        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // SOURCE
        BEGIN();
        SELECT("'" + SOURCE_GROUP + "' AS record_group");
        SELECT("'" + SOURCE_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("source1.source_id AS record_id");
        SELECT("source1.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("replace(source1.change_action,'i','" + ADDED_SOURCE + "')||coalesce(source.reference_nr,'')||' - '||coalesce(source.type_code,'')   AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("source1.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = source1.change_user::text)"
                + " AS user_fullname");
        FROM("application.application_uses_source source1  "
                + " LEFT JOIN source.source source "
                + " ON source1.source_id = source.id ");
        WHERE("source1.application_id = #{" + PARAM_APPLICATION_ID + "}");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // Application Source History
        BEGIN();
        SELECT("'" + SOURCE_GROUP + "' AS record_group");
        SELECT("'" + SOURCE_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("source1.source_id AS record_id");
        SELECT("source1.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("CASE WHEN source1.change_action = 'i' then replace(source1.change_action,'i','" + ADDED_SOURCE + "')||coalesce(source.reference_nr,'')||' - '||coalesce(source.type_code,'') "
                + "  WHEN source1.change_action = 'd' then replace(source1.change_action,'d','" + DELETED_SOURCE + "')||coalesce(source.reference_nr,'')||' - '||coalesce(source.type_code,'') "
                + "  END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("source1.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = source1.change_user::text)"
                + " AS user_fullname");
        FROM("application.application_uses_source_historic source1  "
                + " LEFT JOIN source.source source "
                + " ON source1.source_id = source.id ");
        WHERE("source1.application_id = #{" + PARAM_APPLICATION_ID + "}");


        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // AGENT 
        BEGIN();

        SELECT("'" + AGENT_GROUP + "' AS record_group");
        SELECT("'" + AGENT_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app.agent_id AS record_id");
        SELECT("app.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("replace(party.change_action,'i','" + ADDED_AGENT + "')||' - '||party.name||' '||coalesce(party.last_name,'') AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app.change_user::text)"
                + " AS user_fullname");
        FROM("application.application app");
        FROM("party.party party");
        WHERE("app.id = #{" + PARAM_APPLICATION_ID + "}");
        WHERE("app.agent_id=party.id");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // AGENT History
        BEGIN();

        SELECT("'" + AGENT_GROUP + "' AS record_group");
        SELECT("'" + AGENT_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app.agent_id AS record_id");
        SELECT("app.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("CASE WHEN (app.change_action='i') then replace(party.change_action,'i','" + ADDED_AGENT + "')||' - '||coalesce(party.name,'')||' '||coalesce(party.last_name,'')"
                + " ELSE  replace(app.change_action,app.change_action,'" + DELETED_AGENT + "')||' - '||coalesce(party.name,'')||' '||coalesce(party.last_name,'')"
                + " END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app.change_user::text)"
                + " AS user_fullname");
        FROM("application.application new_app");
        FROM("application.application_historic app"
                + " LEFT JOIN party.party party"
                + "  ON app.agent_id = party.id");
        WHERE("app.id = #{" + PARAM_APPLICATION_ID + "}");
        WHERE("app.agent_id != new_app.agent_id");
        WHERE("app.agent_id=party.id");
        WHERE("((app.rowversion - 1) = new_app.rowversion OR (app.rowversion) = new_app.rowversion)");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;


        // contact_person 
        BEGIN();

        SELECT("'" + CONTACT_PERSON_GROUP + "' AS record_group");
        SELECT("'" + CONTACT_PERSON_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app.contact_person_id AS record_id");
        SELECT("app.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("replace(party.change_action,app.change_action,'" + ADDED_CONTACT_PERSON + "')||' - '||party.name||' '||coalesce(party.last_name,'') AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app.change_user::text)"
                + " AS user_fullname");
        FROM("application.application app");
        FROM("party.party party");
        WHERE("app.id = #{" + PARAM_APPLICATION_ID + "}");
        WHERE("app.contact_person_id=party.id");

        sql = sql + SQL() + " UNION ";
        sortClassifier++;

        // contact_person History
        BEGIN();

        SELECT("'" + CONTACT_PERSON_GROUP + "' AS record_group");
        SELECT("'" + CONTACT_PERSON_GROUP + "' AS record_type");
        SELECT(sortClassifier + " as sort_classifier");
        SELECT("app.contact_person_id AS record_id");
        SELECT("app.rowversion AS record_sequence");
        SELECT("''::text AS nr");
        SELECT("CASE WHEN (app.change_action='i') then replace(party.change_action,'i','" + ADDED_CONTACT_PERSON + "')||' - '||coalesce(party.name,'')||' '||coalesce(party.last_name,'')"
                + " ELSE  replace(app.change_action,app.change_action,'" + DELETED_CONTACT_PERSON + "')||' - '||coalesce(party.name,'')||' '||coalesce(party.last_name,'')"
                + " END AS action_code");
        SELECT("NULL::text AS notation");
        SELECT("app.change_time");
        SELECT("(SELECT (appuser.first_name::text || ' '::text) || appuser.last_name::text"
                + " FROM system.appuser"
                + " WHERE appuser.username::text = app.change_user::text)"
                + " AS user_fullname");
        FROM("application.application new_app");
        FROM("application.application_historic app"
                + " LEFT JOIN party.party_historic party"
                + "  ON app.contact_person_id = party.id");
        WHERE("app.id = #{" + PARAM_APPLICATION_ID + "}");
        WHERE("app.contact_person_id != new_app.contact_person_id");
        WHERE("app.contact_person_id=party.id");
        WHERE("((app.rowversion - 1) = new_app.rowversion OR (app.rowversion) = new_app.rowversion)");

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
            WHERE("compare_strings(#{" + BaUnitSearchResult.QUERY_PARAM_OWNER_NAME + "}, "
                    + "COALESCE(p.name, '') || ' ' || COALESCE(p.last_name, '') || ' ' || COALESCE(p.alias, ''))");
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

    /**
     * Samoa Customization - Creates the query to determine the Unit Development Number for the
     * Spatial Unit Group linked to the service or one of the BA Units in the BA Unit id list. Uses
     * an IN clause to check all of the BA Unit ids.
     *
     * @param serviceId The id of the service. Can be NULL
     * @param baUnitIdListCount The number of ba unit ids to check. Can be NULL or empty.
     * @return
     */
    public static String buildGetUnitDevNrSql(String serviceId, int baUnitIdListCount) {
        String sql;
        BEGIN();
        SELECT("sg.name");
        FROM("cadastre.spatial_unit_group sg");
        if (serviceId != null) {
            // Check if any BA Unit associated to the service is part of a Unit Development
            WHERE("sg.id IN (SELECT sig.spatial_unit_group_id "
                    + "      FROM   administrative.ba_unit_contains_spatial_unit baco, "
                    + "             administrative.ba_unit ba, "
                    + "             transaction.transaction t,"
                    + "             cadastre.spatial_unit_in_group sig "
                    + "      WHERE  t.from_service_id = #{" + PARAM_SERVICE_ID + "} "
                    + "      AND    ba.transaction_id = t.id "
                    + "      AND    baco.ba_unit_id = ba.id "
                    + "      AND    sig.spatial_unit_id = baco.spatial_unit_id) ");
            OR();
            // Check if the service transction is directly linked to a Unit Development
            WHERE("sg.id IN (SELECT t.spatial_unit_group_id "
                    + "      FROM   transaction.transaction t "
                    + "      WHERE  t.from_service_id = #{" + PARAM_SERVICE_ID + "}) ");
        }

        if (baUnitIdListCount > 0) {
            // Check if any of the BA Units are part of a Unit Development via thier parcels
            String whereClause = "sg.id IN (SELECT sig.spatial_unit_group_id "
                    + "      FROM   cadastre.spatial_unit_in_group sig, "
                    + "             administrative.ba_unit_contains_spatial_unit baco "
                    + "      WHERE  sig.spatial_unit_id = baco.spatial_unit_id "
                    + "      AND    baco.ba_unit_id IN (";

            // Build the IN clause with parameter values rather than hard coded ids to 
            // ensure the generated SQL can be treated as a prepared statement. 
            for (int i = 0; i < baUnitIdListCount; i++) {
                whereClause = whereClause + "#{" + PARAM_ID_LIST + i + "}, ";
            }

            whereClause = whereClause.substring(0, whereClause.length() - 2) + "))";
            if (serviceId != null) {
                OR();
            }
            WHERE(whereClause);

            // Check if any of the BA Units are linked to a Common Property of a Unit Development 
            // through their prior title reference (i.e. where the BA Unit is the prior title for
            // the Common Property
            whereClause = "sg.id IN (SELECT sig.spatial_unit_group_id "
                    + "      FROM   cadastre.spatial_unit_in_group sig, "
                    + "             administrative.required_relationship_baunit rel,"
                    + "             administrative.ba_unit_contains_spatial_unit baco,"
                    + "             cadastre.cadastre_object co "
                    + "      WHERE  sig.spatial_unit_id = co.id"
                    + "      AND    co.type_code = 'commonProperty' "
                    + "      AND    baco.spatial_unit_id = co.id "
                    + "      AND    rel.to_ba_unit_id = baco.ba_unit_id "
                    + "      AND    rel.relation_code = 'priorTitle' "
                    + "      AND    rel.from_ba_unit_id IN (";

            for (int i = 0; i < baUnitIdListCount; i++) {
                whereClause = whereClause + "#{" + PARAM_ID_LIST + i + "}, ";
            }

            whereClause = whereClause.substring(0, whereClause.length() - 2) + "))";
            OR();
            WHERE(whereClause);
        }

        sql = SQL();
        return sql;
    }

    public static String buildGetStrataPropsSql(String unitParcelGroupName, int baUnitIdListCount) {
        String sql;
        BEGIN();
        SELECT("ba.id AS ba_id");
        SELECT("ba.type_code AS ba_type_code");
        SELECT("ba.name AS ba_name");
        SELECT("ba.name_firstpart AS ba_name_firstpart");
        SELECT("ba.name_lastpart AS ba_name_lastpart");
        SELECT("ba.status_code AS ba_status_code");
        SELECT("ba.transaction_id AS ba_transaction_id");
        SELECT("ba.creation_date AS ba_registration_date");
        SELECT("   (SELECT CAST(SUM(size) AS integer) "
                + " FROM   administrative.ba_unit_area "
                + " WHERE  ba.id = ba_unit_id"
                + " AND    type_code = 'officialArea' ) AS official_area");
        SELECT("CAST(COALESCE(rrr_curr.share, rrr_pend.share) AS integer) AS unit_entitlement");
        SELECT("co.type_code AS unit_parcel_type");
        SELECT(" (CASE co.type_code WHEN 'commonProperty' THEN '2' "
                + " WHEN 'principalUnit' THEN '3'  || lpad(regexp_replace(ba.name_firstpart, '\\D*',  ''), 5, '0') "
                + " ELSE '1'  || ba.name END) AS sort_key "); 
        FROM("administrative.ba_unit ba");
        LEFT_OUTER_JOIN("administrative.rrr rrr_curr "
                + " ON ba.id = rrr_curr.ba_unit_id AND rrr_curr.type_code = 'unitEntitlement' "
                + " AND rrr_curr.status_code = 'current'");
        LEFT_OUTER_JOIN("administrative.rrr rrr_pend "
                + " ON ba.id = rrr_pend.ba_unit_id AND rrr_pend.type_code = 'unitEntitlement' "
                + " AND rrr_pend.status_code = 'pending'");
        LEFT_OUTER_JOIN("administrative.ba_unit_contains_spatial_unit basp "
                + " ON ba.id = basp.ba_unit_id "
                + " INNER JOIN cadastre.cadastre_object co "
                + "   ON basp.spatial_unit_id = co.id AND co.type_code != 'accessoryUnit'");
        if (unitParcelGroupName != null) {
            // Get all ba units linked to the spatial unit group as well as all BA Units
            // linked to the Common Property as Prior titles or principal units
            WHERE("ba.id IN (SELECT baco.ba_unit_id"
                    + "      FROM   cadastre.spatial_unit_group sg, "
                    + "             cadastre.spatial_unit_in_group sig, "
                    + "             administrative.ba_unit_contains_spatial_unit baco "
                    + "      WHERE  sg.name = #{" + PARAM_UNIT_PARCEL_GROUP_NAME + "} "
                    + "      AND    sig.spatial_unit_group_id = sg.id "
                    + "      AND    baco.spatial_unit_id = sig.spatial_unit_id "
                    + "      UNION "
                    + "      SELECT rel.from_ba_unit_id"
                    + "      FROM   cadastre.spatial_unit_group sg, "
                    + "             cadastre.spatial_unit_in_group sig, "
                    + "             cadastre.cadastre_object co, "
                    + "             administrative.ba_unit_contains_spatial_unit baco, "
                    + "             administrative.required_relationship_baunit rel"
                    + "      WHERE  sg.name = #{" + PARAM_UNIT_PARCEL_GROUP_NAME + "} "
                    + "      AND    sig.spatial_unit_group_id = sg.id "
                    + "      AND    co.id = sig.spatial_unit_id "
                    + "      AND    co.type_code = 'commonProperty' "
                    + "      AND    baco.spatial_unit_id = co.id "
                    + "      AND    rel.to_ba_unit_id = baco.ba_unit_id"
                    + "      AND    rel.relation_code = 'priorTitle' "
                    + "      UNION "
                    + "      SELECT rel.to_ba_unit_id"
                    + "      FROM   cadastre.spatial_unit_group sg, "
                    + "             cadastre.spatial_unit_in_group sig, "
                    + "             cadastre.cadastre_object co, "
                    + "             administrative.ba_unit_contains_spatial_unit baco, "
                    + "             administrative.required_relationship_baunit rel"
                    + "      WHERE  sg.name = #{" + PARAM_UNIT_PARCEL_GROUP_NAME + "} "
                    + "      AND    sig.spatial_unit_group_id = sg.id "
                    + "      AND    co.id = sig.spatial_unit_id "
                    + "      AND    co.type_code = 'commonProperty' "
                    + "      AND    baco.spatial_unit_id = co.id "
                    + "      AND    rel.from_ba_unit_id = baco.ba_unit_id"
                    + "      AND    rel.relation_code = 'commonProperty') ");
        }

        if (baUnitIdListCount > 0) {
            // Add the BA Units from the BA Unit Id List
            String whereClause = "ba.id IN (";

            // Build the IN clause with parameter values rather than hard coded ids to 
            // ensure the generated SQL can be treated as a prepared statement. 
            for (int i = 0; i < baUnitIdListCount; i++) {
                whereClause = whereClause + "#{" + PARAM_ID_LIST + i + "}, ";
            }

            whereClause = whereClause.substring(0, whereClause.length() - 2) + ")";
            if (unitParcelGroupName != null) {
                OR();
            }
            WHERE(whereClause);
        }
        ORDER_BY("sort_key"); 

        sql = SQL();
        return sql;
    }
}
