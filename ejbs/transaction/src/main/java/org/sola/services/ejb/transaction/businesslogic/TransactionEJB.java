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
package org.sola.services.ejb.transaction.businesslogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.DateUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.faults.SOLAValidationException;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.cadastre.businesslogic.CadastreEJBLocal;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectStatusChanger;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;
import org.sola.services.ejb.transaction.repository.entities.TransactionStatusChanger;
import org.sola.services.ejb.transaction.repository.entities.TransactionStatusType;
import org.sola.services.ejb.transaction.repository.entities.TransactionType;

/**
 *
 */
@Stateless
@EJB(name = "java:global/SOLA/TransactionEJBLocal", beanInterface = TransactionEJBLocal.class)
public class TransactionEJB extends AbstractEJB implements TransactionEJBLocal {

    @EJB
    SystemEJBLocal systemEJB;
    @EJB
    CadastreEJBLocal cadastreEJB;

    @Override
    protected void postConstruct() {
        setEntityPackage(TransactionBasic.class.getPackage().getName());
    }

    @Override
    public <T extends TransactionBasic> T getTransactionByServiceId(
            String serviceId,
            boolean createIfNotFound,
            Class<T> transactionClass) {
        T transaction = null;
        if (serviceId != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, TransactionBasic.QUERY_WHERE_BYFROMSERVICEID);
            params.put("serviceId", serviceId);
            transaction = getRepository().getEntity(transactionClass, params);
        }
        if (createIfNotFound && transaction == null) {
            transaction = this.createTransaction(serviceId, transactionClass);
        }
        return transaction;
    }

    @Override
    public <T extends TransactionBasic> T createTransaction(String serviceId, Class<T> transactionClass) {
        T transaction = null;
        try {
            transaction = transactionClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Transaction cannot be created.", ex);
        }
        transaction.getId();
        transaction.setFromServiceId(serviceId);
        return getRepository().saveEntity(transaction);
    }

    @Override
    public <T extends TransactionBasic> T getTransactionById(String id, Class<T> transactionClass) {
        return getRepository().getEntity(transactionClass, id);
    }

    @Override
    public boolean changeTransactionStatusFromService(
            String serviceId, String statusCode) {
        TransactionStatusChanger transaction =
                this.getTransactionByServiceId(serviceId, false, TransactionStatusChanger.class);
        if (transaction == null) {
            return false;
        }

        transaction.setStatusCode(statusCode);
        getRepository().saveEntity(transaction);
        return true;
    }

    @Override
    public List<ValidationResult> approveTransaction(
            String requestType, String serviceId, String languageCode, boolean validationOnly) {

        TransactionStatusChanger transaction = this.getTransactionByServiceId(
                serviceId, false, TransactionStatusChanger.class);
        List<ValidationResult> validationResultList = new ArrayList<ValidationResult>();
        if (transaction == null) {
            return validationResultList;
        }

        validationResultList.addAll(this.validateTransaction(
                transaction.getId(), requestType,
                languageCode, RegistrationStatusType.STATUS_CURRENT));

        validationOnly = validationOnly && !systemEJB.validationSucceeded(validationResultList);
        if (!validationOnly) {
            this.changeStatusOfTransactionObjectsOnApproval(requestType, transaction.getId());
            transaction.setStatusCode(TransactionStatusType.APPROVED);
            transaction.setApprovalDatetime(DateUtility.now());
            getRepository().saveEntity(transaction);
        }
        return validationResultList;
    }

    private void changeStatusOfTransactionObjectsOnApproval(
            String requestType, String transactionId) {
        if (requestType.equals(TransactionType.CADASTRE_CHANGE)
                || requestType.equals(TransactionType.REDEFINE_CADASTRE)
                || requestType.equals(TransactionType.NEW_APARTMENT)
                || requestType.equals(TransactionType.NEW_DIGITAL_PROPERTY)
                || requestType.equals(TransactionType.NEW_DIGITAL_TITLE)
                || requestType.equals(TransactionType.NEW_FREEHOLD)
                || requestType.equals(TransactionType.NEW_STATE)) {
            cadastreEJB.ChangeStatusOfCadastreObjects(
                    transactionId, 
                    CadastreObjectStatusChanger.QUERY_WHERE_SEARCHBYTRANSACTION_PENDING,
                    RegistrationStatusType.STATUS_CURRENT);
        }
        if(requestType.equals(TransactionType.CADASTRE_CHANGE)){
            cadastreEJB.ChangeStatusOfCadastreObjects(
                    transactionId, 
                    CadastreObjectStatusChanger.QUERY_WHERE_SEARCHBYTRANSACTION_TARGET,
                    RegistrationStatusType.STATUS_HISTORIC);   
        }
    }

    @Override
    public boolean rejectTransaction(String serviceId) {
        TransactionBasic transaction = this.getTransactionByServiceId(serviceId, false, TransactionBasic.class);
        if (transaction == null) {
            return false;
        }

        transaction.setEntityAction(EntityAction.DELETE);
        getRepository().saveEntity(transaction);
        return true;
    }

    /**
     * Gets the list of registration status types. 
     * @return 
     */
    @Override
    public List<RegistrationStatusType> getRegistrationStatusTypes(String languageCode) {
        return getRepository().getCodeList(RegistrationStatusType.class, languageCode);
    }

    @Override
    public <T extends TransactionBasic> List<ValidationResult> saveTransaction(
            T transaction, String requestType, String languageCode) {

        //It removes first the transaction from db
        TransactionBasic tmpTransaction = this.getTransactionByServiceId(
                transaction.getFromServiceId(), false, TransactionBasic.class);
        if (tmpTransaction != null) {
            tmpTransaction.setEntityAction(EntityAction.DELETE);
            this.saveEntity(tmpTransaction);
        }

        //It adds it
        transaction = this.saveEntity(transaction);

        //It runs the validation
        List<ValidationResult> validationResultList = validateTransaction(
                transaction.getId(), requestType,
                languageCode, RegistrationStatusType.STATUS_PENDING);

        if (!systemEJB.validationSucceeded(validationResultList)) {
            //If the validation fails the whole transaction is rolledback.
            throw new SOLAValidationException(validationResultList);
        }
        return validationResultList;
    }

    private List<ValidationResult> validateTransaction(
            String transactionId, String requestType, String languageCode, String momentCode) {
        List<BrValidation> brValidationList = null;
        if (requestType.equals(TransactionType.CADASTRE_CHANGE)) {
            brValidationList = this.systemEJB.getBrForValidatingTransaction(
                    "cadastre_object", momentCode, requestType);
        }
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();

        //The business rules fired, are supposed to get only one parameter and that is
        // the id of the transaction
        params.put("id", transactionId);

        //Run the validation
        List<ValidationResult> validationResultList =
                this.systemEJB.checkRulesGetValidation(brValidationList, languageCode, params);

        //If there has to be extra validation depending in the kind of transaction
        // has to happen here and added to the validationResultList.

        return validationResultList;
    }
}
