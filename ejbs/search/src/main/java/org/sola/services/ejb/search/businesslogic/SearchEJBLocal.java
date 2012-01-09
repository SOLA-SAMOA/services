/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.search.repository.entities.ApplicationLogResult;
import org.sola.services.ejb.search.repository.entities.ApplicationSearchParams;
import org.sola.services.ejb.search.repository.entities.ApplicationSearchResult;
import org.sola.services.ejb.search.repository.entities.BaUnitSearchParams;
import org.sola.services.ejb.search.repository.entities.BaUnitSearchResult;
import org.sola.services.ejb.search.repository.entities.BrSearchParams;
import org.sola.services.ejb.search.repository.entities.BrSearchResult;
import org.sola.services.ejb.search.repository.entities.CadastreObjectSearchResult;
import org.sola.services.ejb.search.repository.entities.ConfigMapLayer;
import org.sola.services.ejb.search.repository.entities.DynamicQuery;
import org.sola.services.ejb.search.repository.entities.GenericResult;
import org.sola.services.ejb.search.repository.entities.PartySearchParams;
import org.sola.services.ejb.search.repository.entities.PartySearchResult;
import org.sola.services.ejb.search.repository.entities.PropertyVerifier;
import org.sola.services.ejb.search.repository.entities.SourceSearchParams;
import org.sola.services.ejb.search.repository.entities.SourceSearchResult;
import org.sola.services.ejb.search.repository.entities.UserSearchParams;
import org.sola.services.ejb.search.repository.entities.UserSearchResult;
import org.sola.services.ejb.search.spatial.QueryForNavigation;
import org.sola.services.ejb.search.spatial.QueryForSelect;
import org.sola.services.ejb.search.spatial.ResultForNavigationInfo;
import org.sola.services.ejb.search.spatial.ResultForSelectionInfo;

/**
 *
 * @author manoku
 */
@Local
public interface SearchEJBLocal extends AbstractEJBLocal {

    PropertyVerifier getPropertyVerifier(String firstPart, String lastPart) throws Exception;

    GenericResult getGenericResultList(String queryName, Map params);

    List<ApplicationSearchResult> searchApplications(ApplicationSearchParams params) throws Exception;

    List<ApplicationSearchResult> getUnassignedApplications(String locale) throws Exception;

    List<ApplicationSearchResult> getAssignedApplications(String locale) throws Exception;

    ResultForNavigationInfo getSpatialResult(QueryForNavigation spatialQuery) throws Exception;

    List<ConfigMapLayer> getConfigMapLayerList(String languageCode) throws Exception;

    List<ResultForSelectionInfo> getSpatialResultFromSelection(
            List<QueryForSelect> queriesForSelection) throws Exception;

    HashMap<String, String> getMapSettingList() throws Exception;
    
    HashMap getResultObjectFromStatement(String sqlStatement, Map params);

    List<PartySearchResult> searchParties(PartySearchParams searchParams) throws Exception;

    List<SourceSearchResult> searchSources(SourceSearchParams searchParams) throws Exception;

    List<UserSearchResult> getActiveUsers() throws Exception;

    List<UserSearchResult> searchUsers(UserSearchParams searchParams) throws Exception;

    List<ApplicationLogResult> getApplicationLog(String applicationId);    
    
    List<BrSearchResult> searchBr(BrSearchParams searchParams, String lang);
    
    List<BaUnitSearchResult> searchBaUnits(BaUnitSearchParams searchParams);
    
    List<DynamicQuery> getQueryListAll();
    
    List<CadastreObjectSearchResult> searchCadastreObjects(String searchBy, String searchString);
}
