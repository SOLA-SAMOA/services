/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.services.ejb.administrative.businesslogic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.DateUtility;
import org.sola.common.RolesConstants;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.EntityAction;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.administrative.repository.AdministrativeSqlProvider;
import org.sola.services.ejb.administrative.repository.entities.*;
import org.sola.services.ejb.cadastre.repository.entities.*;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.Transaction;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;
import org.sola.services.ejb.transaction.repository.entities.TransactionUnitParcels;

/**
 * EJB to manage data in the administrative schema. Supports retrieving and
 * saving BA Units and RRR. Also provides methods for retrieving reference codes
 * from the administrative schema.
 */
@Stateless
@EJB(name = "java:global/SOLA/AdministrativeEJBLocal", beanInterface = AdministrativeEJBLocal.class)
public class AdministrativeEJB extends AbstractEJB
        implements AdministrativeEJBLocal {

    @EJB
    private SystemEJBLocal systemEJB;
    @EJB
    private TransactionEJBLocal transactionEJB;
    @EJB
    private PartyEJBLocal partyEJB;
    private static final String CREATE_STRATA_TITLE = "newUnitTitle";
    private static final String CANCEL_STRATA_TITLE = "cancelUnitPlan";
    private static final String CHANGE_ESTATE_TYPE = "varyTitle";
    private static final String PROCLAMATION_TYPE = "proclamation";

    /**
     * Sets the entity package for the EJB to
     * BaUnit.class.getPackage().getName(). This is used to restrict the save
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
        setEntityPackage(BaUnit.class.getPackage().getName());
    }

    /**
     * Retrieves all administrative.change_status_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<ChangeStatusType> getChangeStatusTypes(String languageCode) {
        return getRepository().getCodeList(ChangeStatusType.class, languageCode);
    }

    /**
     * Retrieves all administrative.ba_unit_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<BaUnitType> getBaUnitTypes(String languageCode) {
        return getRepository().getCodeList(BaUnitType.class, languageCode);
    }

    /**
     * Retrieves all administrative.mortgage_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     */
    @Override
    public List<MortgageType> getMortgageTypes(String languageCode) {
        return getRepository().getCodeList(MortgageType.class, languageCode);
    }

    /**
     * Retrieves all administrative.rrr_group_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     * @return
     */
    @Override
    public List<RrrGroupType> getRRRGroupTypes(String languageCode) {
        return getRepository().getCodeList(RrrGroupType.class, languageCode);
    }

    /**
     * Retrieves all administrative.rrr_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     * @return
     */
    @Override
    public List<RrrType> getRRRTypes(String languageCode) {
        return getRepository().getCodeList(RrrType.class, languageCode);
    }

    /**
     * Retrieves all administrative.source_ba_unit_rel_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     * @return
     */
    @Override
    public List<SourceBaUnitRelationType> getSourceBaUnitRelationTypes(String languageCode) {
        return getRepository().getCodeList(SourceBaUnitRelationType.class, languageCode);
    }

    /**
     * Locates a BA Unit using by matching the first part and last part of the
     * BA Unit name. First part and last part must be an exact match.
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
     * Creates a new BA Unit with a default status of pending and a default type
     * of basicPropertyUnit. Will also create a new Transaction record for the
     * BA Unit if the Service is not already associated to a Transaction.
     *
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
     * role.</p>
     *
     * @param serviceId The identifier of the Service the BA Unit is being
     * created as part of
     * @param baUnitTO The details of the BA Unit to create
     * @return The new BA Unit
     * @see #saveBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnit)
     * saveBaUnit
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
     * Saves any updates to an existing BA Unit. Can also be used to create a
     * new BA Unit, however this method does not set any default values on the
     * BA Unit like {@linkplain #createBaUnit(java.lang.String, org.sola.services.ejb.administrative.repository.entities.BaUnit)
     * createBaUnit}. Will also create a new Transaction record for the BA Unit
     * if the Service is not already associated to a Transaction.
     *
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
     * role</p>
     *
     * @param serviceId The identifier of the Service the BA Unit is being
     * created as part of
     * @param baUnitTO The details of the BA Unit to create
     * @return The updated BA Unit
     * @see #createBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnit)
     * createBaUnit
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
        TransactionBasic transaction
                = transactionEJB.getTransactionByServiceId(serviceId, true, TransactionBasic.class);
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
     * Applies the appropriate approval action to every BA Unit that is
     * associated to the specified transaction. This includes updating the
     * status of RRR and Notations associated with the BA Unit.
     * <p>
     * Can also be used to test the outcome of the approval using the
     * validateOnly flag.</p>
     *
     * @param transactionId The Transaction identifier
     * @param approvedStatus The status to set if the validation of the BA Unit
     * is successful.
     * @param validateOnly Validate the transaction data, but do not apply and
     * status changes
     * @param languageCode Language code to use for localization of the
     * validation messages
     * @return A list of validation results.
     */
    @Override
    public List<ValidationResult> approveTransaction(
            String transactionId, String approvedStatus, String requestType,
            boolean validateOnly, String languageCode) {
        List<ValidationResult> validationResult = new ArrayList<ValidationResult>();

        //Change the status of BA Units that are involved in a transaction directly
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYTRANSACTIONID);
        params.put(BaUnit.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        params.put("username", getUserName());
        List<BaUnitStatusChanger> baUnitList
                = getRepository().getEntityList(BaUnitStatusChanger.class, params);
        Date approvalDate = DateUtility.now();
        for (BaUnitStatusChanger baUnit : baUnitList) {
            validationResult.addAll(this.validateBaUnit(baUnit, languageCode));
            if (systemEJB.validationSucceeded(validationResult) && !validateOnly) {
                baUnit.setStatusCode(approvedStatus);
                baUnit.setTransactionId(transactionId);
                if (RegistrationStatusType.STATUS_CURRENT.equals(approvedStatus)
                        && baUnit.getFolioRegDate() == null) {
                    // Set the registration date for the property. 
                    baUnit.setFolioRegDate(approvalDate);
                } else if (RegistrationStatusType.STATUS_HISTORIC.equals(approvedStatus)
                        && baUnit.getCancellationDate() == null) {
                    // Set the cancellation date for the property. 
                    baUnit.setCancellationDate(approvalDate);
                }
                getRepository().saveEntity(baUnit);
            }
        }

        params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Rrr.QUERY_WHERE_BYTRANSACTIONID);
        params.put(Rrr.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        List<RrrStatusChanger> rrrStatusChangerList
                = getRepository().getEntityList(RrrStatusChanger.class, params);
        for (RrrStatusChanger rrr : rrrStatusChangerList) {
            validationResult.addAll(this.validateRrr(rrr, languageCode));
            if (systemEJB.validationSucceeded(validationResult) && !validateOnly) {
                rrr.setStatusCode(approvedStatus);
                if (RegistrationStatusType.STATUS_CURRENT.equals(approvedStatus)
                        && rrr.getRegistrationDate() == null) {
                    // Set the registration date for the rrr. 
                    rrr.setRegistrationDate(approvalDate);
                } else if (RegistrationStatusType.STATUS_HISTORIC.equals(approvedStatus)
                        && rrr.getExpirationDate() == null) {
                    // Set the cancellation date for the rrr. 
                    rrr.setExpirationDate(approvalDate);
                }
                getRepository().saveEntity(rrr);
            }
        }
        if (!validateOnly) {
            params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnitNotation.QUERY_WHERE_BYTRANSACTIONID);
            params.put(BaUnitNotation.QUERY_PARAMETER_TRANSACTIONID, transactionId);
            params.put("username", getUserName());
            params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BaUnitNotation.QUERY_ORDER_BY);

            List<BaUnitNotationStatusChanger> baUnitNotationList
                    = getRepository().getEntityList(BaUnitNotationStatusChanger.class, params);
            for (BaUnitNotationStatusChanger baUnitNotation : baUnitNotationList) {
                baUnitNotation.setStatusCode(RegistrationStatusType.STATUS_CURRENT);
                if (baUnitNotation.getNotationDate() == null) {
                    baUnitNotation.setNotationDate(approvalDate);
                }
                getRepository().saveEntity(baUnitNotation);
            }
        }
        if (!validateOnly) {
            // Additional processing for specific request types

            // Manage the Strata Title approval/cancellation process
            if (CREATE_STRATA_TITLE.equals(requestType)) {
                approveStrataProperties(transactionId, approvalDate);
            } else if (CANCEL_STRATA_TITLE.equals(requestType)) {
                reinstateDormantProperties(transactionId, approvalDate);
            } else if (CHANGE_ESTATE_TYPE.equals(requestType)
                    || PROCLAMATION_TYPE.equals(requestType)) { // Ticket #129
                changeEstateType(transactionId, approvalDate);
            }
        }
        return validationResult;
    }

    /**
     * Executes the business rules to validate the BA Unit.
     *
     * @param baUnit The BA Unit to validate
     * @param languageCode The language code to use for localization of any
     * validation messages
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
     * @param languageCode The language code to use for localization of any
     * validation messages
     * @return The list of validation results.
     */
    private List<ValidationResult> validateRrr(
            RrrStatusChanger rrr, String languageCode) {
        List<BrValidation> brValidationList = this.systemEJB.getBrForValidatingRrr(
                RegistrationStatusType.STATUS_CURRENT, rrr.getTypeCode());
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("id", rrr.getId());
        params.put("username", getUserName());
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
     * Returns all BA Units that have been created by the specified transaction
     *
     * @param transactionId The Transaction identifier
     */
    @Override
    public List<BaUnit> getBaUnitsCreatedByTransactionId(String transactionId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYTRANSACTIONID);
        params.put(BaUnit.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        return getRepository().getEntityList(BaUnit.class, params);
    }

    /**
     * Retrieves all administrative.ba_unit_rel_type code values.
     *
     * @param languageCode The language code to use for localization of display
     * values.
     * @return
     */
    @Override
    public List<BaUnitRelType> getBaUnitRelTypes(String languageCode) {
        return getRepository().getCodeList(BaUnitRelType.class, languageCode);
    }

    /**
     * Identifies a BA Unit as subject to cancellation / termination by linking
     * the BA Unit to a Transaction via the administrative.ba_unit_target
     * association. The BA Unit is not canceled / terminated until the
     * application canceling the BA Unit is approved.
     *
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
     * role.</p>
     *
     * @param baUnitId The identifier of the BA Unit to be canceled / terminated
     * @param serviceId The identifier of the service that is canceling /
     * terminating the BA Unit
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
     * Reverses the cancellation / termination of a BA Unit by removing the BA
     * Unit Target created by
     * {@linkplain #terminateBaUnit(java.lang.String, java.lang.String) terminateBaUnit}.
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
     * role.</p>
     *
     * @param baUnitId The identifier of the BA Unit to reverse the cancellation
     * for.
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
     * Retrieves the actions a specific user has performed against any
     * application during a specific period.
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

        return result;
    }

    /**
     * Creates a new BA Unit Area
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
     * role.</p>
     *
     * @param baUnitId The identifier of the area the BA Unit is being created
     * as part of
     * @param baUnitAreaTO The details of the BA Unit to create
     * @return The new BA Unit Area
     * @see #saveBaUnit(java.lang.String,
     * org.sola.services.ejb.administrative.repository.entities.BaUnitArea)
     * createBaUnit
     */
    @Override
//    @RolesAllowed(RolesConstants.ADMINISTRATIVE_BA_UNIT_SAVE)
    public BaUnitArea createBaUnitArea(String baUnitId, BaUnitArea baUnitArea) {
        if (baUnitArea == null) {
            return null;
        }
        return getRepository().saveEntity(baUnitArea);
    }

    /**
     * Locates a BA Unit and cadastre object's area size
     *
     *
     * @param id The BA Unit id
     * @param colist the list of cadastre object for the ba unit
     * @return The BA Unit matching the name
     */
    @Override
    public BaUnit getBaUnitWithCadObject(String nameFirstPart, String nameLastPart, String colist) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYPROPERTYCODE);
        params.put(BaUnit.QUERY_PARAMETER_FIRSTPART, nameFirstPart);
        params.put(BaUnit.QUERY_PARAMETER_LASTPART, nameLastPart);
        params.put(BaUnit.QUERY_PARAMETER_COLIST, colist);
        return getRepository().getEntity(BaUnit.class, params);
    }

    /**
     * Creates the Strata Properties required for a Unit Development. This
     * method can be used to create the initial set of properties based on a
     * UnitParcelGroup as well as to create any additional properties when the
     * Unit Parcel Group is changed.
     *
     * @param serviceId The identifier for the Create Unit Title Service
     * @param group The Unit Parcel Group
     * @param baUnitIds The list of BA Units representing the underlying
     * properties for the Unit Development (Typically the list of BA Units
     * linked to the application as Application Properties). This list of BA
     * Units is checked to ensure only valid properties are linked as the
     * underlying properties for the unit development.
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_STRATA_UNIT_CREATE)
    public void createStrataProperties(String serviceId, UnitParcelGroup group, List<String> baUnitIds) {
        if (group != null && group.getUnitParcelList() != null && group.getUnitParcelList().size() > 0) {

            TransactionUnitParcels trans = transactionEJB.getTransactionByServiceId(serviceId,
                    false, TransactionUnitParcels.class);

            String baUnitIdList = "";
            if (baUnitIds != null && baUnitIds.size() > 0) {
                for (String id : baUnitIds) {
                    baUnitIdList = baUnitIdList + id + ",";
                }
            }

            // This method was originally implemented at the EJB level, but due to database connection
            // issues, the logic has been transferred to a database stored proceedure to ensure
            // reliable execution. 
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_QUERY, AdministrativeSqlProvider.buildCreateStrataProperties());
            params.put(AdministrativeSqlProvider.PARAM_UNIT_PARCEL_GROUP_ID, group.getId());
            params.put(AdministrativeSqlProvider.PARAM_TRANSACTION_ID, trans.getId());
            params.put(AdministrativeSqlProvider.PARAM_CURRENT_USER, this.getUserName());
            params.put(AdministrativeSqlProvider.PARAM_BA_UNIT_ID_LIST, baUnitIdList);
            getRepository().bulkUpdate(params);
        }
    }

    /**
     * Determines the underlying properties for the Unit Development based on
     * the Unit Parcel Group as well as any additional list of BA Units.
     * Typically this additional list will be the list of Application Properties
     * from the Application.
     *
     * @param group The Unit Parcel Group
     * @param baUnitIds The list of additional BA Units (i.e. The Application
     * Properties)
     */
    private List<BaUnit> getUnderlyingProperties(UnitParcelGroup group, List<String> baUnitIds) {
        List<BaUnit> result = new ArrayList<BaUnit>();
        // Get all underlying properties based on the underlying parcels set in the Unit Parcel Group
        if (group.getParcelList() != null) {
            for (UnitParcel parcel : group.getParcelList()) {
                List<BaUnit> tmpList = getBaUnitBySpatialUnitId(parcel.getId());
                if (tmpList != null) {
                    for (BaUnit baUnit : tmpList) {
                        // Make sure the BA Unit is a current or dormant basic property unit that
                        // is not already part of the underlying parcels list
                        if ((RegistrationStatusType.STATUS_CURRENT.equals(baUnit.getStatusCode())
                                || RegistrationStatusType.STATUS_DORMANT.equals(baUnit.getStatusCode()))
                                && !BaUnitType.STRATA_UNIT_TYPE.equals(baUnit.getTypeCode())
                                && !result.contains(baUnit)) {
                            result.add(baUnit);
                        }
                    }
                }
            }
        }
        // Get all of the underlying properties based on the list of ba unit 
        // ids supplied (i.e. some BA Units may not be linked to parcels. 
        if (baUnitIds != null) {
            for (String baUnitId : baUnitIds) {
                BaUnit tmpBaUnit = getBaUnitById(baUnitId);
                if (tmpBaUnit != null && (RegistrationStatusType.STATUS_CURRENT.equals(tmpBaUnit.getStatusCode())
                        || RegistrationStatusType.STATUS_DORMANT.equals(tmpBaUnit.getStatusCode()))
                        && !BaUnitType.STRATA_UNIT_TYPE.equals(tmpBaUnit.getTypeCode())
                        && !result.contains(tmpBaUnit)) {
                    result.add(tmpBaUnit);
                }
            }
        }
        return result;
    }

    /**
     * Creates a Ba Unit Area entity using the information provided.
     *
     * @param baUnitId The BA Unit to link the area record to
     * @param size The size of the area. If the size is NULL, no BaUnitArea
     * entity will be created.
     * @param areaType THe type of area (official, calculated, etc)
     */
    private BaUnitArea createBaUnitArea(String baUnitId, BigDecimal size, String areaType) {
        BaUnitArea result = null;
        if (size != null) {
            BaUnitArea area = new BaUnitArea();
            area.setBaUnitId(baUnitId);
            area.setTypeCode(areaType);
            area.setSize(size);
            result = getRepository().saveEntity(area);
        }
        return result;
    }

    /**
     * Retrieves all of the BA Units associated to a specific Spatial Unit Id.
     * This includes any historic BA Units.
     *
     * @param spatialUnitId The identifier of the Spatial Unit
     * @return The BA Units linked to the spatial unit or null.
     */
    private List<BaUnit> getBaUnitBySpatialUnitId(String spatialUnitId) {
        List<BaUnit> result = null;
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BaUnit.QUERY_WHERE_BYBSPATIALUNITID);
        params.put(BaUnit.QUERY_PARAMETER_SPATIAL_UNIT_ID, spatialUnitId);
        result = getRepository().getEntityList(BaUnit.class, params);
        return result;
    }

    /**
     * Checks for the freehold properties and updates them to have a status of
     * Dormant.
     *
     * @param transactionId
     */
    private void approveStrataProperties(String transactionId, Date approvalDate) {
        TransactionUnitParcels trans = transactionEJB.getTransactionById(transactionId,
                TransactionUnitParcels.class);
        if (trans.getUnitParcelGroup() != null) {
            List<BaUnit> underlyingProps = getUnderlyingProperties(trans.getUnitParcelGroup(), null);
            for (BaUnit prop : underlyingProps) {
                // Ticket #68 - If property is freehold, set the property status to 
                // dormant and cancel all Rrrs
                if (isFreehold(prop.getId(), RegistrationStatusType.STATUS_CURRENT)) {
                    BaUnitStatusChanger baUnitToUpdate = new BaUnitStatusChanger();
                    baUnitToUpdate.setId(prop.getId());
                    getRepository().refreshEntity(baUnitToUpdate);
                    baUnitToUpdate.setStatusCode(RegistrationStatusType.STATUS_DORMANT);
                    getRepository().saveEntity(baUnitToUpdate);
                    if (prop.getRrrList() != null && prop.getRrrList().size() > 0) {
                        for (Rrr rrr : prop.getRrrList()) {
                            if (RegistrationStatusType.STATUS_CURRENT.equals(rrr.getStatusCode())) {
                                RrrStatusChanger rrrToUpdate = new RrrStatusChanger();
                                rrrToUpdate.setId(rrr.getId());
                                getRepository().refreshEntity(rrrToUpdate);
                                rrrToUpdate.setStatusCode(RegistrationStatusType.STATUS_HISTORIC);
                                rrr.setExpirationDate(approvalDate);
                                getRepository().saveEntity(rrrToUpdate);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Ticket #68. Determines if the underlying property of a unit development
     * is Freehold so that all rights on the property can be canceled (in the
     * case of approval) or reinstated with the ownership structure of the units
     * (when the unit development is canceled)
     *
     * @param baUnitId
     * @return true if the Property is freehold, false otherwise.
     */
    private boolean isFreehold(String baUnitId, String statusCode) {
        boolean result = false;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Rrr.QUERY_WHERE_ISFREEHOLD);
        params.put(CommonSqlProvider.PARAM_LIMIT_PART, "1");
        params.put(Rrr.QUERY_PARAMETER_BAUNITID, baUnitId);
        params.put(Rrr.QUERY_PARAMETER_STATUS, statusCode);
        Rrr r = getRepository().getEntity(Rrr.class, params);
        if (r != null) {
            result = true;
        }
        return result;
    }

    /**
     * Reinstates the Dormant properties that where underlying the strata
     * parcels when the Unit Development is canceled.
     *
     * If connection pool errors occur when executing this function, attempt to
     * increase the size of the Maximum Pool Size for the Connection Pool in
     * Glassfish to 100 or higher.
     *
     * @param transactionId Transaction Id
     * @param approvalDate Date the cancellation was approved
     */
    private void reinstateDormantProperties(String transactionId, Date approvalDate) {
        TransactionUnitParcels trans = transactionEJB.getTransactionById(transactionId,
                TransactionUnitParcels.class);
        if (trans.getUnitParcelGroup() != null) {
            List<BaUnit> underlyingProps = getUnderlyingProperties(trans.getUnitParcelGroup(), null);
            for (BaUnit prop : underlyingProps) {
                if (RegistrationStatusType.STATUS_DORMANT.equals(prop.getStatusCode())) {
                    // Determine the shares based on the unit entitlements
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put(CommonSqlProvider.PARAM_QUERY, AdministrativeSqlProvider.buildGetUnitEntitlementSql());
                    params.put(AdministrativeSqlProvider.PARAM_TRANSACTION_ID, transactionId);
                    List<UnitEntitlementShare> entitlementShares = getRepository().getEntityList(UnitEntitlementShare.class, params);
                    if (entitlementShares != null && entitlementShares.size() > 0) {

                        // Preprocess the entitlement shares in case some shares have the same party record. If there is shares
                        // that have the same party, then it is necesary to duplicate the party record so that the rrr_id, party_id
                        // constraint on administrative.rrr_for_party table is not broken. 
                        Map<String, List<UnitEntitlementShare>> partyShares = new HashMap<String, List<UnitEntitlementShare>>();
                        for (UnitEntitlementShare s : entitlementShares) {
                            if (!partyShares.containsKey(s.getPartyId())) {
                                partyShares.put(s.getPartyId(), new ArrayList<UnitEntitlementShare>());
                            }
                            partyShares.get(s.getPartyId()).add(s);
                        }

                        for (String key : partyShares.keySet()) {
                            if (partyShares.get(key).size() > 1) { // Need to create a clone of the party to avoid the rrr_for_party table constraint. 
                                Party p = partyEJB.getParty(key);
                                for (UnitEntitlementShare s : partyShares.get(key)) {
                                    p.makeCloneable();
                                    p.setId(null); // Force a new id to be assigned to the party
                                    p = partyEJB.saveParty(p);
                                    s.setPartyId(p.getId()); // Reset the party Id for the Entitlement Share. 
                                }
                            }
                        }

                        // Create a Freehold RRR with all unit owners having a share in the previously dormant property 
                        Rrr freeholdRrr = new Rrr();
                        freeholdRrr.getId(); // Allocate an id for the freehold Rrr. 
                        freeholdRrr.setTypeCode(RrrType.FREEHOLD_TYPE);
                        freeholdRrr.setPrimary(true);

                        BaUnitNotation note = new BaUnitNotation();
                        note.setNotationText("");
                        freeholdRrr.setNotation(note);

                        freeholdRrr.setRrrShareList(new ArrayList<RrrShare>());
                        String shareId = null;
                        RrrShare share = null;
                        for (UnitEntitlementShare entShare : entitlementShares) {
                            // Create the shares for the Freehold RRR based on the
                            // entitlements shares. Note that some shares may have multiple
                            // parties linked to them (undivided shares) so use the shareId to
                            // determine when a new share is required.
                            if (!entShare.getShareId().equals(shareId)) {
                                share = new RrrShare();
                                freeholdRrr.getRrrShareList().add(share);
                                shareId = entShare.getShareId();
                                Integer denominator = entShare.getDenominator() * entShare.getEntitlementTotal();
                                Integer nominator = entShare.getNominator() * entShare.getEntitlement();
                                if (denominator >= Short.MAX_VALUE) {
                                    // Rationalise the denominator and nominator so that the Short is not exceeded by
                                    // expressing the fraction out of 10000. Rounding can introduce errors into 
                                    // the fraction so only use this approach when the Short is exceeded. 
                                    BigDecimal n = new BigDecimal(nominator.toString());
                                    BigDecimal d = new BigDecimal(denominator.toString());
                                    BigDecimal r = n.multiply(new BigDecimal("10000").divide(d)).setScale(0, RoundingMode.HALF_UP);
                                    nominator = new Integer(r.toString());
                                    denominator = 10000;
                                }
                                share.setDenominator(new Short(denominator.toString()));
                                share.setNominator(new Short(nominator.toString()));
                            }
                            if (share != null) {
                                if (share.getRightHolderList() == null) {
                                    share.setRightHolderList(new ArrayList<Party>());
                                }
                                // Add the party to the share
                                share.getRightHolderList().add(partyEJB.getParty(entShare.getPartyId()));
                            }
                        }
                        prop.getRrrList().add(freeholdRrr);
                        LocalInfo.setTransactionId(transactionId);
                        getRepository().saveEntity(prop);

                        // Make the new Freehold RRR current. 
                        RrrStatusChanger fRrr = getRepository().getEntity(RrrStatusChanger.class, freeholdRrr.getId());
                        fRrr.setStatusCode(RegistrationStatusType.STATUS_CURRENT);
                        fRrr.setRegistrationDate(approvalDate);
                        getRepository().saveEntity(fRrr);
                    }
                }

                // Make the underlying property current again
                BaUnitStatusChanger baUnitToUpdate = new BaUnitStatusChanger();
                baUnitToUpdate.setId(prop.getId());
                getRepository().refreshEntity(baUnitToUpdate);
                baUnitToUpdate.setStatusCode(RegistrationStatusType.STATUS_CURRENT);
                getRepository().saveEntity(baUnitToUpdate);
            }
        }
    }

    /**
     * Ticket #92. Make the all other primary rights on the property historic if
     * a new primary right is created due to a Change Estate Type service.
     *
     * @param transactionId The id for the transaction that has created a new
     * primary right
     * @param approvalDate The datetime for the approval action.
     */
    private void changeEstateType(String transactionId, Date approvalDate) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Rrr.QUERY_WHERE_CHANGE_ESTATE);
        params.put(Rrr.QUERY_PARAMETER_TRANSACTIONID, transactionId);
        List<RrrStatusChanger> rrrStatusChangerList
                = getRepository().getEntityList(RrrStatusChanger.class, params);

        for (RrrStatusChanger rrr : rrrStatusChangerList) {
            if (RegistrationStatusType.STATUS_CURRENT.equals(rrr.getStatusCode())) {
                rrr.setStatusCode(RegistrationStatusType.STATUS_HISTORIC);
                // Set the cancellation date for the rrr. 
                rrr.setExpirationDate(approvalDate);
                getRepository().saveEntity(rrr);
            }
        }
    }

    /**
     * Saves an instance of a certificate print
     *
     * @return
     */
    public CertificatePrint saveCertificatePrint(CertificatePrint print) {
        return getRepository().saveEntity(print);
    }

    /**
     * Ticket #68. Processes a list of Strata BaUnits and marks them for
     * cancellation
     *
     * @param serviceId The service to link the cancellation to
     * @param group The Unit Parcel Group to associate with the cancel
     * transaction.
     * @param baUnitIds The list of BaUnits to cancel.
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_STRATA_UNIT_CREATE)
    public void terminateStrataProperties(String serviceId, UnitParcelGroup group, List<String> baUnitIds) {
        if (baUnitIds != null && baUnitIds.size() > 0) {
            // Link the transction to the unit parcel group
            TransactionUnitParcels trans = transactionEJB.getTransactionByServiceId(serviceId,
                    false, TransactionUnitParcels.class
            );

            String baUnitIdList = "";

            for (String id : baUnitIds) {
                baUnitIdList = baUnitIdList + id + ",";
            }

            // This method was originally implemented at the EJB level, but due to database connection
            // issues, the logic has been transferred to bulk insert statement
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(CommonSqlProvider.PARAM_QUERY, AdministrativeSqlProvider.buildTerminateStrataProperties());
            params.put(AdministrativeSqlProvider.PARAM_TRANSACTION_ID, trans.getId());
            params.put(AdministrativeSqlProvider.PARAM_CURRENT_USER,
                    this.getUserName());
            params.put(AdministrativeSqlProvider.PARAM_BA_UNIT_ID_LIST, baUnitIdList);

            getRepository()
                    .bulkUpdate(params);

            trans.setUnitParcelGroupId(group.getId());
            transactionEJB.saveEntity(trans);
        }
    }

    /**
     * Ticket #68. Reverses the cancellation flag for a list of Strata BaUnits
     *
     * @param serviceId The service to link the cancellation to
     * @param baUnitIds The list of BaUnits to undo the termination flag.
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_STRATA_UNIT_CREATE)
    public void undoTerminateStrataProperties(String serviceId, List<String> baUnitIds) {
        if (baUnitIds != null && baUnitIds.size() > 0) {

            TransactionUnitParcels trans = transactionEJB.getTransactionByServiceId(serviceId,
                    false, TransactionUnitParcels.class
            );

            String baUnitIdList = "";

            for (String id : baUnitIds) {
                baUnitIdList = baUnitIdList + id + ",";
            }

            // This method was originally implemented at the EJB level, but due to database connection
            // issues, the logic has been transferred to bulk delete statement
            Map<String, Object> params = new HashMap<String, Object>();

            params.put(CommonSqlProvider.PARAM_QUERY, AdministrativeSqlProvider.buildUndoTerminateStrataProperties());
            params.put(AdministrativeSqlProvider.PARAM_BA_UNIT_ID_LIST, baUnitIdList);

            params.put(AdministrativeSqlProvider.PARAM_TRANSACTION_ID, trans.getId());
            getRepository()
                    .bulkUpdate(params);

            // Unlink the transaction from the unit parcel group
            trans.setUnitParcelGroupId(
                    null);
            transactionEJB.saveEntity(trans);
        }
    }

    /**
     * Version 1911a. Update the status of this deed property from historic to
     * current.
     *
     * <p>
     * Requires the {@linkplain RolesConstants#ADMINISTRATIVE_MAKE_PROP_CURRENT}
     * role.</p>
     *
     * @param baUnitId - ID of the property to update the status of
     */
    @Override
    @RolesAllowed(RolesConstants.ADMINISTRATIVE_MAKE_PROP_CURRENT)
    public void makePropertyCurrent(String baUnitId) {
        BaUnitStatusChanger baUnit = getRepository().getEntity(BaUnitStatusChanger.class, baUnitId); 
        if (RegistrationStatusType.STATUS_HISTORIC.equals(baUnit.getStatusCode())) {
            baUnit.setStatusCode(RegistrationStatusType.STATUS_CURRENT);
            baUnit.markForSave();
            getRepository().saveEntity(baUnit);
        }
    }

}
