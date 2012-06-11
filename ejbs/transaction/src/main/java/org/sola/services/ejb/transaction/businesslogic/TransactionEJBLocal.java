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
package org.sola.services.ejb.transaction.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;

/**
 * Local interface for the {@linkplain TransactionEJB}.
 */
@Local
public interface TransactionEJBLocal extends AbstractEJBLocal {

    /**
     * See {@linkplain TransactionEJB#createTransaction(java.lang.String, java.lang.Class)
     * TransactionEJB.createTransaction}.
     */
    <T extends TransactionBasic> T createTransaction(String serviceId, Class<T> transactionClass);

    /**
     * See {@linkplain TransactionEJB#getTransactionByServiceId(java.lang.String, boolean, java.lang.Class)
     * TransactionEJB.getTransactionByServiceId}.
     */
    <T extends TransactionBasic> T getTransactionByServiceId(
            String serviceId,
            boolean createIfNotFound,
            Class<T> transactionClass);

    /**
     * See {@linkplain TransactionEJB#getTransactionById(java.lang.String, java.lang.Class)
     * TransactionEJB.getTransactionById}.
     */
    <T extends TransactionBasic> T getTransactionById(String id, Class<T> transactionClass);

    /**
     * See {@linkplain TransactionEJB#changeTransactionStatusFromService(java.lang.String, java.lang.String)
     * TransactionEJB.changeTransactionStatusFromService}.
     */
    boolean changeTransactionStatusFromService(String serviceId, String statusCode);

    /**
     * See {@linkplain TransactionEJB#approveTransaction(java.lang.String, java.lang.String,
     * java.lang.String, boolean) TransactionEJB.approveTransaction}.
     */
    List<ValidationResult> approveTransaction(
            String requestType, String serviceId, String languageCode, boolean validationOnly);

    /**
     * See {@linkplain TransactionEJB#rejectTransaction(java.lang.String)
     * TransactionEJB.rejectTransaction}.
     */
    boolean rejectTransaction(String serviceId);

    /**
     * See {@linkplain TransactionEJB#getRegistrationStatusTypes(java.lang.String)
     * TransactionEJB.getRegistrationStatusTypes}.
     */
    List<RegistrationStatusType> getRegistrationStatusTypes(String languageCode);

    /**
     * See {@linkplain TransactionEJB#saveTransaction(org.sola.services.ejb.transaction.repository.entities.TransactionBasic,
     * java.lang.String, java.lang.String) TransactionEJB.saveTransaction}.
     */
    <T extends TransactionBasic> List<ValidationResult> saveTransaction(
            T transaction, String requestType, String languageCode);
}
