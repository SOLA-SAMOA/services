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
package org.sola.services.ejb.cadastre.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.cadastre.repository.entities.*;

/**
 * EJB to manage data in the cadastre schema. Supports retrieving and saving of cadastre objects.
 * Also provides methods for retrieving reference codes from the administrative schema.
 */
@Stateless
@EJB(name = "java:global/SOLA/CadastreEJBLocal", beanInterface = CadastreEJBLocal.class)
public class CadastreEJB extends AbstractEJB implements CadastreEJBLocal {

    /**
     * Retrieves all cadastre.cadastre_object_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<CadastreObjectType> getCadastreObjectTypes(String languageCode) {
        return getRepository().getCodeList(CadastreObjectType.class, languageCode);
    }

    /**
     * Retrieves a cadastre object using the specified identifier.
     *
     * @param id The identifier of the cadastre object to retrieve.
     */
    @Override
    public CadastreObject getCadastreObject(String id) {
        return getRepository().getEntity(CadastreObject.class, id);
    }

    /**
     * Retrieves a list of cadastre object matching the list of ids provided.
     *
     * @param cadastreObjIds A list of cadaster object ids to use for retrieval.
     */
    @Override
    public List<CadastreObject> getCadastreObjects(List<String> cadastreObjIds) {
        return getRepository().getEntityListByIds(CadastreObject.class, cadastreObjIds);
    }

    /**
     * Returns a maximum of 10 cadastre objects that have a name first part and/or name last part
     * that matches the specified search string. This method supports partial matches and is case
     * insensitive.
     *
     * @param searchString The search string to use
     * @return The list of cadastre objects matching the search string
     */
    @Override
    public List<CadastreObject> getCadastreObjectByParts(String searchString) {
        Integer numberOfMaxRecordsReturned = 10;
        HashMap params = new HashMap();
        params.put("search_string", searchString);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, numberOfMaxRecordsReturned);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYPARTS, params);
    }

    /**
     * Returns the cadastre object that is located at the point specified or null if there is no
     * cadastre object at that location. Uses the PostGIS ST_Intersects function to perform the
     * comparison.
     *
     * @param x The x ordinate of the location
     * @param y The y ordinate of the location
     * @param srid The SRID identifying the coordinate system for the x,y coordinate. Must match the
     * SRID used by SOLA.
     */
    @Override
    public CadastreObject getCadastreObjectByPoint(double x, double y, int srid) {
        HashMap params = new HashMap();
        params.put("x", x);
        params.put("y", y);
        params.put("srid", srid);
        return getRepository().getEntity(
                CadastreObject.class, CadastreObject.QUERY_WHERE_SEARCHBYPOINT, params);
    }

    /**
     * Can be used to create a new cadastre object or save any updates to the details of an existing
     * cadastre object.
     *
     * @param cadastreObject The cadastre object to create/save.
     * @return The cadastre object after the save is completed.
     */
    @Override
    public CadastreObject saveCadastreObject(CadastreObject cadastreObject) {
        return getRepository().saveEntity(cadastreObject);
    }

    /**
     * Retrieves all cadastre objects linked to the specified BA Unit.
     *
     * @param baUnitId Identifier of the BA Unit
     */
    @Override
    public List<CadastreObject> getCadastreObjectsByBaUnit(String baUnitId) {
        HashMap params = new HashMap();
        params.put("ba_unit_id", baUnitId);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYBAUNIT, params);
    }

    /**
     * Retrieves all cadastre objects linked to the specified Service through transaction.
     *
     * @param serviceId Identifier of the Service
     */
    @Override
    public List<CadastreObject> getCadastreObjectsByService(String serviceId) {
        HashMap params = new HashMap();
        params.put("service_id", serviceId);
        return getRepository().getEntityList(CadastreObject.class,
                CadastreObject.QUERY_WHERE_SEARCHBYSERVICE, params);
    }

    /**
     * Updates the status of cadastre objects associated with the specified transaction. The filter
     * parameter can be used to indicate the where clause to apply.
     *
     * @param transactionId Identifier of the transaction associated to the cadastre objects to be
     * updated
     * @param filter The where clause to use when retrieving the cadastre objects. Must be {@linkplain CadastreObjectStatusChanger#QUERY_WHERE_SEARCHBYTRANSACTION_PENDING}
     * or {@linkplain CadastreObjectStatusChanger#QUERY_WHERE_SEARCHBYTRANSACTION_TARGET}.
     * @param statusCode The status code to set on the selected cadastre objects.
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

    /**
     * Retrieves the list of Cadastre Object Targets associated with the transaction. <p>Cadastre
     * Object Targets are used to link the cadastre object to new transactions that may occur on the
     * cadastre object after it has been initially created - for example the transaction to
     * extinguish the cadastre object.</p>
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<CadastreObjectTarget> getCadastreObjectTargetsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObjectTarget.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObjectTarget.class, params);
    }

    /**
     * Retrieves all Survey Points associated with the transaction.
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<SurveyPoint> getSurveyPointsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                SurveyPoint.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(SurveyPoint.class, params);
    }

    /**
     * Retrieves all Cadastre Objects created by the transaction.
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<CadastreObject> getCadastreObjectsByTransaction(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObject.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObject.class, params);

    }

    /**
     * Retrieves all node points from the underlying cadastre objects that intersect the specified
     * bounding box coordinates. All of the node points within the bounding box are used to create a
     * single geometry - {@linkplain CadastreObjectNode#geom}. The cadastre objects used as the
     * source of the node points are also captured in the {@linkplain CadastreObjectNode#cadastreObjectList}.
     *
     * @param xMin The xMin ordinate of the bounding box
     * @param yMin The yMin ordinate of the bounding box
     * @param xMax The xMax ordinate of the bounding box
     * @param yMax The yMax ordinate of the bounding box
     * @param srid The SRID to use to create the bounding box. Must be the same SRID as the one used
     * by the cadastre_object table.
     * @return The CadastreObjectNode representing all node points within the bounding box as well
     * as the list of cadastre objects used to obtain the node points.
     */
    @Override
    public CadastreObjectNode getCadastreObjectNode(
            double xMin, double yMin, double xMax, double yMax, int srid) {
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
        if (cadastreObjectNode != null) {
            params.clear();
            params.put("geom", cadastreObjectNode.getGeom());
            cadastreObjectNode.setCadastreObjectList(getRepository().getEntityList(
                    CadastreObject.class, CadastreObject.QUERY_WHERE_SEARCHBYGEOM, params));
        }
        return cadastreObjectNode;

    }

    /**
     * Unknown
     *
     * @param xMin The xMin ordinate of the bounding box
     * @param yMin The yMin ordinate of the bounding box
     * @param xMax The xMax ordinate of the bounding box
     * @param yMax The yMax ordinate of the bounding box
     * @param srid The SRID to use to create the bounding box. Must be the same SRID as the one used
     * by the cadastre_object table.
     */
    @Override
    public CadastreObjectNode getCadastreObjectNodePotential(
            double xMin, double yMin, double xMax, double yMax, int srid) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_FROM_PART,
                CadastreObjectNode.QUERY_GET_BY_RECTANGLE_POTENTIAL_FROM_PART);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, 1);
        params.put("minx", xMin);
        params.put("miny", yMin);
        params.put("maxx", xMax);
        params.put("maxy", yMax);
        params.put("srid", srid);
        CadastreObjectNode cadastreObjectNode = getRepository().getEntity(
                CadastreObjectNode.class, params);
        if (cadastreObjectNode != null) {
            params.clear();
            params.put("geom", cadastreObjectNode.getGeom());
            cadastreObjectNode.setCadastreObjectList(getRepository().getEntityList(
                    CadastreObject.class, CadastreObject.QUERY_WHERE_SEARCHBYGEOM, params));
        }
        return cadastreObjectNode;
    }

    /**
     * Retrieves all Cadastre Object Node Targets associated to the transaction. <p>A Cadastre
     * Object Node Target is used to identify the nodes that have been added, moved or removed as
     * part of a redefinition transaction. </p>
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<CadastreObjectNodeTarget> getCadastreObjectNodeTargetsByTransaction(
            String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObjectNodeTarget.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObjectNodeTarget.class, params);
    }

    /**
     * Retrieves the cadastre objects that have been associated with a cadastre redefinition
     * transaction.
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<CadastreObjectTargetRedefinition> getCadastreObjectRedefinitionTargetsByTransaction(
            String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                CadastreObjectTarget.QUERY_WHERE_SEARCHBYTRANSACTION);
        params.put("transaction_id", transactionId);
        return getRepository().getEntityList(CadastreObjectTargetRedefinition.class, params);
    }

    /**
     * Approves the changes to cadastre objects as a result of a cadastre redefinition.
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public void approveCadastreRedefinition(String transactionId) {
        List<CadastreObjectTargetRedefinition> targetObjectList =
                this.getCadastreObjectRedefinitionTargetsByTransaction(transactionId);
        for (CadastreObjectTargetRedefinition targetObject : targetObjectList) {
            CadastreObjectStatusChanger cadastreObject =
                    this.getRepository().getEntity(CadastreObjectStatusChanger.class,
                    targetObject.getCadastreObjectId());
            cadastreObject.setGeomPolygon(targetObject.getGeomPolygon());
            cadastreObject.setTransactionId(transactionId);
            cadastreObject.setApprovalDatetime(null);
            this.saveEntity(cadastreObject);
        }
    }

    /**
     * Process the list of spatialUnitChanges and apply the necessary changes to the spatial unit
     * records.
     *
     * @param transactionId
     */
    @Override
    public void applySpatialUnitChanges(String transactionId) {
        List<SpatialUnitChange> spatialUnitChanges = getSpatialUnitChangeByTransaction(transactionId);
        if (spatialUnitChanges != null && !spatialUnitChanges.isEmpty()) {
            for (SpatialUnitChange change : spatialUnitChanges) {
                SpatialUnit spUnit = null;
                if (change.getSpatialUnitId() != null) {
                    //Retrieve the spatail unit to update
                    spUnit = getRepository().getEntity(SpatialUnit.class, change.getSpatialUnitId());
                }
                if (spUnit == null) {
                    // Create a new sp unit and set its level
                    spUnit = new SpatialUnit();
                    spUnit.setLevelId(change.getLevelId());
                }
                if (change.isDeleteOnApproval()) {
                    // Clear the geom so the sp unit no longer displays
                    spUnit.setGeom(null);
                    spUnit.setReferencePoint(null);
                } else {
                    // Update the sp unit label and geometry
                    spUnit.setLabel(change.getLabel());
                    if (change.isPoint()) {
                        spUnit.setReferencePoint(change.getGeom());
                    } else {
                        spUnit.setGeom(change.getGeom());
                    }
                }
                getRepository().saveEntity(spUnit);
            }
        }
    }

    /**
     * Retrieves the spatial unit change records that have been associated with a cadastre change or
     * a cadastre redefinition transaction.
     *
     * @param transactionId The identifier of the transaction
     */
    @Override
    public List<SpatialUnitChange> getSpatialUnitChangeByTransaction(
            String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(
                CommonSqlProvider.PARAM_WHERE_PART,
                SpatialUnitChange.QUERY_WHERE_BYTRANSACTIONID);
        params.put(SpatialUnitChange.QUERY_PARAMETER_TRANSACTIONID, transactionId);




        return getRepository().getEntityList(SpatialUnitChange.class, params);
    }
}
