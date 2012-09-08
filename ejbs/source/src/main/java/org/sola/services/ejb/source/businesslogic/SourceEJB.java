/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations
 * (FAO). All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this
 * list of conditions and the following disclaimer. 2. Redistributions in binary
 * form must reproduce the above copyright notice,this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
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
import org.sola.services.common.EntityAction;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.faults.SOLAValidationException;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.digitalarchive.businesslogic.DigitalArchiveEJBLocal;
import org.sola.services.ejb.source.repository.entities.*;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;

/**
 * EJB to manage data in the source schema. Supports retrieving and saving
 * source details.
 */
@Stateless
@EJB(name = "java:global/SOLA/SourceEJBLocal", beanInterface = SourceEJBLocal.class)
public class SourceEJB extends AbstractEJB implements SourceEJBLocal {

    @EJB
    DigitalArchiveEJBLocal digitalArchiveEJB;
    @EJB
    TransactionEJBLocal transactionEJB;
    @EJB
    SystemEJBLocal systemEJB;

    /**
     * Sets the entity package for the EJB to
     * Source.class.getPackage().getName(). This is used to restrict the save
     * and retrieval of Code Entities.
     *
     * @see AbstractEJB#getCodeEntity(java.lang.Class, java.lang.String,
     * java.lang.String) AbstractEJB.getCodeEntity
     * @see AbstractEJB#getCodeEntityList(java.lang.Class, java.lang.String)
     * AbstractEJB.getCodeEntityList
     * @see
     * AbstractEJB#saveCodeEntity(org.sola.services.common.repository.entities.AbstractCodeEntity)
     * AbstractEJB.saveCodeEntity
     */
    @Override
    protected void postConstruct() {
        setEntityPackage(Source.class.getPackage().getName());
    }

    /**
     * Retrieves a list of sources created by the specified transaction.
     *
     * @param transactionId Identifier of the transaction.
     */
    private List<Source> getSourceByTransactionId(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Source.QUERY_WHERE_BYTRANSACTIONID);
        params.put(Source.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        return getRepository().getEntityList(Source.class, params);
    }
    
    /**
     * Retrieves a list of power of attorney created by the specified transaction.
     *
     * @param transactionId Identifier of the transaction.
     */
    private List<PowerOfAttorney> getPowerOfAttorneyByTransactionId(String transactionId) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_QUERY, PowerOfAttorney.QUERY_GET_BY_TRANSACTION_ID);
        params.put(PowerOfAttorney.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        return getRepository().getEntityList(PowerOfAttorney.class, params);
    }

    /**
     * Can be used to create a new source or save any updates to the details of
     * an existing source. <p>Requires the {@linkplain RolesConstants#SOURCE_SAVE}
     * role.</p>
     *
     * @param source The source to create/save
     * @return The source after the save is completed.
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Source saveSource(Source source) {
        return getRepository().saveEntity(source);
    }

    /**
     * Retrieves all source.source records from the database.<p>No role is
     * required to execute this method.</p>
     */
    @Override
    public List<Source> getAllsources() {
        return getRepository().getEntityList(Source.class);
    }

    /**
     * Returns a list of sources matching the supplied ids. <p>No role is
     * required to execute this method.</p>
     *
     * @param sourceIds The list of source ids
     */
    @Override
    public List<Source> getSources(List<String> sourceIds) {
        return getRepository().getEntityListByIds(Source.class, sourceIds);
    }

    /**
     * Returns the details for the specified source.
     *
     * <p>No role is required to execute this method.</p>
     *
     * @param id The identifier of the source to retrieve.
     */
    @Override
    public Source getSourceById(String id) {
        return getRepository().getEntity(Source.class, id);
    }

    @Override
    public PowerOfAttorney getPowerOfAttorneyById(String id) {
        return getRepository().getEntity(PowerOfAttorney.class, id);
    }

    /**
     * Retrieves all source.source_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<SourceType> getSourceTypes(String languageCode) {
        return getRepository().getCodeList(SourceType.class, languageCode);
    }

    /**
     * Retrieves all source.availability_status_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<AvailabilityStatus> getAvailabilityStatusList(String languageCode) {
        return getRepository().getCodeList(AvailabilityStatus.class, languageCode);
    }

    /**
     * Retrieves all source.presentation_form_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<PresentationFormType> getPresentationFormTypes(String languageCode) {
        return getRepository().getCodeList(PresentationFormType.class, languageCode);
    }

    /**
     * Performs the approval action against all sources created by the specified
     * transaction. Triggered as part of the application approval action.
     *
     * <p>Requires the {@linkplain RolesConstants#APPLICATION_APPROVE} role.</p>
     *
     * @param transactionId The identifier of the transaction.
     * @param approvedStatus The status to update the source records with
     * @param validateOnly Indicates only validation of the sources should
     * occur.
     * @param languageCode Language code to use for localization of validation
     * messages
     * @return The validation messages resulting from the approval action. Note
     * that currently there are no validation rules for sources so this list is
     * always empty.
     */
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
            params.put("username", getUserName());
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
     * Associates a source with a transaction and sets the source status to
     * <code>pending</code>. Also validates the source to ensure it does not
     * have any other pending transaction associations. Note that the original
     * source record is duplicated. Will also create a new transaction record if
     * one does not already exist for the service.
     *
     * <p>Requires the {@linkplain RolesConstants#SOURCE_TRANSACTIONAL}
     * role.</p>
     *
     * @param serviceId Identifier of the service the source relates to. Used to
     * determine the transaction to associate the source with.
     * @param sourceId Identifier of the source to validate
     * @throws SOLAValidationException If the source already has a pending
     * association with another transaction.
     * @throws SOLAException If the source does not exist
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
        TransactionBasic transaction =
                transactionEJB.getTransactionByServiceId(serviceId, true, TransactionBasic.class);
        source.setTransactionId(transaction.getId());
        source.setStatusCode(RegistrationStatusType.STATUS_PENDING);
        return saveSource(source);
    }

    /**
     * Associates a Power of attorney with a transaction and sets the related
     * source status to
     * <code>pending</code>. Also validates the source to ensure it does not
     * have any other pending transaction associations. Note that the original
     * source record is duplicated. Will also create a new transaction record if
     * one does not already exist for the service.
     *
     * <p>Requires the {@linkplain RolesConstants#SOURCE_TRANSACTIONAL}
     * role.</p>
     *
     * @param serviceId Identifier of the service the source relates to. Used to
     * determine the transaction to associate the source with.
     * @param powerOfAttorney Power of attorney object containing related
     * source.
     * @throws SOLAValidationException If the source already has a pending
     * association with another transaction.
     * @throws SOLAException If the source does not exist
     */
    @RolesAllowed(RolesConstants.SOURCE_TRANSACTIONAL)
    @Override
    public PowerOfAttorney attachPowerOfAttorneyToTransaction(String serviceId,
            PowerOfAttorney powerOfAttorney, String languageCode) {
        if (powerOfAttorney == null || powerOfAttorney.getSource() == null) {
            return null;
        }

        Source source = attachSourceToTransaction(serviceId, 
                powerOfAttorney.getSource().getId(), languageCode);
        
        powerOfAttorney.setSource(source);
        powerOfAttorney.setId(source.getId());
        powerOfAttorney.setRowVersion(0);
        powerOfAttorney.setRowId(null);
        return getRepository().saveEntity(powerOfAttorney);
    }

    /**
     * Deletes the specified source if the status of the source is
     * <code>pending</code>.
     *
     * <p>Requires the {@linkplain RolesConstants#SOURCE_TRANSACTIONAL}
     * role.</p>
     *
     * @param sourceId Identifier of the source to detach from the transaction.
     * @return true if the source is successfully deleted.
     * @throws SOLAException If the status of the source is not pending
     *
     */
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

    /**
     * Retrieves all sources associated with the service. Uses the transaction
     * associated with the service to determine the sources to return.
     *
     * @param serviceId Identifier of the service
     * @see #getSourceByTransactionId(java.lang.String) getSourceByTransactionId
     */
    @Override
    public List<Source> getSourcesByServiceId(String serviceId) {
        List<Source> sourceList = new ArrayList<Source>();
        TransactionBasic transaction =
                transactionEJB.getTransactionByServiceId(serviceId, false, TransactionBasic.class);
        if (transaction != null) {
            sourceList = getSourceByTransactionId(transaction.getId());
        }
        return sourceList;
    }

    /**
     * Executes the business rules for validating the source.
     *
     * @param sourceId The id of the source to be validated.
     * @param languageCode The language code to use for localizing the
     * validation messages.
     * @return The list of validation messages.
     * @see
     * org.sola.services.ejb.system.businesslogic.SystemEJB#getBrForValidatingTransaction(java.lang.String,
     * java.lang.String, java.lang.String)
     * SystemEJB.getBrForValidatingTransaction
     * @see
     * org.sola.services.ejb.system.businesslogic.SystemEJB#checkRuleGetValidation(org.sola.services.ejb.system.repository.entities.BrValidation,
     * java.lang.String, java.util.HashMap) SystemEJB.checkRuleGetValidation
     */
    private List<ValidationResult> validateSource(
            String sourceId, String momentCode, String languageCode) {
        List<BrValidation> brValidationList =
                this.systemEJB.getBrForValidatingTransaction("source", momentCode, null);
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", sourceId);
        params.put("username", getUserName());
        //Run the validation
        return this.systemEJB.checkRulesGetValidation(
                brValidationList, languageCode, params);
    }

    /**
     * Retrieves all Power of attorneys associated with the service. Uses the transaction
     * associated with the service to determine the attorneys to return.
     *
     * @param serviceId Identifier of the service
     * @see #getPowerOfAttorneyByTransactionId(java.lang.String) getPowerOfAttorneyByTransactionId
     */
    @Override
    public List<PowerOfAttorney> getPowerOfAttorneyByServiceId(String serviceId) {

        List<PowerOfAttorney> powerOfAttorneyList = new ArrayList<PowerOfAttorney>();
        TransactionBasic transaction =
                transactionEJB.getTransactionByServiceId(serviceId, false, TransactionBasic.class);
        if (transaction != null) {
            powerOfAttorneyList = getPowerOfAttorneyByTransactionId(transaction.getId());
        }
        return powerOfAttorneyList;
    }
}
