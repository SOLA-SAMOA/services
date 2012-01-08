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
package org.sola.services.ejb.cadastre.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractSolaTransactionEJBLocal;
import org.sola.services.ejb.cadastre.repository.entities.CadastreChange;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectType;
import org.sola.services.ejb.cadastre.repository.entities.SurveyPoint;

/**
 * Local interface for handling cadastre schema operations.
 */
@Local
public interface CadastreEJBLocal extends AbstractSolaTransactionEJBLocal {

    List<CadastreObjectType> getCadastreObjectTypes(String languageCode);

    CadastreObject getCadastreObject(String id);

    List<CadastreObject> getCadastreObjects(List<String> cadastreObjIds);

    List<CadastreObject> getCadastreObjectByParts(String searchString);

    //CadastreObject createCadastreObject(CadastreObject cadastreObject);
    CadastreObject saveCadastreObject(CadastreObject cadastreObject);

//    boolean removeCadastreObject(CadastreObject cadastreObject);
    CadastreObject getCadastreObjectByPoint(double x, double y, int srid);

    List<CadastreObject> getCadastreObjectsByBaUnit(String baUnitId);

    List<CadastreObject> getCadastreObjectsByService(String serviceId);

    List<ValidationResult> saveCadastreChange(CadastreChange cadastreChange, String languageCode);

    CadastreChange getCadastreChange(String serviceId);

    List<ValidationResult> approveTransactionCadastreChange(String transactionId,
            boolean validateOnly, String languageCode);
}
