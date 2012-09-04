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
package org.sola.services.ejb.administrative.businesslogic;

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
import org.sola.services.common.LocalInfo;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.administrative.repository.entities.*;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.Transaction;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;

/**
 * EJB to manage data in the administrative schema. Supports retrieving and saving BA Units and RRR.
 * Also provides methods for retrieving reference codes from the administrative schema.
 */
@Stateless
@EJB(name = "java:global/SOLA/AdministrativeEJBLocal", beanInterface = AdministrativeEJBLocal.class)
public class AdministrativeEJB extends AbstractEJB
        implements AdministrativeEJBLocal {

    @EJB
    private SystemEJBLocal systemEJB;
    @EJB
    private TransactionEJBLocal transactionEJB;

    /**
     * Sets the entity package for the EJB to BaUnit.class.getPackage().getName(). This is used to
     * restrict the save and retrieval of Code Entities.
     *
     * @see AbstractEJB#getCodeEntity(java.lang.Class, java.lang.String, java.lang.String)
     * AbstractEJB.getCodeEntity
     * @see AbstractEJB#getCodeEntityList(java.lang.Class, java.lang.String)
     * AbstractEJB.getCodeEntityList
     * @see
     * AbstractEJB#saveCodeEntity(org.sola.services.common.repository.entities.AbstractCodeEntity)
     * AbstractEJB.saveCodeEntity
     */
    @Override
    protected void postConstruct() {
        setEntityPackage(BaUnit.class.getPackage().getName());
    }

    /**
     * Retrieves all administrative.change_status_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<ChangeStatusType> getChangeStatusTypes(String languageCode) {
        return getRepository().getCodeList(ChangeStatusType.class, languageCode);
    }

    /**
     * Retrieves all administrative.ba_unit_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<BaUnitType> getBaUnitTypes(String languageCode) {
        return getRepository().getCodeList(BaUnitType.class, languageCode);
    }

    /**
     * Retrieves all administrative.mortgage_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<MortgageType> getMortgageTypes(String languageCode) {
        return getRepository().getCodeList(MortgageType.class, languageCode);
    }

    /**
     * Retrieves all administrative.rrr_group_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     * @return
     */
    @Override
    public List<RrrGroupType> getRRRGroupTypes(String languageCode) {
        return getRepository().getCodeList(RrrGroupType.class, languageCode);
    }

    /**
     * Retrieves all administrative.rrr_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     * @return
     */
    @Override
    public List<RrrType> getRRRTypes(String languageCode) {
        return getRepository().getCodeList(RrrType.class, languageCode);
    }

    /**
     * Retrieves all administrative.source_ba_unit_rel_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     * @return
     */
    @Override
    public List<SourceBaUnitRelationType> getSourceBaUnitRelationTypes(String languageCode) {
        return getRepository().getCodeList(SourceBaUnitRelationType.class, languageCode);
    }

    /**
     * Locates a BA Unit using by matching the first part and last part of the BA Unit name. First
     * part and last part must be an exact match.
     *
     * @param nameFirstpart The first part of the BA Unit name
     * @param nameLastpart The last part of the BA Unit name
     * @return The BA Unit matching the name
     */
    @Override
    public BaUnit getBaUnitByCode(String nameFirstpart, String nameLastpart) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYPROPERTYCODE);
        params.put(BaUnit.QUERY_PARAMETER_FIRSTPART, nameFirstpart);
        params.put(BaUnit.QUERY_PARAMETER_LASTPART, nameLastpart);
        return getRepository().getEntity(BaUnit.class, params);
    }

    /**
     * Creates a new BA Unit with a default status of pending and a default type of
     * basicPropertyUnit. Will also create a new Transaction record for the BA Unit if the Service
     * is not already associated to a Transaction.
     *
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role.</p>
     *
     * @param serviceId The identifier of the Service the BA Unit is being created as part of
     * @param baUnitTO The details of the BA Unit to create
     * @return The new BA Unit
     * @see #saveBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnit) saveBaUnit
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnit createBaUnit(String serviceId, BaUnit baUnit) {
        if (baUnit == null) {
            return null;
        }
        return saveBaUnit(serviceId, baUnit);
    }

    /**
     * Saves any updates to an existing BA Unit. Can also be used to create a new BA Unit, however
     * this method does not set any default values on the BA Unit like
     * {@linkplain #createBaUnit(java.lang.String, org.sola.services.ejb.administrative.repository.entities.BaUnit)
     * createBaUnit}. Will also create a new Transaction record for the BA Unit if the Service is
     * not already associated to a Transaction.
     *
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role</p>
     *
     * @param serviceId The identifier of the Service the BA Unit is being created as part of
     * @param baUnitTO The details of the BA Unit to create
     * @return The updated BA Unit
     * @see #createBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnit) createBaUnit
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnit saveBaUnit(String serviceId, BaUnit baUnit) {
        if (baUnit == null) {
            return null;
        }
        if (baUnit.isNew() && baUnit.getNameFirstpart() == null && baUnit.getNameLastpart() == null
                && (baUnit.getCadastreObjectList() == null || baUnit.getCadastreObjectList().isEmpty())) {
            // Samoa Customization. Creating a new Proprety with no reference to a parcel is not
            // valid if the NameFirstPart (i.e. Lot number) and NameLastPart (i.e. Plan number) are
            // not provided. 
            throw new SOLAException(ServiceMessage.EJB_ADMINISTRATIVE_NO_PARCEL); 
        }
        TransactionBasic transaction =
                transactionEJB.getTransactionByServiceId(serviceId, true, TransactionBasic.class);
        LocalInfo.setTransactionId(transaction.getId());
        return getRepository().saveEntity(baUnit);
    }

    /**
     * Retrieves the BA Unit matching the supplied identifier.
     *
     * @param id The BA Unit identifier
     * @return The BA Unit details or null if the identifier is invalid.
     */
    @Override
    public BaUnit getBaUnitById(String id) {
        BaUnit result = null;
        if (id != null) {
            result = getRepository().getEntity(BaUnit.class, id);
        }
        return result;
    }

    /**
     * Applies the appropriate approval action to every BA Unit that is associated to the specified
     * transaction. This includes updating the status of RRR and Notations associated with the BA
     * Unit. <p>Can also be used to test the outcome of the approval using the validateOnly
     * flag.</p>
     *
     * @param transactionId The Transaction identifier
     * @param approvedStatus The status to set if the validation of the BA Unit is successful.
     * @param validateOnly Validate the transaction data, but do not apply and status changes
     * @param languageCode Language code to use for localization of the validation messages
     * @return A list of validation results.
     */
    @Override
    public List<ValidationResult> approveTransaction(
            String transactionId, String approvedStatus,
            boolean validateOnly, String languageCode) {
        List<ValidationResult> validationResult = new ArrayList<ValidationResult>();

        //Change the status of BA Units that are involved in a transaction directly
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYTRANSACTIONID);
        params.put(BaUnit.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        List<BaUnitStatusChanger> baUnitList =
                getRepository().getEntityList(BaUnitStatusChanger.class, params);

        for (BaUnitStatusChanger baUnit : baUnitList) {
            validationResult.addAll(this.validateBaUnit(baUnit, languageCode));
            if (systemEJB.validationSucceeded(validationResult) && !validateOnly) {
                baUnit.setStatusCode(approvedStatus);
                baUnit.setTransactionId(transactionId);
                getRepository().saveEntity(baUnit);
            }
        }

        params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Rrr.QUERY_WHERE_BYTRANSACTIONID);
        params.put(Rrr.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        List<RrrStatusChanger> rrrStatusChangerList =
                getRepository().getEntityList(RrrStatusChanger.class, params);
        for (RrrStatusChanger rrr : rrrStatusChangerList) {
            validationResult.addAll(this.validateRrr(rrr, languageCode));
            if (systemEJB.validationSucceeded(validationResult) && !validateOnly) {
                rrr.setStatusCode(approvedStatus);
                getRepository().saveEntity(rrr);
            }
        }
        if (!validateOnly) {
            params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnitNotation.QUERY_WHERE_BYTRANSACTIONID);
            params.put(BaUnitNotation.QUERY_PARAMETER_TRANSACTIONID, transactionId);
            params.put(CommonSqlProvider.PARAM_ORDER_BY_PART,  BaUnitNotation.QUERY_ORDER_BY);
        
            List<BaUnitNotationStatusChanger> baUnitNotationList =
                    getRepository().getEntityList(BaUnitNotationStatusChanger.class, params);
            for (BaUnitNotationStatusChanger baUnitNotation : baUnitNotationList) {
                baUnitNotation.setStatusCode(RegistrationStatusType.STATUS_CURRENT);
                getRepository().saveEntity(baUnitNotation);
            }
        }

        return validationResult;
    }

    /**
     * Executes the business rules to validate the BA Unit.
     *
     * @param baUnit The BA Unit to validate
     * @param languageCode The language code to use for localization of any validation messages
     * @return The list of validation results.
     */
    private List<ValidationResult> validateBaUnit(
            BaUnitStatusChanger baUnit, String languageCode) {
        List<BrValidation> brValidationList = this.systemEJB.getBrForValidatingTransaction(
                "ba_unit", RegistrationStatusType.STATUS_CURRENT, null);
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", baUnit.getId());
        //Run the validation
        return this.systemEJB.checkRulesGetValidation(brValidationList, languageCode, params);
    }

    /**
     * Executes the business rules to validate the RRR.
     *
     * @param rrr The RRR to validate
     * @param languageCode The language code to use for localization of any validation messages
     * @return The list of validation results.
     */
    private List<ValidationResult> validateRrr(
            RrrStatusChanger rrr, String languageCode) {
        List<BrValidation> brValidationList = this.systemEJB.getBrForValidatingRrr(
                RegistrationStatusType.STATUS_CURRENT, rrr.getTypeCode());
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", rrr.getId());
        //Run the validation
        return this.systemEJB.checkRulesGetValidation(brValidationList, languageCode, params);
    }

    /**
     * Returns all BA Units that are associated to the specified transaction
     *
     * @param transactionId The Transaction identifier
     */
    @Override
    public List<BaUnit> getBaUnitsByTransactionId(String transactionId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BY_TRANSACTION_ID_EXTENDED);
        params.put(BaUnit.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        return getRepository().getEntityList(BaUnit.class, params);
    }

    /**
     * Retrieves all administrative.ba_unit_rel_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     * @return
     */
    @Override
    public List<BaUnitRelType> getBaUnitRelTypes(String languageCode) {
        return getRepository().getCodeList(BaUnitRelType.class, languageCode);
    }

    /**
     * Identifies a BA Unit as subject to cancellation / termination by linking the BA Unit to a
     * Transaction via the administrative.ba_unit_target association. The BA Unit is not canceled /
     * terminated until the application canceling the BA Unit is approved.
     *
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role.</p>
     *
     * @param baUnitId The identifier of the BA Unit to be canceled / terminated
     * @param serviceId The identifier of the service that is canceling / terminating the BA Unit
     * @return The BA Unit that will be canceled / terminated.
     * @see #cancelBaUnitTermination(java.lang.String) cancelBaUnitTermination
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnit terminateBaUnit(String baUnitId, String serviceId) {
        if (baUnitId == null || serviceId == null) {
            return null;
        }

        // Check transaction to exist and have pending status
        Transaction transaction = transactionEJB.getTransactionByServiceId(
                serviceId, true, Transaction.class);
        if (transaction == null || !transaction.getStatusCode().equals(RegistrationStatusType.STATUS_PENDING)) {
            return null;
        }

        //TODO: Put BR check to have only one pending transaction for the BaUnit and BaUnit to be with "current" status.
        //TODO: Check BR for service to have cancel action and empty Rrr field.

        BaUnitTarget baUnitTarget = new BaUnitTarget();
        baUnitTarget.setBaUnitId(baUnitId);
        baUnitTarget.setTransactionId(transaction.getId());
        getRepository().saveEntity(baUnitTarget);

        return getBaUnitById(baUnitId);
    }

    /**
     * Reverses the cancellation / termination of a BA Unit by removing the BA Unit Target created
     * by {@linkplain #terminateBaUnit(java.lang.String, java.lang.String) terminateBaUnit}.
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role.</p>
     *
     * @param baUnitId The identifier of the BA Unit to reverse the cancellation for.
     * @return The details of the BA Unit that has had its termination canceled.
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnit cancelBaUnitTermination(String baUnitId) {
        if (baUnitId == null) {
            return null;
        }

        //TODO: Put BR check to have only one pending transaction for the BaUnit and BaUnit to be with "current" status.

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnitTarget.QUERY_WHERE_GET_BY_BAUNITID);
        params.put(BaUnitTarget.PARAM_BAUNIT_ID, baUnitId);

        List<BaUnitTarget> targets = getRepository().getEntityList(BaUnitTarget.class, params);

        if (targets != null && targets.size() > 0) {
            for (BaUnitTarget baUnitTarget : targets) {
                Transaction transaction = transactionEJB.getTransactionById(
                        baUnitTarget.getTransactionId(), Transaction.class);
                if (transaction != null
                        && transaction.getStatusCode().equals(RegistrationStatusType.STATUS_PENDING)) {
                    // DELETE peding 
                    baUnitTarget.setEntityAction(EntityAction.DELETE);
                    getRepository().saveEntity(baUnitTarget);
                }
            }
        }

        return getBaUnitById(baUnitId);
    }
    
      /**
     * Retrieves the actions a specific user has performed against any application during a specific
     * period.
     *
     * @param baUnitId
     * @return The list of areas of the baunit
     */
    @Override
    public BaUnitArea getBaUnitAreas(String baUnitId) {
        BaUnitArea result = null;
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnitArea.QUERY_WHERE_BYUNITAREAID);
        params.put(BaUnitArea.QUERY_WHERE_BYBAUNITID, baUnitId);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BaUnitArea.QUERY_ORDER_BYCHANGETIME);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, 1);
        result = getRepository().getEntity(BaUnitArea.class, params);
        
        
        System.out.println("PARAMS QUERY: "+params);
        return result;
    }
    
    
    /**
     * Creates a new BA Unit Area 
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role.</p>
     *
     * @param baUnitId The identifier of the area the BA Unit is being created as part of
     * @param baUnitAreaTO The details of the BA Unit to create
     * @return The new BA Unit Area
     * @see #saveBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnitArea) createBaUnit
     */
    @Override
//    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnitArea createBaUnitArea(String baUnitId, BaUnitArea baUnitArea) {
        if (baUnitArea == null) {
            return null;
        }
        return getRepository().saveEntity(baUnitArea);
    }
    
    
}
