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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.entities.AbstractResultEntity;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.search.repository.ApplicationLogResult;
import org.sola.services.ejb.search.repository.ApplicationSearchParams;
import org.sola.services.ejb.search.repository.ApplicationSearchResult;
import org.sola.services.ejb.search.repository.ConfigMapLayer;
import org.sola.services.ejb.search.repository.GenericResult;
import org.sola.services.ejb.search.repository.PartySearchParams;
import org.sola.services.ejb.search.repository.PartySearchResult;
import org.sola.services.ejb.search.repository.PropertyVerifier;
import org.sola.services.ejb.search.repository.SearchSqlProvider;
import org.sola.services.ejb.search.repository.Setting;
import org.sola.services.ejb.search.repository.SourceSearchParams;
import org.sola.services.ejb.search.repository.SourceSearchResult;
import org.sola.services.ejb.search.repository.SpatialResult;
import org.sola.services.ejb.search.repository.UserSearchParams;
import org.sola.services.ejb.search.repository.UserSearchResult;
import org.sola.services.ejb.search.spatial.QueryForNavigation;
import org.sola.services.ejb.search.spatial.QueryForSelect;
import org.sola.services.ejb.search.spatial.ResultForNavigationInfo;
import org.sola.services.ejb.search.spatial.ResultForSelectionInfo;

@Stateless
@EJB(name = "java:global/SOLA/SearchEJBLocal", beanInterface = SearchEJBLocal.class)
public class SearchEJB extends AbstractEJB implements SearchEJBLocal {

    @PersistenceContext(unitName = "sola.search.pu")
    private EntityManager em;
    private Properties dynamicQueriesResource;
    private static String DYNAMIC_QUERY_LOCATION_RESOURCE = "/props/queries.properties";
    private static String DYNAMIC_QUERY_NAME_PREFIX = "dynamic.";

    private Properties getDynamicQueriesResource() throws Exception {
        if (dynamicQueriesResource == null) {
            InputStream queriesResource = this.getClass().getResourceAsStream(
                    DYNAMIC_QUERY_LOCATION_RESOURCE);
            if (queriesResource == null) {
                throw new Exception("Dynamic query file is not found. File expected :"
                        + DYNAMIC_QUERY_LOCATION_RESOURCE);
            }
            dynamicQueriesResource = new Properties();
            dynamicQueriesResource.load(queriesResource);
        }
        return dynamicQueriesResource;
    }

    private String getDynamicQueryStatement(String queryName) throws Exception {
        return this.getDynamicQueriesResource().getProperty(queryName);
    }

    private String[] getDynamicQueryFieldNames(String forQueryName) throws Exception {
        System.out.println("getDynamicQueryFieldNames, queryName:" + forQueryName);
        String fieldsAsCommaSeperatedString = this.getDynamicQueriesResource().getProperty(
                String.format("%s.fields", forQueryName));
        if (fieldsAsCommaSeperatedString == null || fieldsAsCommaSeperatedString.isEmpty()) {
            throw new Exception(
                    String.format("Fields for query {0} are not found.", forQueryName));
        }
        return fieldsAsCommaSeperatedString.split(",");
    }

    private Query prepareQuery(String queryName, Object[] params) throws Exception {
        Query query;
        if (queryName.startsWith(DYNAMIC_QUERY_NAME_PREFIX)) {
            String sqlStatement = getDynamicQueryStatement(queryName);
            query = (Query) em.createNativeQuery(sqlStatement);
        } else {
            query = (Query) em.createNamedQuery(queryName);
        }
        if (query == null) {
            throw new Exception(String.format(
                    "searchejb.preparequery.querynotfound. queryname: %s. ", queryName, params));
        }

        this.setParams(query, params);
        return query;
    }

    private Query prepareQueryStatement(String sqlStatement, Object[] params) throws Exception {
        Query query;
        query = (Query) em.createNativeQuery(sqlStatement);
        this.setParams(query, params);
        return query;
    }

    private void setParams(Query query, Object[] params) {
        if (params != null) {
            for (int paramIndex = 0; paramIndex < params.length; paramIndex++) {
                if (params[paramIndex] == null) {
                    params[paramIndex] = "Null";
                }
                query.setParameter(paramIndex + 1, params[paramIndex]);
            }
        }
    }

    @Override
    public <T extends AbstractResultEntity> List<T> getResultList(String queryName, Object[] params)
            throws Exception {
        List<T> result = null;

        Query query = this.prepareQuery(queryName, params);
        result = (List<T>) query.getResultList();

        return result;
    }

    @Override
    public GenericResult getGenericResultList(
            String queryName, Object[] params)
            throws Exception {
        String[] fieldNames = this.getDynamicQueryFieldNames(queryName);
        GenericResult result = new GenericResult();
        result.setFieldNames(fieldNames);

        Query query = this.prepareQuery(queryName, params);
        List<Object[]> rawResult = query.getResultList();
        for (Object[] row : rawResult) {
            result.addRow(row);
        }
        return result;
    }

    @Override
    public <T extends AbstractResultEntity> T getResultEntity(String queryName, Object[] params)
            throws Exception {
        T result = (T) this.getResultObject(queryName, params);
        return result;
    }

    @Override
    public Object getResultObject(String queryName, Object[] params) throws Exception {
        Query query = this.prepareQuery(queryName, params);
        return this.getResultObject(query);
    }

    @Override
    public Object getResultObjectFromStatement(String sqlStatement, Object[] params)
            throws Exception {
        Query query = this.prepareQueryStatement(sqlStatement, params);
        return this.getResultObject(query);
    }

    private Object getResultObject(Query query) throws Exception {
        Object result = null;
        try {
            result = query.getSingleResult();
        } catch (NoResultException noResultException) {
            //It means no result is returned
        } 
        // This throw hides the real exception.
//        catch (Exception ex) {
//            throw new Exception("searchejb.getresultobject.somethingwentwrong.", ex);
//        }
        return result;
    }

    @Override
    public PropertyVerifier getPropertyVerifier(String firstPart, String lastPart) throws Exception {
        return getResultEntity("PropertyVerifier.verify", new Object[]{firstPart, lastPart});
    }

    @Override
    public List<ApplicationSearchResult> searchApplications(ApplicationSearchParams params) throws Exception {
        // Process params

        // added Paola
        if (params.getContactPerson() == null || params.getContactPerson().equals("")) {
            params.setContactPerson("%");
        } else {
            params.setContactPerson(params.getContactPerson() + "%");
        }
        //end added paola

        if (params.getAgent() == null || params.getAgent().equals("")) {
            params.setAgent("%");
        } else {
            params.setAgent(params.getAgent() + "%");
        }

        if (params.getNr() == null || params.getNr().equals("")) {
            params.setNr("%");
        } else {
            params.setNr(params.getNr() + "%");
        }

        if (params.getFromDate() == null) {
            params.setFromDate(new GregorianCalendar(1, 1, 1).getTime());
        }

        if (params.getToDate() == null) {
            params.setToDate(new GregorianCalendar(2500, 1, 1).getTime());
        }

        params.setAgent(params.getAgent().trim());
        params.setNr(params.getNr().trim());
        params.setContactPerson(params.getContactPerson().trim());

        return getResultList("ApplicationSummary.searchApplications",
                new Object[]{params.getLocale(), params.getFromDate(), params.getToDate(),
                    params.getNr(), params.getAgent(), params.getContactPerson()});
    }

    @Override
    public List<SourceSearchResult> searchSources(SourceSearchParams searchParams) throws Exception {
        Date dateTo = new GregorianCalendar(2500, 1, 1).getTime();
        Date dateFrom = new GregorianCalendar(1, 1, 1).getTime();
        
        if(searchParams.getFromRecordationDate()==null){
            searchParams.setFromRecordationDate(dateFrom);
        }
        
        if(searchParams.getToRecordationDate()==null){
            searchParams.setToRecordationDate(dateTo);
        }
        
        if(searchParams.getFromSubmissionDate()==null){
            searchParams.setFromSubmissionDate(dateFrom);
        }
        
        if(searchParams.getToSubmissionDate()==null){
            searchParams.setToSubmissionDate(dateTo);
        }
        
        if(searchParams.getTypeCode()==null){
            searchParams.setTypeCode("");
        }
        
        if(searchParams.getRefNumber()==null){
            searchParams.setRefNumber("");
        }
        
        if(searchParams.getLaNumber()==null){
            searchParams.setLaNumber("");
        }
        
        if(searchParams.getLocale()==null || searchParams.getLocale().length()<1){
            searchParams.setLaNumber("en");
        }
        
        Query query = em.createNamedQuery("SourceSummary.searchSources");
        query.setParameter("locale", searchParams.getLocale());
        query.setParameter("laNumber", searchParams.getLaNumber());
        query.setParameter("typeCode", searchParams.getTypeCode());
        query.setParameter("refNumber", searchParams.getRefNumber());
        query.setParameter("fromRecordationDate", searchParams.getFromRecordationDate(), TemporalType.DATE);
        query.setParameter("toRecordationDate", searchParams.getToRecordationDate(), TemporalType.DATE);
        query.setParameter("fromSubmissionDate", searchParams.getFromSubmissionDate(), TemporalType.DATE);
        query.setParameter("toSubmissionDate", searchParams.getToSubmissionDate(), TemporalType.DATE);
        return (List<SourceSearchResult>)query.getResultList();
    }
    
    // TODO: Annotate only for admin usage
    @Override
    public List<UserSearchResult> searchUsers(UserSearchParams searchParams) throws Exception {
        if(searchParams.getGroupId()==null){
            searchParams.setGroupId("");
        }
        
        if(searchParams.getUserName()==null){
            searchParams.setUserName("");
        }
        
        if(searchParams.getFirstName()==null){
            searchParams.setFirstName("");
        }
        
        if(searchParams.getLastName()==null){
            searchParams.setLastName("");
        }
        
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_QUERY, UserSearchResult.QUERY_ADVANCED_USER_SEARCH);
        params.put("userName", searchParams.getUserName());
        params.put("firstName", searchParams.getFirstName());
        params.put("lastName", searchParams.getLastName());
        params.put("groupId", searchParams.getGroupId());
        return getRepository().getEntityList(UserSearchResult.class, params);
    }
    
    @Override
    public List<ApplicationSearchResult> getUnassignedApplications(String locale) throws Exception {
        return getResultList("ApplicationSummary.getUnassigned", new Object[]{locale});
    }

    @Override
    public List<ApplicationSearchResult> getAssignedApplications(String locale) throws Exception {
        return getResultList("ApplicationSummary.getAssigned", new Object[]{locale});
    }

    @Override
    public List<UserSearchResult> getActiveUsers() throws Exception {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_QUERY, UserSearchResult.QUERY_ACTIVE_USERS);
        return getRepository().getEntityList(UserSearchResult.class, params);
    }
    
    @Override
    public List<PartySearchResult> searchParties(PartySearchParams searchParams) throws Exception {
        if(searchParams.getName()==null){
            searchParams.setName("");
        }
        if(searchParams.getTypeCode()==null){
            searchParams.setTypeCode("%");
        }
        if(searchParams.getRoleTypeCode()==null){
            searchParams.setRoleTypeCode("%");
        }
        
        searchParams.setName(searchParams.getName().trim());
        searchParams.setTypeCode(searchParams.getTypeCode().trim());
        searchParams.setRoleTypeCode(searchParams.getRoleTypeCode().trim());
        
        return getResultList("PartySummary.searchParties", new Object[]{
            searchParams.getName(), searchParams.getTypeCode(), searchParams.getRoleTypeCode()});
    }

    @Override
    public ResultForNavigationInfo getSpatialResult(
            QueryForNavigation spatialQuery) throws Exception {
        //Object[] params = new Object[6];
        Object[] params = new Object[5];
        params[0] = spatialQuery.getWest();
        params[1] = spatialQuery.getSouth();
        params[2] = spatialQuery.getEast();
        params[3] = spatialQuery.getNorth();
        params[4] = spatialQuery.getSrid();
        //params[5] = spatialQuery.getPixelResolution();
        ResultForNavigationInfo spatialResultInfo = new ResultForNavigationInfo();
        List<SpatialResult> result = this.getResultList(spatialQuery.getQueryName(), params);
        spatialResultInfo.setToAdd(result);
        return spatialResultInfo;
    }

    @Override
    public List<ConfigMapLayer> getConfigMapLayerList() throws Exception {
        return this.getResultList("ConfigMapLayer.get", null);
    }

    @Override
    public List<ResultForSelectionInfo> getSpatialResultFromSelection(
            List<QueryForSelect> queriesForSelection) throws Exception {
        List<ResultForSelectionInfo> results = new ArrayList<ResultForSelectionInfo>();
        for (QueryForSelect queryInfo : queriesForSelection) {
            Object[] params = new Object[2];
            params[0] = queryInfo.getFilteringGeometry();
            params[1] = queryInfo.getSrid();
            ResultForSelectionInfo resultInfo = new ResultForSelectionInfo();
            resultInfo.setId(queryInfo.getId());
            resultInfo.setResult(this.getGenericResultList(queryInfo.getQueryName(), params));
            results.add(resultInfo);
        }
        return results;
    }

    @Override
    public HashMap<String, String> getSettingList(String queryName) throws Exception {
        HashMap settingMap = new HashMap();
        List<Setting> settings = this.getResultList(queryName, null);
        for (Setting setting : settings) {
            settingMap.put(setting.getId(), setting.getVl());
        }
        return settingMap;
    }
    
    @Override
    public List<ApplicationLogResult> getApplicationLog(String applicationId) { 
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_QUERY, SearchSqlProvider.buildApplicationLogSql());
        params.put(SearchSqlProvider.PARAM_APPLICATION_ID, applicationId);
        return getRepository().getEntityList(ApplicationLogResult.class, params);
        
    }
}
