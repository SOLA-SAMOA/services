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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.faults.SOLAValidationException;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.cadastre.repository.entities.CadastreChange;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectType; // NOTE namespace change
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectStatusChanger;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectTarget;
import org.sola.services.ejb.cadastre.repository.entities.SurveyPoint;
import org.sola.services.ejb.cadastre.repository.entities.TransactionSource;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.Transaction;

/**
 * Implementation of {
 * <p/>
 * @link CadastreEJBLocal} interface.
 */
@Stateless
@EJB(name = "java:global/SOLA/CadastreEJBLocal", beanInterface = CadastreEJBLocal.class)
public class CadastreEJB extends AbstractEJB implements CadastreEJBLocal {

    @EJB
    private TransactionEJBLocal transactionEJB;
    @EJB
    private SystemEJBLocal systemEJB;

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
    public List<ValidationResult> approveTransaction(
            String transactionId, String approvedStatus, boolean validateOnly, String languageCode) {
        List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        if (!validateOnly && systemEJB.validationSucceeded(validationResultList)) {
            this.makeTransactionChanges(transactionId);
        }
        return validationResultList;
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

    @Override
    public List<ValidationResult> saveCadastreChange(
            CadastreChange cadastreChange, String languageCode) {
        transactionEJB.rejectTransaction(cadastreChange.getFromServiceId());
        Transaction transaction =
                transactionEJB.getTransactionByServiceId(cadastreChange.getFromServiceId(), true);
        LocalInfo.setTransactionId(transaction.getId());
        for (String targetCadastreObjectId : cadastreChange.getTargetCadastreObjectIdList()) {
            CadastreObjectTarget cadastreObjectTarget = new CadastreObjectTarget();
            //cadastreObjectTarget.setTransactionId(transaction.getId());
            cadastreObjectTarget.setCadastreObjectId(targetCadastreObjectId);
            getRepository().saveEntity(cadastreObjectTarget);
        }
        for (CadastreObject newCadastreObject : cadastreChange.getNewCadastreObjectList()) {
            //Set it to null to reset it later
            newCadastreObject.setId(null);
            //newCadastreObject.setTransactionId(transaction.getId());
            getRepository().saveEntity(newCadastreObject);
        }
        for (SurveyPoint surveyPoint : cadastreChange.getSurveyPointList()) {
            //surveyPoint.setTransactionId(transaction.getId());
            getRepository().saveEntity(surveyPoint);
        }
        for (String sourceId : cadastreChange.getSourceIdList()) {
            TransactionSource transactionCadastreSource =
                    new TransactionSource();
            //transactionCadastreSource.setTransactionId(transaction.getId());
            transactionCadastreSource.setSourceId(sourceId);
            getRepository().saveEntity(transactionCadastreSource);
        }
        List<ValidationResult> validationResultList = validateCadastreChange(
                transaction.getId(), languageCode, RegistrationStatusType.STATUS_PENDING);
        if (!systemEJB.validationSucceeded(validationResultList)) {
            throw new SOLAValidationException(validationResultList);
        }
        return validationResultList;

    }

    @Override
    public CadastreChange getCadastreChange(String serviceId) {
        CadastreChange cadastreChange = new CadastreChange();
        cadastreChange.setFromServiceId(serviceId);
        Transaction transaction = transactionEJB.getTransactionByServiceId(serviceId, false);
        if (transaction != null) {


            HashMap params = new HashMap();
            params.put("transaction_id", transaction.getId());

            List<CadastreObjectTarget> cadastreObjectTargetList =
                    getRepository().getEntityList(CadastreObjectTarget.class,
                    CadastreObjectTarget.QUERY_WHERE_SEARCHBYTRANSACTION, (HashMap) params.clone());
            if (cadastreObjectTargetList != null) {
                for (CadastreObjectTarget coTarget : cadastreObjectTargetList) {
                    cadastreChange.getTargetCadastreObjectIdList().add(coTarget.getCadastreObjectId());
                }
            }
            List<TransactionSource> sourceInTransactionList =
                    getRepository().getEntityList(TransactionSource.class,
                    TransactionSource.QUERY_WHERE_SEARCHBYTRANSACTION, (HashMap) params.clone());
            if (sourceInTransactionList != null) {
                for (TransactionSource sourceInTransaction : sourceInTransactionList) {
                    cadastreChange.getSourceIdList().add(sourceInTransaction.getSourceId());
                }
            }
            List<SurveyPoint> surveyPointList =
                    getRepository().getEntityList(SurveyPoint.class,
                    SurveyPoint.QUERY_WHERE_SEARCHBYTRANSACTION, (HashMap) params.clone());
            cadastreChange.setSurveyPointList(surveyPointList);

            List<CadastreObject> cadastreObjectList =
                    getRepository().getEntityList(CadastreObject.class,
                    CadastreObject.QUERY_WHERE_SEARCHBYTRANSACTION, (HashMap) params.clone());
            cadastreChange.setNewCadastreObjectList(cadastreObjectList);

        }
        return cadastreChange;
    }

    private List<ValidationResult> validateCadastreChange(
            String transactionId, String languageCode, String momentCode) {
        List<BrValidation> brValidationList =
                this.systemEJB.getBrForValidatingCadastreObject(momentCode);
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", transactionId);
        //Run the validation
        return this.systemEJB.checkRulesGetValidation(brValidationList, languageCode, params);
    }

    private void makeTransactionChanges(String transactionId) {
        HashMap params = new HashMap();
        params.put("transaction_id", transactionId);
        List<CadastreObjectStatusChanger> involvedCoList =
                getRepository().getEntityList(CadastreObjectStatusChanger.class,
                CadastreObjectStatusChanger.QUERY_WHERE_SEARCHBYTRANSACTION, params);
        String newStatus = null;
        for (CadastreObjectStatusChanger involvedCo : involvedCoList) {
            newStatus = RegistrationStatusType.STATUS_CURRENT.equals(involvedCo.getStatusCode())
                    ? RegistrationStatusType.STATUS_HISTORIC : RegistrationStatusType.STATUS_CURRENT;
            involvedCo.setStatusCode(newStatus);
            getRepository().saveEntity(involvedCo);
        }
    }

    @Override
    public List<ValidationResult> approveTransactionCadastreChange(String transactionId,
            boolean validateOnly, String languageCode) {
        List<ValidationResult> validationResultList = this.validateCadastreChange(transactionId,
                languageCode, RegistrationStatusType.STATUS_CURRENT);
        if (!validateOnly && systemEJB.validationSucceeded(validationResultList)) {
            this.makeTransactionChanges(transactionId);
        }
        return validationResultList;
    }
}
