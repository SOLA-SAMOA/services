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
package org.sola.services.ejb.cadastre.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectNode;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectStatusChanger;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectType; // NOTE namespace change
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectTarget;
import org.sola.services.ejb.cadastre.repository.entities.SurveyPoint;

/**
 * Implementation of {
 * <p/>
 * @link CadastreEJBLocal} interface.
 */
@Stateless
@EJB(name = "java:global/SOLA/CadastreEJBLocal", beanInterface = CadastreEJBLocal.class)
public class CadastreEJB extends AbstractEJB implements CadastreEJBLocal {

    @Override
    public List<CadastreObjectType> getCadastreObjectTypes(String languageCode) {
        return getRepository().getCodeList(CadastreObjectType.class, languageCode);
    }

    @Override
    public CadastreObject getCadastreObject(String id) {
        return getRepository().getEntity(CadastreObject.class, id);
    }

    @Override
    public List<CadastreObject> getCadastreObjects(List<String> cadastreObjIds) {
        return getRepository().getEntityListByIds(CadastreObject.class, cadastreObjIds);
    }

    @Override
    public List<CadastreObject> getCadastreObjectByParts(String searchString) {
        Integer numberOfMaxRecordsReturned = 10;
        HashMap params = new HashMap();
        params.put("search_string", searchString);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, numberOfMaxRecordsReturned);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYPARTS, params);
    }

    @Override
    public CadastreObject getCadastreObjectByPoint(double x, double y, int srid) {
        HashMap params = new HashMap();
        params.put("x", x);
        params.put("y", y);
        params.put("srid", srid);
        return getRepository().getEntity(
                CadastreObject.class, CadastreObject.QUERY_WHERE_SEARCHBYPOINT, params);
    }

    @Override
    public CadastreObject saveCadastreObject(CadastreObject cadastreObject) {
        return getRepository().saveEntity(cadastreObject);
    }

    @Override
    public List<CadastreObject> getCadastreObjectsByBaUnit(String baUnitId) {
        HashMap params = new HashMap();
        params.put("ba_unit_id", baUnitId);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYBAUNIT, params);
    }

    @Override
    public List<CadastreObject> getCadastreObjectsByService(String serviceId) {
        HashMap params = new HashMap();
        params.put("service_id", serviceId);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYSERVICE, params);
    }

    /**
     * 
     * @param transactionId
     * @param statusCode 
     */
    @Override
    public void ChangeStatusOfCadastreObjects(
            String transactionId, String filter, String statusCode) {
        HashMap params = new HashMap();
        params.put("transaction_id", transactionId);
        List<CadastreObjectStatusChanger> involvedCoList =
                getRepository().getEntityList(CadastreObjectStatusChanger.class, filter, params);
        for (CadastreObjectStatusChanger involvedCo : involvedCoList) {
            involvedCo.setStatusCode(statusCode);
            getRepository().saveEntity(involvedCo);
        }
    }
    
    @Override
    public List<CadastreObjectTarget> getCadastreObjectTargetsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObjectTarget.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObjectTarget.class, params);
    }

    @Override
    public List<SurveyPoint> getSurveyPointsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                SurveyPoint.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(SurveyPoint.class, params);
    }

    @Override
    public List<CadastreObject> getCadastreObjectsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObject.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObject.class, params);

    }
    
    @Override
    public CadastreObjectNode getCadastreObjectNode(
            double xMin, double yMin, double xMax, double yMax, int srid){
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_FROM_PART, 
                CadastreObjectNode.QUERY_GET_BY_RECTANGLE_FROM_PART);
        params.put(CommonSqlProvider.PARAM_WHERE_PART, 
                CadastreObjectNode.QUERY_GET_BY_RECTANGLE_WHERE_PART);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, 1);
        params.put("minx", xMin);
        params.put("miny", yMin);
        params.put("maxx", xMax);
        params.put("maxy", yMax);
        params.put("srid", srid);
        CadastreObjectNode cadastreObjectNode = getRepository().getEntity(
                CadastreObjectNode.class, params);   
        if (cadastreObjectNode != null){
            params.clear();
            params.put("geom", cadastreObjectNode.getGeom());
            List<CadastreObject> cadastreObjectInvolvedList = getRepository().getEntityList(
                    CadastreObject.class, CadastreObject.QUERY_WHERE_SEARCHBYGEOM, params);
            cadastreObjectNode.setCadastreObjectList(cadastreObjectInvolvedList);
        }
        return cadastreObjectNode;

    }
}
