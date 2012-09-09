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

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.cadastre.repository.entities.*;

/**
 * Local interface for the {@linkplain CadastreEJB}
 */
@Local
public interface CadastreEJBLocal extends AbstractEJBLocal {

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectTypes(java.lang.String)
     * CadastreEJB.getCadastreObjectTypes}.
     */
    List<CadastreObjectType> getCadastreObjectTypes(String languageCode);

    /**
     * See {@linkplain CadastreEJB#getCadastreObject(java.lang.String)
     * CadastreEJB.getCadastreObject}.
     */
    CadastreObject getCadastreObject(String id);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjects(java.util.List)
     * CadastreEJB.getCadastreObjects}.
     */
    List<CadastreObject> getCadastreObjects(List<String> cadastreObjIds);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectByParts(java.lang.String)
     * CadastreEJB.getCadastreObjectByParts}.
     */
    List<CadastreObject> getCadastreObjectByParts(String searchString);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectByAllParts(java.lang.String)
     * CadastreEJB.getCadastreObjectByAllParts}.
     */
    List<CadastreObject> getCadastreObjectByAllParts(String searchString);

    /**
     * See {@linkplain CadastreEJB#saveCadastreObject(org.sola.services.ejb.cadastre.repository.entities.CadastreObject)
     * CadastreEJB.saveCadastreObject}.
     */
    CadastreObject saveCadastreObject(CadastreObject cadastreObject);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectByPoint(double, double, int)
     * CadastreEJB.getCadastreObjectByPoint}.
     */
    CadastreObject getCadastreObjectByPoint(double x, double y, int srid, String typeCode);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectsByBaUnit(java.lang.String)
     * CadastreEJB.getCadastreObjectsByBaUnit}.
     */
    List<CadastreObject> getCadastreObjectsByBaUnit(String baUnitId);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectsByService(java.lang.String)
     * CadastreEJB.getCadastreObjectsByService}.
     */
    List<CadastreObject> getCadastreObjectsByService(String serviceId);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectTargetsByTransaction(java.lang.String)
     * CadastreEJB.getCadastreObjectTargetsByTransaction}.
     */
    List<CadastreObjectTarget> getCadastreObjectTargetsByTransaction(String transactionId);

    /**
     * See {@linkplain CadastreEJB#getSurveyPointsByTransaction(java.lang.String)
     * CadastreEJB.getSurveyPointsByTransaction}.
     */
    List<SurveyPoint> getSurveyPointsByTransaction(String transactionId);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectsByTransaction(java.lang.String)
     * CadastreEJB.getCadastreObjectsByTransaction}.
     */
    List<CadastreObject> getCadastreObjectsByTransaction(String transactionId);

    /**
     * See {@linkplain CadastreEJB#ChangeStatusOfCadastreObjects(java.lang.String, java.lang.String, java.lang.String)
     * CadastreEJB.ChangeStatusOfCadastreObjects}.
     */
    void ChangeStatusOfCadastreObjects(String transactionId, String filter, String statusCode);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectNode(double, double, double, double, int)
     * CadastreEJB.getCadastreObjectNode}.
     */
    CadastreObjectNode getCadastreObjectNode(
            double xMin, double yMin, double xMax, double yMax, int srid, String cadastreObjectType);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectNodePotential(double, double, double, double, int)
     * CadastreEJB.getCadastreObjectNodePotential}.
     */
    CadastreObjectNode getCadastreObjectNodePotential(
            double xMin, double yMin, double xMax, double yMax, int srid, String cadastreObjectType);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectNodeTargetsByTransaction(java.lang.String)
     * CadastreEJB.getCadastreObjectNodeTargetsByTransaction}.
     */
    List<CadastreObjectNodeTarget> getCadastreObjectNodeTargetsByTransaction(String transactionId);

    /**
     * See {@linkplain CadastreEJB#getCadastreObjectRedefinitionTargetsByTransaction(java.lang.String)
     * CadastreEJB.getCadastreObjectRedefinitionTargetsByTransaction}.
     */
    List<CadastreObjectTargetRedefinition> getCadastreObjectRedefinitionTargetsByTransaction(
            String transactionId);

    /**
     * See {@linkplain CadastreEJB#approveCadastreRedefinition(java.lang.String)
     * CadastreEJB.approveCadastreRedefinition}.
     */
    void approveCadastreRedefinition(String transactionId);
}
