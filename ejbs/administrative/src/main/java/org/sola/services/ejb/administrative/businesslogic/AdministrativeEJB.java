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
package org.sola.services.ejb.administrative.businesslogic;

import java.io.Serializable;
import java.math.BigDecimal;
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
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
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
     * BA Unit like null null null null null null null null null null     {@linkplain #createBaUnit(java.lang.String, org.sola.services.ejb.administrative.repository.entities.BaUnit)
     * createBaUnit}. Will also create a new Transaction record for the BA Unit
     * if the Service is not already associated to a Transaction.
     *
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
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
     * Applies the appropriate approval action to every BA Unit that is
     * associated to the specified transaction. This includes updating the
     * status of RRR and Notations associated with the BA Unit. <p>Can also be
     * used to test the outcome of the approval using the validateOnly flag.</p>
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
        List<BaUnitStatusChanger> baUnitList =
                getRepository().getEntityList(BaUnitStatusChanger.class, params);
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
        params.put("username", getUserName());
        List<RrrStatusChanger> rrrStatusChangerList =
                getRepository().getEntityList(RrrStatusChanger.class, params);
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

            List<BaUnitNotationStatusChanger> baUnitNotationList =
                    getRepository().getEntityList(BaUnitNotationStatusChanger.class, params);
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
                approveStrataProperties(transactionId);
            } else if (CANCEL_STRATA_TITLE.equals(requestType)) {
                cancelStrataProperties(transactionId);
            } else if (CHANGE_ESTATE_TYPE.equals(requestType)) {
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
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
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
     * <p>Requires the {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE}
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
     * Creates a new BA Unit Area <p>Requires the
     * {@linkplain RolesConstants#ADMINISTRATIVE_BA_UNIT_SAVE} role.</p>
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

            boolean updateTransaction = false;
            List<BaUnit> underlyingProperties = null;
            List<String> underlyingRrrIds = null;

            // Check the Common Property has an associated BA Unit. If not, create the Common Property
            BaUnit commonProperty = null;
            for (UnitParcel unitParcel : group.getUnitParcelList()) {
                if (CadastreObjectType.CODE_COMMON_PROPERTY.equals(unitParcel.getTypeCode())) {
                    commonProperty = getBaUnitByCode(unitParcel.getNameFirstpart(), unitParcel.getNameLastpart());
                    if (commonProperty == null) {
                        underlyingProperties = getUnderlyingProperties(group, baUnitIds);
                        underlyingRrrIds = getUnderlyingRrrIds(underlyingProperties);
                        String estateType = getUnderlyingEstateType(underlyingRrrIds);
                        commonProperty = createCommonProperty(unitParcel, estateType, underlyingProperties);
                        BigDecimal officialArea = commonProperty.getCalculatedAreaSize();
                        commonProperty = saveBaUnit(serviceId, commonProperty);
                        createBaUnitArea(commonProperty.getId(), officialArea, SpatialValueArea.OFFICIAL_AREA_TYPE);
                        updateTransaction = true;
                    } else {
                        // The common Property exists. Include the prior title references from the common
                        // property when determining the underlying properties. 
                        if (baUnitIds == null) {
                            baUnitIds = new ArrayList<String>();
                        }
                        if (commonProperty.getParentBaUnits() != null) {
                            for (ParentBaUnitInfo priorTitle : commonProperty.getParentBaUnits()) {
                                if (BaUnitRelType.PRIOR_TITLE_TYPE.equals(priorTitle.getRelationCode())) {
                                    baUnitIds.add(priorTitle.getRelatedBaUnitId());
                                }
                            }
                        }
                    }
                    break;
                }
            }

            for (UnitParcel unitParcel : group.getUnitParcelList()) {
                if (CadastreObjectType.CODE_PRINCIPAL_UNIT.equals(unitParcel.getTypeCode())) {
                    // Check for a BA Unit that matches the principal unit parcel appellation
                    BaUnit unit = getBaUnitByCode(unitParcel.getNameFirstpart(), unitParcel.getNameLastpart());
                    if (unit == null) {
                        // Clone the RRRs from the underlying properties and add them to the
                        // new Principal Unit. 
                        if (underlyingProperties == null) {
                            underlyingProperties = getUnderlyingProperties(group, baUnitIds);
                        }
                        if (underlyingRrrIds == null) {
                            underlyingRrrIds = getUnderlyingRrrIds(underlyingProperties);
                        }
                        List<Rrr> rrrList = cloneRrrs(underlyingRrrIds);

                        // Create the principal Unit
                        unit = createPrincipalUnit(unitParcel, commonProperty, rrrList);
                        BigDecimal officialArea = unit.getCalculatedAreaSize();
                        unit = saveBaUnit(serviceId, unit);
                        createBaUnitArea(unit.getId(), officialArea, SpatialValueArea.OFFICIAL_AREA_TYPE);
                        updateTransaction = true;
                    }
                }

                // Update the new transaction to reference the unit parcel group
                if (updateTransaction) {
                    TransactionUnitParcels trans = transactionEJB.getTransactionByServiceId(serviceId,
                            false, TransactionUnitParcels.class);
                    trans.setUnitParcelGroupId(group.getId());
                    transactionEJB.saveEntity(trans);
                }
            }
        }
    }

    /**
     * Creates the Common Property for a new Unit Development. This includes
     * creating a Body Corporate Rules RRR as well as a Address for Service RRR.
     *
     * @param commonPropParcel The Common Property Parcel to link to the Common
     * Property
     * @param estateType The estate type for the Common Property
     * @param underlyingProperties The list of underlying properties for the
     * Unit Development. These properties are linked to the Common Property as
     * Prior Titles.
     */
    private BaUnit createCommonProperty(UnitParcel commonPropParcel, String estateType,
            List<BaUnit> underlyingProperties) {
        BaUnit result = createStrataUnit(commonPropParcel);

        if (underlyingProperties != null && underlyingProperties.size() > 0) {
            List<ParentBaUnitInfo> parentList = new ArrayList<ParentBaUnitInfo>();
            // Setup a prior title reference from the common Property to the underlying properties
            for (BaUnit underlyingProp : underlyingProperties) {
                ParentBaUnitInfo priorTitle = new ParentBaUnitInfo();
                priorTitle.setRelatedBaUnitId(underlyingProp.getId());
                priorTitle.setRelationCode(BaUnitRelType.PRIOR_TITLE_TYPE);
                parentList.add(priorTitle);
            }

            // Find the village linked to the first underlying parcel and associate that
            // to the common property as the village
            if (underlyingProperties.get(0).getParentBaUnits() != null) {
                for (ParentBaUnitInfo parentProp : underlyingProperties.get(0).getParentBaUnits()) {
                    if (BaUnitRelType.VILLAGE_TYPE.equals(parentProp.getRelationCode())) {
                        ParentBaUnitInfo village = new ParentBaUnitInfo();
                        village.setRelatedBaUnitId(parentProp.getRelatedBaUnitId());
                        village.setRelationCode(parentProp.getRelationCode());
                        parentList.add(village);
                        break;
                    }
                }
            }
            result.setParentBaUnits(parentList);
        }

        // Add the Body Corporate Rules and Address for Service RRRs
        List<Rrr> rrrList = new ArrayList<Rrr>();

        // Create the primary Rrr for the Common Property in the name of the Body Corporate
        Rrr estateRrr = new Rrr();
        estateRrr.setTypeCode(estateType);
        BaUnitNotation note = new BaUnitNotation();
        note.setNotationText("Body Corporate of " + commonPropParcel.getNameLastpart());
        Party party = new Party();
        party.setName(note.getNotationText());
        party.setTypeCode(Party.TYPE_CODE_NON_NATURAL_PERSON);
        RrrShare share = new RrrShare();
        share.setRightHolderList(new ArrayList<Party>());
        share.getRightHolderList().add(party);
        //Save the new party record as it will not be created by the Ba Unit Save. 
        saveParties(share.getRightHolderList());
        share.setDenominator(new Short("1"));
        share.setNominator(new Short("1"));
        estateRrr.setRrrShareList(new ArrayList<RrrShare>());
        estateRrr.getRrrShareList().add(share);
        estateRrr.setPrimary(true);
        estateRrr.setNotation(note);
        rrrList.add(estateRrr);

        Rrr bodyCorpRules = new Rrr();
        bodyCorpRules.setTypeCode(RrrType.BODY_CORPORATE_RULES_TYPE);
        BaUnitNotation note1 = new BaUnitNotation();
        note1.setNotationText("Body Corporate Rules");
        bodyCorpRules.setNotation(note1);
        rrrList.add(bodyCorpRules);

        Rrr serviceAddress = new Rrr();
        serviceAddress.setTypeCode(RrrType.ADDRESS_FOR_SERVICE_TYPE);
        BaUnitNotation note2 = new BaUnitNotation();
        note2.setNotationText("Address for Service <address>");
        serviceAddress.setNotation(note2);
        rrrList.add(serviceAddress);

        result.setRrrList(rrrList);

        return result;
    }

    /**
     * Creates a new Principal Unit based on the Unit Parcel and common property
     * details. Also adds an Unit Entitlement RRR to the new Principal Unit.
     *
     * @param parcel The Principal Unit parcel to create the Principal Unit
     * property for
     * @param commonProperty The Common Property the Unit Development
     * @param rrrList The list of Rrrs from the underlying properties that need
     * to be added to the new principal unit.
     */
    private BaUnit createPrincipalUnit(UnitParcel parcel, BaUnit commonProperty, List<Rrr> rrrList) {
        BaUnit result = createStrataUnit(parcel);
        if (commonProperty != null) {

            // Link the commonProperty to the new principal unit
            ParentBaUnitInfo commProp = new ParentBaUnitInfo();
            commProp.setRelatedBaUnitId(commonProperty.getId());
            commProp.setRelationCode(BaUnitRelType.COMMON_PROPERTY_TYPE);
            List<ParentBaUnitInfo> parentList = new ArrayList<ParentBaUnitInfo>();
            parentList.add(commProp);

            // Link the village of the common property to the new principal unit
            if (commonProperty.getParentBaUnits() != null) {
                for (ParentBaUnitInfo parentProp : commonProperty.getParentBaUnits()) {
                    if (BaUnitRelType.VILLAGE_TYPE.equals(parentProp.getRelationCode())) {
                        ParentBaUnitInfo village = new ParentBaUnitInfo();
                        village.setRelatedBaUnitId(parentProp.getRelatedBaUnitId());
                        village.setRelationCode(parentProp.getRelationCode());
                        parentList.add(village);
                        break;
                    }
                }
            }
            result.setParentBaUnits(parentList);

            if (rrrList == null) {
                rrrList = new ArrayList<Rrr>();
            }
            // Add an entitlement RRR to the Principal Unit
            Rrr entitlement = new Rrr();
            entitlement.setTypeCode(RrrType.UNIT_ENTITLEMENT_TYPE);
            BaUnitNotation note = new BaUnitNotation();
            note.setNotationText("Unit entitlement <entitlement>");
            entitlement.setNotation(note);
            rrrList.add(entitlement);

            result.setRrrList(rrrList);
        }
        return result;
    }

    /**
     * Creates a BA Unit representing a Strata (a.k.a. Unit Title) property
     * (e.g. Principal Unit or Common Property) based on the details included in
     * the UnitParcel.
     *
     * @param parcel The Unit Parcel to create the Strata Unit Property for.
     */
    private BaUnit createStrataUnit(UnitParcel parcel) {
        BaUnit result = new BaUnit();
        result.setNameFirstpart(parcel.getNameFirstpart());
        result.setNameLastpart(parcel.getNameLastpart());
        result.setName(parcel.getNameFirstpart() + "/" + parcel.getNameLastpart());
        result.setTypeCode(BaUnitType.STRATA_UNIT_TYPE);

        // Link the unit parcel to the property
        List<CadastreObject> unitParcels = new ArrayList<CadastreObject>();
        unitParcels.add(parcel);
        result.setCadastreObjectList(unitParcels);

        BigDecimal officalArea = BigDecimal.ZERO;

        // Set the official area for the new unit based on the official area(s) for the parcel. 
        if (parcel.getSpatialValueAreaList() != null && parcel.getSpatialValueAreaList().size() > 0) {
            for (SpatialValueArea area : parcel.getSpatialValueAreaList()) {
                if (SpatialValueArea.OFFICIAL_AREA_TYPE.equals(area.getTypeCode())) {
                    if (area.getSize() != null) {
                        officalArea = officalArea.add(area.getSize());
                    }
                }
            }
        }

        if (officalArea.compareTo(BigDecimal.ZERO) > 0) {
            // Tempoarially store the official area in the calcualted area field. 
            result.setCalculatedAreaSize(officalArea);
        }
        return result;
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

    private List<String> getUnderlyingRrrIds(List<BaUnit> underlyingParcels) {
        List<String> result = new ArrayList<String>();
        boolean hasPrimaryRrr = false;
        for (BaUnit underlyingProp : underlyingParcels) {
            if (underlyingProp.getRrrList() != null && underlyingProp.getRrrList().size() > 0) {
                for (Rrr rrr : underlyingProp.getRrrList()) {
                    if (RegistrationStatusType.STATUS_CURRENT.equals(rrr.getStatusCode())) {
                        if (rrr.isPrimary() && !hasPrimaryRrr) {
                            // Only add one primary RRR to the Principal Units
                            hasPrimaryRrr = true;
                            result.add(rrr.getId());
                        }
                        if (!rrr.isPrimary()) {
                            result.add(rrr.getId());
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Determines the estate type of the underlying parcel. For Strata
     * Properties, this should either be Freehold or LeaseHold.
     *
     * @param rrrIdList The list of RRRs to check. .
     */
    private String getUnderlyingEstateType(List<String> rrrIdList) {
        String result = RrrType.FREEHOLD_TYPE;
        for (String rrrId : rrrIdList) {
            Rrr rrr = getRepository().getEntity(Rrr.class, rrrId);
            if (rrr != null && rrr.isPrimary()) {
                // Don't check any more Rrrs as the primary right has been found
                result = rrr.getTypeCode();
                break;
            }
        }
        return result;
    }

    /**
     * Generates clone copy of all Rrrs based on the list of Rrr ids provided.
     *
     * @param rrrIdList
     * @return
     */
    private List<Rrr> cloneRrrs(List<String> rrrIdList) {
        List<Rrr> result = new ArrayList<Rrr>();
        for (String rrrId : rrrIdList) {
            Rrr rrr = getRepository().getEntity(Rrr.class, rrrId);
            if (rrr != null) {
                rrr.makeCloneable();
                // Parties are readonly on the RRR, so it is necessary to iterate over each 
                // party and explicitly save the cloned parties
                if (rrr.getRrrShareList() != null && rrr.getRrrShareList().size() > 0) {
                    for (RrrShare share : rrr.getRrrShareList()) {
                        saveParties(share.getRightHolderList());
                    }
                    // The Rrr Rightholders List also lists the parties from the shares, so 
                    // clear this list to avoid trying to insert duplicates. 
                    rrr.setRightHolderList(null);
                } else {
                    // No shares for the RRR, but there might be right holders linked directly
                    // to the RRR
                    saveParties(rrr.getRightHolderList());
                }
                result.add(rrr);
            }
        }
        return result;
    }

    /**
     * Creates new parties for cloned Rrr's.
     *
     * @param parties
     */
    private void saveParties(List<Party> parties) {
        if (parties != null && parties.size() > 0) {
            ListIterator<Party> it = parties.listIterator();
            while (it.hasNext()) {
                Party party = it.next();
                it.remove();
                party = partyEJB.saveParty(party);
                it.add(party);
            }
        }
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
    private void approveStrataProperties(String transactionId) {
        TransactionUnitParcels trans = transactionEJB.getTransactionById(transactionId,
                TransactionUnitParcels.class);
        if (trans.getUnitParcelGroup() != null) {
            List<BaUnit> underlyingProps = getUnderlyingProperties(trans.getUnitParcelGroup(), null);
            for (BaUnit prop : underlyingProps) {
                if (prop.getRrrList() != null && prop.getRrrList().size() > 0) {
                    for (Rrr rrr : prop.getRrrList()) {
                        if (RegistrationStatusType.STATUS_CURRENT.equals(rrr.getStatusCode())
                                && rrr.isPrimary()
                                && RrrType.FREEHOLD_TYPE.equals(rrr.getTypeCode())) {
                            BaUnitStatusChanger baUnitToUpdate = new BaUnitStatusChanger();
                            baUnitToUpdate.setId(prop.getId());
                            getRepository().refreshEntity(baUnitToUpdate);
                            baUnitToUpdate.setStatusCode(RegistrationStatusType.STATUS_DORMANT);
                            getRepository().saveEntity(baUnitToUpdate);
                        }
                    }
                }
            }
        }
    }

    private void cancelStrataProperties(String transactionId) {
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
        params.put("username", getUserName());
        List<RrrStatusChanger> rrrStatusChangerList =
                getRepository().getEntityList(RrrStatusChanger.class, params);

        for (RrrStatusChanger rrr : rrrStatusChangerList) {
            if (!RegistrationStatusType.STATUS_HISTORIC.equals(rrr.getStatusCode())) {
                rrr.setStatusCode(RegistrationStatusType.STATUS_HISTORIC);
                // Set the cancellation date for the rrr. 
                rrr.setExpirationDate(approvalDate);
                getRepository().saveEntity(rrr);
            }
        }
    }
    
    /**
     * Saves an instance of a certificate print
     * @return 
     */
    public CertificatePrint saveCertificatePrint(CertificatePrint print) {
        return getRepository().saveEntity(print);
    }
}
