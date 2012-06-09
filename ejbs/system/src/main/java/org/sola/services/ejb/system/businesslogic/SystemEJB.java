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
package org.sola.services.ejb.system.businesslogic;

import java.io.Serializable;
import java.math.BigDecimal;
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
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.search.businesslogic.SearchEJBLocal;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.repository.entities.Br;
import org.sola.services.ejb.system.repository.entities.BrCurrent;
import org.sola.services.ejb.system.repository.entities.BrReport;
import org.sola.services.ejb.system.repository.entities.BrValidation;

/**
 * System EJB - Provides access to SOLA System data including business rules
 */
@Stateless
@EJB(name = "java:global/SOLA/SystemEJBLocal", beanInterface = SystemEJBLocal.class)
public class SystemEJB extends AbstractEJB implements SystemEJBLocal {

    @EJB
    private SearchEJBLocal searchEJB;

    /**
     * Sets the entity package for the EJB to Br.class.getPackage().getName(). This is used to
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
        setEntityPackage(Br.class.getPackage().getName());
    }

    /**
     * Returns the tax rate applicable for financial calculations.
     */
    @Override
    public BigDecimal getTaxRate() {
        // Note that the String constructor is perferred for BigDecimal
        return new BigDecimal("0.075");
    }

    /**
     * Returns the SOLA business rule matching the id.
     *
     * <p>Requires the {@linkplain RolesConstants.ADMIN_MANAGE_SECURITY} role.</p>
     *
     * @param id Identifier for the business rule to return
     * @param lang The language code to use to localize the display value for each Br.
     *
     */
    @RolesAllowed(RolesConstants.ADMIN_MANAGE_SECURITY)
    @Override
    public Br getBr(String id, String lang) {
        if (lang == null) {
            return getRepository().getEntity(Br.class, id);
        } else {
            Map params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_LANGUAGE_CODE, lang);
            return getRepository().getEntity(Br.class, id, lang);
        }
    }

    /**
     * Can be used to create a new business rule or save any updates to the details of an existing
     * business role.
     *
     * <p> Requires the {@linkplain RolesConstants.ADMIN_MANAGE_SECURITY} role. </p>
     *
     * @param br The business rule to save.
     * @return The updated/new business rule.
     */
    @RolesAllowed(RolesConstants.ADMIN_MANAGE_SECURITY)
    @Override
    public Br saveBr(Br br) {
        return getRepository().saveEntity(br);
    }

    /**
     * Retrieves the br specified by the id from the system.br_current view. The view lists all br's
     * that are currently active.
     *
     * @param id The identifier of the br to retrieve.
     * @param languageCode The language code to localize the display values and validation messages
     * for the business rule.
     * @throws SOLAException If the business rule is not found
     */
    private BrCurrent getBrCurrent(String id, String languageCode) {
        BrCurrent result = null;
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_LANGUAGE_CODE, languageCode);
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrCurrent.QUERY_WHERE_BYID);
        params.put(BrCurrent.QUERY_PARAMETER_ID, id);
        result = getRepository().getEntity(BrCurrent.class, params);
        if (result == null) {
            throw new SOLAException(ServiceMessage.RULE_NOT_FOUND, new Object[]{id});
        }
        return result;
    }

    /**
     * Returns the Br Report for the specified business rule.
     *
     * @param id Identifer of the business rule to retrieve the report for.
     */
    @Override
    public BrReport getBrReport(String id) {
        Map params = new HashMap<String, Object>();
        return getRepository().getEntity(BrReport.class, id);
    }

    /**
     * Returns a list of business rules matching the supplied ids.
     *
     * <p>No role is required to execute this method.</p>
     *
     * @param ids The list of business rule ids
     */
    @Override
    public List<BrReport> getBrs(List<String> ids) {
        Map params = new HashMap<String, Object>();
        return getRepository().getEntityListByIds(BrReport.class, ids);
    }

    /**
     * Returns a br report for every business rule in the system.br table.
     *
     * <p>No role is required to execute this method.</p>
     */
    @Override
    public List<BrReport> getAllBrs() {
        return getRepository().getEntityList(BrReport.class);
    }

    /**
     * Retrieves the business rules required validate an application for the specified momentCode.
     * Business rules are returned in the order indicated by the
     * system.br_validation.order_of_execution.
     *
     * @param momentCode The code indicating the action being applied to the application. Used to
     * obtain the subset of application business rules that apply for a specific action. Must be
     * <code>validate</code> or
     * <code>approve</code>.
     */
    @Override
    public List<BrValidation> getBrForValidatingApplication(String momentCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORAPPLICATION);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    /**
     * Retrieves the business rules required validate services for the specified momentCode.
     * Business rules are returned in the order indicated by the
     * system.br_validation.order_of_execution.
     *
     * @param momentCode The code indicating the action being applied to the service. Used to obtain
     * the subset of service business rules that apply for a specific action. Must be
     * <code>start</code> or
     * <code>complete</code>.
     * @param requestTypeCode The type of service being validated. Allows services of different
     * types to have different business rules applied.
     */
    @Override
    public List<BrValidation> getBrForValidatingService(
            String momentCode, String requestTypeCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORSERVICE);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(BrValidation.QUERY_PARAMETER_REQUESTTYPE, requestTypeCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    /**
     * Retrieves the business rules required validate rrr for the specified momentCode. Business
     * rules are returned in the order indicated by the system.br_validation.order_of_execution.
     *
     * @param momentCode The code indicating the action being applied to the rrr. Used to obtain the
     * subset of rrr business rules that apply for a specific action. Must be
     * <code>current</code> or
     * <code>pending</code>.
     * @param rrrType The type of rrr being validated. Allows rrr of different types to have
     * different business rules applied
     */
    @Override
    public List<BrValidation> getBrForValidatingRrr(String momentCode, String rrrType) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORRRR);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(BrValidation.QUERY_PARAMETER_RRRTYPE, rrrType);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    /**
     * Retrieves the business rules required validate a transaction. Business rules are returned in
     * the order indicated by the system.br_validation.order_of_execution.
     *
     * @param targetCode The target to validate. Must be one of
     * <code>application</code>,
     * <code>service</code>,
     * <code>source</code>,
     * <code>ba_unit</code>,
     * <code>rrr</code> or
     * <code>cadastre_object</code>.
     * @param momentCode The code indicating the action being applied to the transaction. Used to
     * obtain the subset of business rules that apply for a specific action. Must be
     * <code>current</code> or
     * <code>pending</code>.
     * @param requestTypeCode The type of service being validated associated with the transaction.
     */
    @Override
    public List<BrValidation> getBrForValidatingTransaction(
            String targetCode, String momentCode, String requestTypeCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FOR_TRANSACTION);
        params.put(BrValidation.QUERY_PARAMETER_TARGETCODE, targetCode);
        params.put(BrValidation.QUERY_PARAMETER_REQUESTTYPE, requestTypeCode);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    /**
     * Executes the rule using the appropriate rules engine. Currently only SQL rules are supported,
     * but JBOSS Drools rules could be supported in future.
     *
     * @param br The business rule to execute
     * @param parameters The parameters the business rule operates on
     * @return Hashmap containing the rule results
     * @throws SOLAException If execution of the rule fails.
     * @see
     * org.sola.services.ejb.search.businesslogic.SearchEJB#getResultObjectFromStatement(java.lang.String,
     * java.util.Map) SearchEJB.getResultObjectFromStatement
     */
    private HashMap checkRuleBasic(
            BrCurrent br, HashMap<String, Serializable> parameters) {
        HashMap ruleResult = null;
        try {
//            if (br.getTechnicalTypeCode().equals("drools")) {
//                //Here is supposed to come the code which runs the business rule using drools engine.
//            } 
            if (br.getTechnicalTypeCode().equals("sql")) {
                String sqlStatement = br.getBody();
                ruleResult = searchEJB.getResultObjectFromStatement(sqlStatement, parameters);
                if (ruleResult == null) {
                    ruleResult = new HashMap();
                }
                if (!ruleResult.containsKey(Result.VALUE_FIELD_NAME)) {
                    ruleResult.put(Result.VALUE_FIELD_NAME, null);
                }
            }
            return ruleResult;
        } catch (Exception ex) {
            throw new SOLAException(ServiceMessage.RULE_FAILED_EXECUTION, new Object[]{br.getId(), ex});
        }
    }

    /**
     * Executes a business rule an returns a single value as the result.
     *
     * @param brName The name of the business rule to execute.
     * @param parameters The parameters for the business rule.
     * @see #getBrCurrent(java.lang.String, java.lang.String) getBrCurrent
     * @see #checkRuleBasic(org.sola.services.ejb.system.repository.entities.BrCurrent,
     * java.util.HashMap) checkRuleBasic
     */
    @Override
    public Result checkRuleGetResultSingle(
            String brName, HashMap<String, Serializable> parameters) {
        BrCurrent br = this.getBrCurrent(brName, "en");
        Result result = new Result();
        result.setName(brName);
        HashMap rawResult = this.checkRuleBasic(br, parameters);
        result.setValue(rawResult.get(Result.VALUE_FIELD_NAME));
        return result;
    }

    /**
     * Executes a set of business rules and returns the validation messages resulting from the
     * validation
     *
     * @param brListToValidate The list of business rules to execute
     * @param languageCode The language code to use to localize the validation messages
     * @param parameters The parameters for the business rules
     * @return The list of validation messages.
     * @see #checkRuleGetValidation(org.sola.services.ejb.system.repository.entities.BrValidation,
     * java.lang.String, java.util.HashMap) checkRuleGetValidation
     */
    @Override
    public List<ValidationResult> checkRulesGetValidation(
            List<BrValidation> brListToValidate, String languageCode,
            HashMap<String, Serializable> parameters) {
        List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        if (brListToValidate != null) {
            for (BrValidation brForValidation : brListToValidate) {
                validationResultList.add(
                        this.checkRuleGetValidation(brForValidation, languageCode, parameters));
            }
        }
        return validationResultList;
    }

    /**
     * Obtains the current definition for the rule to execute from the database, executes the rule
     * and returns the results in the form of validation result feedback.
     *
     * @param brForValidation The business rule to load and execute
     * @param languageCode The locale to use for retrieving the rule feedback messages
     * @param parameters Parameters the business rule operates on
     * @return The feedback messages obtained from executing the business rules.
     */
    private ValidationResult checkRuleGetValidation(
            BrValidation brForValidation, String languageCode,
            HashMap<String, Serializable> parameters) {

        BrCurrent br = this.getBrCurrent(brForValidation.getBrId(), languageCode);
        ValidationResult result = new ValidationResult();
        result.setName(br.getId());
        HashMap rawResult = this.checkRuleBasic(br, parameters);
        // Result can be null for some checks, so default to True in these cases. 
        if (rawResult.get(Result.VALUE_FIELD_NAME) == null) {
            rawResult.put(Result.VALUE_FIELD_NAME, Boolean.TRUE);
        }
        result.setSuccessful(rawResult.get(Result.VALUE_FIELD_NAME).equals(Boolean.TRUE));
        //Replace parameters if they exist
        String feedback = br.getFeedback();
        for (Object keyObj : rawResult.keySet()) {
            if (keyObj.equals(Result.VALUE_FIELD_NAME)) {
                continue;
            }
            feedback = feedback.replace(keyObj.toString(), rawResult.get(keyObj).toString());
        }
        result.setFeedback(feedback);
        result.setSeverity(brForValidation.getSeverityCode());
        return result;
    }

    /**
     * Checks all validation messages to determine if the validation succeeded or not. The
     * validation fails if any {@linkplain BrValidation#SEVERITY_CRITICAL critical} business rule
     * fails.
     *
     * @param validationResultList The list of validations to check.
     * @return
     * <code>false</code> if at least one critical validation fails,
     * <code>true</code> otherwise.
     */
    @Override
    public boolean validationSucceeded(List<ValidationResult> validationResultList) {
        for (ValidationResult validationResult : validationResultList) {
            if (validationResult.getSeverity().equals(BrValidation.SEVERITY_CRITICAL)
                    && !validationResult.isSuccessful()) {
                return false;
            }
        }
        return true;
    }
}
