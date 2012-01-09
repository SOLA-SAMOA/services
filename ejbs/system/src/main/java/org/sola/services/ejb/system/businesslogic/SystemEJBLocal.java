/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
import java.util.HashMap;
import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.br.ResultFeedback;
import org.sola.services.ejb.system.repository.entities.Br;
import org.sola.services.ejb.system.repository.entities.BrReport;
import org.sola.services.ejb.system.repository.entities.BrValidation;

/**
 *
 * @author soladev
 */
@Local
public interface SystemEJBLocal extends AbstractEJBLocal {

    BigDecimal getTaxRate();
    
    Br getBr(String id, String lang);
    
    Br saveBr(Br br);
    
    List<BrReport> getBrs(List<String> ids);
    
    List<BrReport> getAllBrs();
    
    BrReport getBrReport(String id);
    
    List<BrValidation> getBrForValidatingApplication(String momentCode);

    List<BrValidation> getBrForValidatingService(String momentCode, String requestTypeCode);

    List<BrValidation> getBrForValidatingTransaction(
            String targetCode, String momentCode, String requestTypeCode);

    List<BrValidation> getBrForValidatingRrr(String momentCode, String rrrType);

//    ResultFeedback checkRuleGetFeedback(
//            String brName, String languageCode, HashMap<String, Serializable> parameters);
//
//    List<ResultFeedback> checkRulesGetFeedback(
//            List<String> brNameList, String languageCode,
//            HashMap<String, Serializable> parameters);

    Result checkRuleGetResultSingle(
            String brName, HashMap<String, Serializable> parameters);

    List<ValidationResult> checkRulesGetValidation(
            List<BrValidation> brListToValidate, String languageCode,
            HashMap<String, Serializable> parameters);

    boolean validationSucceeded(List<ValidationResult> validationResultList);
}