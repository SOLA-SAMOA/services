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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.services.ejb.system.businesslogic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.search.businesslogic.SearchEJBLocal;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.br.ResultFeedback;
import org.sola.services.ejb.system.repository.entities.BrCurrent;
import org.sola.services.ejb.system.repository.entities.BrValidation;

/**
 *
 * @author soladev
 */
@Stateless
@EJB(name = "java:global/SOLA/SystemEJBLocal", beanInterface = SystemEJBLocal.class)
public class SystemEJB extends AbstractEJB implements SystemEJBLocal {

    @EJB
    private SearchEJBLocal searchEJB;

    @Override
    public BigDecimal getTaxRate() {
        // Note that the String constructor is perferred for BigDecimal
        return new BigDecimal("0.075");
    }

    private BrCurrent getBr(String id, String languageCode) {
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

    @Override
    public List<BrValidation> getBrForValidatingApplication(String momentCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORAPPLICATION);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    @Override
    public List<BrValidation> getBrForValidatingBaUnit(String momentCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORBAUNIT);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

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

    @Override
    public List<BrValidation> getBrForValidatingRrr(String momentCode, String rrrType) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORRRR);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(BrValidation.QUERY_PARAMETER_RRRTYPE, rrrType);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    @Override
    public List<BrValidation> getBrForValidatingSource(String momentCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORSOURCE);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    @Override
    public List<BrValidation> getBrForValidatingCadastreObject(String momentCode) {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, BrValidation.QUERY_WHERE_FORCADASTREOBJECT);
        params.put(BrValidation.QUERY_PARAMETER_MOMENTCODE, momentCode);
        params.put(CommonSqlProvider.PARAM_ORDER_BY_PART, BrValidation.QUERY_ORDERBY_ORDEROFEXECUTION);
        return getRepository().getEntityList(BrValidation.class, params);
    }

    private Object checkRuleBasic(
            BrCurrent br, HashMap<String, Serializable> parameters) {
        Object ruleResult = null;
        try {
            if (br.getTechnicalTypeCode().equals("drools")) {
                //Here is supposed to come the code which runs the business rule using drools engine.
            } else if (br.getTechnicalTypeCode().equals("sql")) {
                String sqlStatement = br.getBody();
                Object[] params = null;
                if (parameters != null) {
                    Integer paramIndex = 1;
                    params = new Object[parameters.keySet().size()];
                    for (String paramName : parameters.keySet()) {
                        sqlStatement = sqlStatement.replace(
                                String.format("{%s}", paramName), String.format("?%s", paramIndex));
                        params[paramIndex - 1] = parameters.get(paramName);
                        paramIndex++;
                    }
                }
                ruleResult = searchEJB.getResultObjectFromStatement(sqlStatement, params);
            }
            return ruleResult;
        } catch (Exception ex) {
            throw new SOLAException(ServiceMessage.RULE_FAILED_EXECUTION, new Object[]{br.getId(), ex});
        }
    }

    @Override
    public Result checkRuleGetResultSingle(
            String brName, HashMap<String, Serializable> parameters) {
        BrCurrent br = this.getBr(brName, "en");
        Result result = new Result();
        result.setName(brName);
        Object rawResult = this.checkRuleBasic(br, parameters);
        result.setValue(rawResult);
        return result;
    }

    @Override
    public ResultFeedback checkRuleGetFeedback(
            String brName, String languageCode, HashMap<String, Serializable> parameters) {
        BrCurrent br = this.getBr(brName, languageCode);
        ResultFeedback result = new ResultFeedback();
        result.setName(brName);
        Object rawResult = this.checkRuleBasic(br, parameters);
        result.setValue(rawResult);
        result.setFeedback(br.getFeedback());
        return result;
    }

    @Override
    public List<ResultFeedback> checkRulesGetFeedback(
            List<String> brNameList, String languageCode,
            HashMap<String, Serializable> parameters) {

        List<ResultFeedback> result = new ArrayList<ResultFeedback>();
        for (String brName : brNameList) {
            result.add(this.checkRuleGetFeedback(brName, languageCode, parameters));

        }
        return result;
    }

    @Override
    public List<ValidationResult> checkRulesGetValidation(
            List<BrValidation> brListToValidate, String languageCode,
            HashMap<String, Serializable> parameters) {
        List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        for (BrValidation brForValidation : brListToValidate) {
            validationResultList.add(
                    this.checkRuleGetValidation(brForValidation, languageCode, parameters));
        }
        return validationResultList;
    }

    private ValidationResult checkRuleGetValidation(
            BrValidation brForValidation, String languageCode,
            HashMap<String, Serializable> parameters) {

        BrCurrent br = this.getBr(brForValidation.getBrId(), languageCode);
        ValidationResult result = new ValidationResult();
        result.setName(br.getId());
        Object rawResult = this.checkRuleBasic(br, parameters);
        // Result can be null for some checks, so default to True in these cases. 
        rawResult = rawResult == null ? Boolean.TRUE : rawResult; 
        result.setSuccessful(rawResult.equals(Boolean.TRUE));
        result.setFeedback(br.getFeedback());
        result.setSeverity(brForValidation.getSeverityCode());
        return result;
    }

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
