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
package org.sola.services.ejb.search.businesslogic;

import org.junit.Ignore;
import java.util.GregorianCalendar;
import java.util.Date;
import org.sola.services.ejb.search.repository.SourceSearchResult;
import org.sola.services.ejb.search.repository.SourceSearchParams;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.bind.DatatypeConverter;
import java.util.List;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.search.repository.ConfigMapLayer;
import org.sola.services.ejb.search.repository.GenericResult;
import org.sola.services.ejb.search.repository.PartySearchParams;
import org.sola.services.ejb.search.repository.PartySearchResult;
import org.sola.services.ejb.search.repository.TestSpatial;
import org.sola.services.ejb.search.repository.PropertyVerifier;
import org.sola.services.ejb.search.repository.UserSearchParams;
import org.sola.services.ejb.search.repository.UserSearchResult;
import org.sola.services.ejb.search.spatial.QueryForNavigation;
import org.sola.services.ejb.search.spatial.ResultForNavigationInfo;
import static org.junit.Assert.*;

/**
 *
 * @author manoku
 */
public class SearchEJBIT extends AbstractEJBTest {

    public SearchEJBIT() {
        super();
    }

    /** Test searching active users */
    @Test
    public void testActiveUserSearch() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        try {
            SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
            List<UserSearchResult> result = instance.getActiveUsers();

            assertNotNull(result);

            if (result != null && result.size() > 0) {
                System.out.println("Found " + result.size() + " active users");
            } else {
                System.out.println("Can't find any active user");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /** Test advanced searching of users */
    @Test
    public void testAdavncedUserSearch() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        try {
            SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
            UserSearchParams params =new UserSearchParams();
            params.setUserName("test");
            
            List<UserSearchResult> result = instance.searchUsers(params);

            assertNotNull(result);

            if (result != null && result.size() > 0) {
                System.out.println("Found " + result.size() + " users");
            } else {
                System.out.println("Can't find any user");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    
    /** Test source search. */
    @Test
    public void testSourceSearch() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        try {
            SourceSearchParams params = new SourceSearchParams();
            Date dateTo = new GregorianCalendar(2500, 1, 1).getTime();
            Date dateFrom = new GregorianCalendar(1, 1, 1).getTime();
            params.setFromRecordationDate(dateFrom);
            params.setFromSubmissionDate(dateFrom);
            params.setToRecordationDate(dateTo);
            params.setToSubmissionDate(dateTo);
            params.setLocale("it");
            params.setLaNumber("");
            params.setRefNumber("");
            params.setTypeCode("");

            SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
            List<SourceSearchResult> result = instance.searchSources(params);

            assertNotNull(result);

            if (result != null && result.size() > 0) {
                System.out.println("Found " + result.size() + " sources");
            } else {
                System.out.println("Can't find any source");
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Test party search
     */
    //@Ignore
    @Test
    public void testPartySearch() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        PartySearchParams params = new PartySearchParams();
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        List<PartySearchResult> result = instance.searchParties(params);
        assertNotNull(result);

        if (result != null && result.size() > 0) {
            System.out.println("Found " + result.size() + " parties");
        } else {
            System.out.println("Can't find any parties");
        }
    }

    /**
     * Test of GetPropertyVerifier method of class SearchEJB.
     */
    @Test
    public void testGetPropertyVerifier() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getPropertyVerifier-With parameters");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        String firstPart = "602";
        String lastPart = "6629";
        PropertyVerifier result = instance.getPropertyVerifier(firstPart, lastPart);
        if (result != null) {
            System.out.println("ba unit found: " + result.toString());
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of GetPropertyVerifier method of class SearchEJB.
     */
    @Test
    public void testGetPropertyVerifierNullParameters() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getPropertyVerifier-With null parameters");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        String firstPart = null;
        String lastPart = "6629";
        PropertyVerifier result = instance.getPropertyVerifier(firstPart, lastPart);
        if (result != null) {
            System.out.println("ba unit found: " + result.toString());
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of GetPropertyVerifier method of class SearchEJB.
     */
    @Test
    public void testGetSpatialTest() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getSpatialTest");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        TestSpatial result = instance.getResultEntity("TestSpatial.get2", null);
        if (result != null) {
            System.out.println("spatial test found: " + result.toString());
            System.out.println("geometry: " + DatatypeConverter.printHexBinary(result.getTheGeom()));
            try {
                WKBReader wkbReader = new WKBReader();
                Geometry geom = wkbReader.read(result.getTheGeom());
                System.out.println("geometry found:" + geom.toString());
            } catch (Exception ex) {
                System.out.println("Failed to transform geometry");
                System.out.println("Error:" + ex.getMessage());
            }
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of getSpatialResult method of class SearchEJB.
     */
    @Test
    public void testGetSpatialResult() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("Testing spatial result queries");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        QueryForNavigation spatialQuery = new QueryForNavigation();
        int srid = 2193;
        double east = 1795771, west = 1776400, north = 5932259, south = 5919888;
        spatialQuery.setWest(west);
        spatialQuery.setSouth(south);
        spatialQuery.setEast(east);
        spatialQuery.setNorth(north);
        spatialQuery.setSrid(srid);
        this.testSpatialQuery(instance, spatialQuery, "SpatialResult.getParcels");
        this.testSpatialQuery(instance, spatialQuery, "SpatialResult.getSurveyControls");
        this.testSpatialQuery(instance, spatialQuery, "SpatialResult.getRoads");
        this.testSpatialQuery(instance, spatialQuery, "SpatialResult.getApplications");
        this.testSpatialQuery(instance, spatialQuery, "SpatialResult.getPlaceNames");
    }

    private void testSpatialQuery(SearchEJBLocal instance,
            QueryForNavigation spatialQuery, String queryName) throws Exception {
        System.out.println("Testing query: " + queryName);
        spatialQuery.setQueryName(queryName);
        ResultForNavigationInfo result = instance.getSpatialResult(spatialQuery);
        if (result != null) {
            System.out.println("Result has found: " + result.getToAdd().size());
            if (result.getToAdd().size() > 0) {
                System.out.println("First result: " + result.getToAdd().get(0).toString());
            }
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of getSpatialResult method of class SearchEJB.
     */
    @Test
    public void testConfigMapLayer() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getConfigMapLayer - getting configuration information");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        List<ConfigMapLayer> result = instance.getConfigMapLayerList();
        if (result != null) {
            System.out.println("Result has found: " + result.size());
            if (result.size() > 0) {
                System.out.println("First result: " + result.get(0).toString());
            }
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of getSettings method of class SearchEJB.
     */
    @Test
    public void testGetSettings() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getSettings - getting configuration information");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        HashMap<String, String> result = instance.getSettingList("Setting.getForMap");
        if (result != null) {
            System.out.println("Result has found: " + result.size());
            if (result.size() > 0) {
                System.out.println("First result: " + result.get("map-srid"));
            }
        } else {
            System.out.println("Result: nothing returned");
        }
    }

    /**
     * Test of getting result from a dynamic query.
     */
    @Test
    public void testGetDynamicQueryResult() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("Testing dynamic query");
        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SearchEJB.class.getSimpleName());
        System.out.println("Queryname: dynamic.test");
        Object objValue = instance.getResultObject("dynamic.test", null);
        System.out.println("Returned value:" + objValue);

        String sqlStatement = "select 1=?1 as test_field";
        System.out.println("Get result from statement: " + sqlStatement);
        Object objValue2 = instance.getResultObjectFromStatement(sqlStatement, new Object[]{1});
        System.out.println("Returned value:" + objValue2);

        sqlStatement = "select count(*) from application.request_type where code in (?1)";
        System.out.println("Get result from statement: " + sqlStatement);
        List listParameter = new ArrayList();
        listParameter.add("cadastreChange");
        listParameter.add("documentCopy");

        objValue2 = instance.getResultObjectFromStatement(
                sqlStatement, new Object[]{listParameter});
        System.out.println("Returned value:" + objValue2);

        System.out.println("Getting map definition...");
        HashMap<String, String> settings = instance.getSettingList("Setting.getForMap");
        Object[] params = new Object[2];
        params[0] = this.getGeometry(
                String.format("POLYGON ((%s %s, %s %s, %s %s, %s %s))",
                settings.get("map-west"), settings.get("map-south"),
                settings.get("map-east"), settings.get("map-south"),
                settings.get("map-east"), settings.get("map-north"),
                settings.get("map-west"), settings.get("map-south")));
        params[1] = Integer.parseInt(settings.get("map-srid"));
        this.testDynamicQuery(instance, "dynamic.informationtool.get_parcel", params);
        this.testDynamicQuery(instance, "dynamic.informationtool.get_place_name", params);
        this.testDynamicQuery(instance, "dynamic.informationtool.get_road", params);
        this.testDynamicQuery(instance, "dynamic.informationtool.get_application", params);
        this.testDynamicQuery(instance, "dynamic.informationtool.get_survey_control", params);
    }

    private void testDynamicQuery(SearchEJBLocal instance,
            String queryName, Object[] params) throws Exception {
        System.out.println("Testing query: " + queryName);
        GenericResult result = instance.getGenericResultList(queryName, params);
        if (result != null) {
            System.out.println("Result has found: " + result.getValues().size());
            if (result.getValues().size() > 0) {
                System.out.println("First result: " + result.getValues().get(0).toString());
            }
        } else {
            System.out.println("Result: nothing returned");
        }
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
        this.testQueriesForResultList(instance, "CadastreObjectWithGeometry.searchByBaUnitId",
                new Object[]{"3068323"});
    }

    private void testQueriesForResultList(
            SearchEJBLocal instance, String queryName, Object[] params) throws Exception {
        System.out.println("Testing query: " + queryName);
        List result =
                instance.getResultList(queryName, params);
        if (result != null && result.size() > 0) {
            System.out.println("Found " + result.size() + " elements.");
        } else {
            System.out.println("Can't find any element.");
        }
    }
}
