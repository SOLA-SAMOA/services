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
/*
 * Food and Agriculture Orgainsation (FAO) of the United Nations
 * Solutions for Open Source Land Administration - Sola.
 */
package org.sola.services.ejb.source.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.RolesConstants;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.faults.SOLAValidationException;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.digitalarchive.businesslogic.DigitalArchiveEJBLocal;
import org.sola.services.ejb.source.repository.entities.AvailabilityStatus;
import org.sola.services.ejb.source.repository.entities.PresentationFormType;
import org.sola.services.ejb.source.repository.entities.Source;
import org.sola.services.ejb.source.repository.entities.SourceStatusChanger;
import org.sola.services.ejb.source.repository.entities.SourceType;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.Transaction;

@Stateless
@EJB(name = "java:global/SOLA/SourceEJBLocal", beanInterface = SourceEJBLocal.class)
public class SourceEJB extends AbstractEJB implements SourceEJBLocal {

    @EJB
    DigitalArchiveEJBLocal digitalArchiveEJB;
    @EJB
    TransactionEJBLocal transactionEJB;
    @EJB
    SystemEJBLocal systemEJB;

    @Override
    protected void postConstruct() {
        setEntityPackage(Source.class.getPackage().getName());
    }
    
    
    private List<Source> getSourceByTransactionId(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Source.QUERY_WHERE_BYTRANSACTIONID);
        params.put(Source.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        return getRepository().getEntityList(Source.class, params);
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Source saveSource(Source source) {
        return getRepository().saveEntity(source);
    }

    @Override
    public List<Source> getAllsources() {
        return getRepository().getEntityList(Source.class);
    }

    @Override
    public List<Source> getSources(List<String> sourceIds) {
        return getRepository().getEntityListByIds(Source.class, sourceIds);
    }

    @Override
    public Source getSourceById(String id) {
        return getRepository().getEntity(Source.class, id);
    }

    @Override
    public List<SourceType> getSourceTypes(String languageCode) {
        return getRepository().getCodeList(SourceType.class, languageCode);
    }

    @Override
    public List<AvailabilityStatus> getAvailabilityStatusList(String languageCode) {
        return getRepository().getCodeList(AvailabilityStatus.class, languageCode);
    }

    @Override
    public List<PresentationFormType> getPresentationFormTypes(String languageCode) {
        return getRepository().getCodeList(PresentationFormType.class, languageCode);
    }

    @Override
    @RolesAllowed(RolesConstants.APPLICATION_APPROVE)
    public List<ValidationResult> approveTransaction(
            String transactionId, String approvedStatus,
            boolean validateOnly, String languageCode) {
        List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        if (!validateOnly) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, Source.QUERY_WHERE_BYTRANSACTIONID);
            params.put(Source.QUERY_PARAMETER_TRANSACTIONID, transactionId);
            List<SourceStatusChanger> sourceList =
                    getRepository().getEntityList(SourceStatusChanger.class, params);

            for (SourceStatusChanger source : sourceList) {
                source.setStatusCode(approvedStatus);
                getRepository().saveEntity(source);
            }
        }
        return validationResultList;
    }

    /**
     * Associates a source with a transaction and sets the source status to Pending. 
     * @param serviceId
     * @param sourceId 
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_TRANSACTIONAL)
    public Source attachSourceToTransaction(String serviceId, String sourceId, String languageCode) {
        //Check br that the source does not have any other pending associated.
        List<ValidationResult> validationResult =
                this.validateSource(sourceId, RegistrationStatusType.STATUS_PENDING, languageCode);
        if (!systemEJB.validationSucceeded(validationResult)) {
            throw new SOLAValidationException(validationResult);
        }

        // Search for the source
        Source source = this.getSourceById(sourceId);
        if (source == null) {
            throw new SOLAException(ServiceMessage.EJB_SOURCE_SOURCE_NOT_FOUND);
        }

        // Duplicate the source record. Need to assign a new Id and RowId as well as reset some 
        // values and mark the source for Save (i.e. insert)
        source.setId(null);
        source.setRowId(null);
        source.setLoaded(false);
        source.resetEntityAction();
        source.setRowVersion(0);
        source.markForSave();


        // Get the transaction. If transaction does not exist it will be created
        Transaction transaction = transactionEJB.getTransactionByServiceId(serviceId, true);
        source.setTransactionId(transaction.getId());
        source.setStatusCode(RegistrationStatusType.STATUS_PENDING);
        return saveSource(source);
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_TRANSACTIONAL)
    public boolean dettachSourceFromTransaction(String sourceId) {
        boolean success = false;
        Source source = this.getSourceById(sourceId);

        if (source != null) {
            if (!source.getStatusCode().equals(RegistrationStatusType.STATUS_PENDING)) {
                throw new SOLAException(ServiceMessage.EJB_SOURCE_SOURCE_NOT_PENDING);
            }

            source.markForDelete();
            saveSource(source);
            success = true;
        }

        return success;
    }

    @Override
    public List<Source> getSourcesByServiceId(String serviceId) {
        List<Source> sourceList = new ArrayList<Source>();
        Transaction transaction = transactionEJB.getTransactionByServiceId(serviceId, false);
        if (transaction != null) {
            sourceList = getSourceByTransactionId(transaction.getId());
        }
        return sourceList;
    }

    /**
     * It runs the business rules for validating the source.
     * @param sourceId The id of the source to be validated
     * @param languageCode
     * @return 
     */
    private List<ValidationResult> validateSource(
            String sourceId, String momentCode, String languageCode) {
        List<BrValidation> brValidationList =
                this.systemEJB.getBrForValidatingSource(momentCode);
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", sourceId);
        //Run the validation
        return this.systemEJB.checkRulesGetValidation(
                brValidationList, languageCode, params);
    }
    
    
    /** Returns list of {@link Source} objects, by the given list of IDs. */
    @Override
    public List<Source> getSourcesByIds(List<String> sourceIds) {
        return getRepository().getEntityListByIds(Source.class, sourceIds);
    }
}
