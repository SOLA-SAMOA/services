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

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.TransactionBasic;
import org.sola.services.ejb.transaction.repository.entities.TransactionCadastreChange;

/**
 * Provides local interface for administrative ejbs.
 */
@Local
public interface TransactionEJBLocal extends AbstractEJBLocal {
    
    <T extends TransactionBasic> T createTransaction(String serviceId, Class<T> transactionClass);
    /**
     * It searches for a transaction by serviceId. If not found,
     * it creates a transaction and returns it.
     * @param serviceId
     * @param createIfNotFound
     * @param transactionClass 
     * @return 
     */
    <T extends TransactionBasic> T getTransactionByServiceId(
            String serviceId,
            boolean createIfNotFound,
            Class<T> transactionClass);
    
     <T extends TransactionBasic> T getTransactionById(String id, Class<T> transactionClass);
    boolean changeTransactionStatusFromService(String serviceId, String statusCode);
    List<ValidationResult> approveTransaction(
            String requestType, String serviceId, String languageCode, boolean validationOnly);
    boolean rejectTransaction(String serviceId);
    List<RegistrationStatusType> getRegistrationStatusTypes(String languageCode);
    
    <T extends TransactionBasic> List<ValidationResult> saveTransaction(
            T transaction, String requestType, String languageCode);
}
