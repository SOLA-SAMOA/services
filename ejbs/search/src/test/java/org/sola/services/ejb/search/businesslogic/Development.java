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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.search.businesslogic;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.util.HashMap;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.search.repository.SearchSqlProvider;
import org.sola.services.ejb.search.repository.entities.GenericResult;
import org.sola.services.ejb.search.spatial.ResultForSelectionInfo;

/**
 *
 * @author manoku
 */
public class Development extends AbstractEJBTest {

    public Development() {
        super();
    }

    private byte[] getGeometry(String wktGeometry) throws Exception {
        WKTReader wktReader = new WKTReader();
        Geometry geom = wktReader.read(wktGeometry);
        WKBWriter wkbWriter = new WKBWriter();
        return wkbWriter.write(geom);
    }

    @Test
    @Ignore
    public void testSearchOthers() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("Testing other queries that return lists of entities");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        //this.testQueriesForResultList(instance, "CadastreObjectWithGeometry.searchByBaUnitId",
        //        new Object[]{"3068323"});
        List result =  instance.searchSpatialObjects(
                "map_search.cadastre_object_by_baunit_owner", "fiku");
        System.out.println("Found:" + result.size());
    }

    @Ignore
    @Test
    public void test() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("Test information tool queries");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        String[] queriesForGenericResult = {
            "dynamic.informationtool.get_parcel",
            "dynamic.informationtool.get_parcel_pending",
            "dynamic.informationtool.get_place_name",
            "dynamic.informationtool.get_road",
            "dynamic.informationtool.get_application",
            "dynamic.informationtool.get_survey_control"};
        HashMap<String, String> mapSettings = instance.getMapSettingList();
        int srid = Integer.parseInt(mapSettings.get("map-srid"));
        double west = Double.parseDouble(mapSettings.get("map-west"));
        double south = Double.parseDouble(mapSettings.get("map-south"));
        double east = Double.parseDouble(mapSettings.get("map-east"));
        double north = Double.parseDouble(mapSettings.get("map-north"));
        double centerX = west + (east - west) / 2;
        double centerY = south + (north - south) / 2;

        byte[] filteringGeometry = this.getGeometry(String.format("POINT(%s %s)", centerX, centerY));
        HashMap params = new HashMap();
        params.put(ResultForSelectionInfo.PARAM_GEOMETRY, filteringGeometry);
        params.put(ResultForSelectionInfo.PARAM_SRID, srid);
        params.put(CommonSqlProvider.PARAM_LANGUAGE_CODE, "en");
        for (String queryName : queriesForGenericResult) {
            System.out.print("Testing query:" + queryName);
            try {
                GenericResult result = instance.getGenericResultList(queryName, params);
                System.out.println("Success.");
            } catch (Exception ex) {
                System.out.println("Failed. Reason:");
                ex.printStackTrace();
            }

        }
    }

    private void testQueriesForResultList(
            SearchEJBLocal instance, String queryName, Object[] params) throws Exception {
        System.out.println("Testing query: " + queryName);
//        List result =
//                instance.getResultList(queryName, params);
//        if (result != null && result.size() > 0) {
//            System.out.println("Found " + result.size() + " elements.");
//        } else {
//            System.out.println("Can't find any element.");
//        }
    }

    @Test
    @Ignore
    public void testApplicationLogSql() throws Exception {
        System.out.println(SearchSqlProvider.buildGetUnitDevNrSql(" ", 1));
    }
}
