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
package org.sola.services.ejb.transaction.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.DateUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;
import org.sola.services.ejb.transaction.repository.entities.Transaction;
import org.sola.services.ejb.transaction.repository.entities.TransactionStatusType;

/**
 *
 */
@Stateless
@EJB(name = "java:global/SOLA/TransactionEJBLocal", beanInterface = TransactionEJBLocal.class)
public class TransactionEJB extends AbstractEJB implements TransactionEJBLocal {

    @Override
    public Transaction getTransactionByServiceId(String serviceId, boolean createIfNotFound) {
        Transaction transaction = null;
        if (serviceId != null) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, Transaction.QUERY_WHERE_BYFROMSERVICEID);
            params.put("serviceId", serviceId);
            transaction = getRepository().getEntity(Transaction.class, params);
        }
        if (createIfNotFound && transaction == null) {
            transaction = this.createTransaction(serviceId);
        }
        return transaction;
    }

    @Override
    public Transaction createTransaction(String serviceId) {
        Transaction transaction = new Transaction();
        transaction.getId();
        transaction.setFromServiceId(serviceId);
        return getRepository().saveEntity(transaction);
    }

    @Override
    public Transaction getTransactionById(String id) {
        return getRepository().getEntity(Transaction.class, id);
    }

    @Override
    public boolean changeTransactionStatusFromService(
            String serviceId, String statusCode) {
        Transaction transaction = this.getTransactionByServiceId(serviceId, false);
        if (transaction == null) {
            return false;
        }

        transaction.setStatusCode(statusCode);
        getRepository().saveEntity(transaction);
        return true;
    }

    @Override
    public boolean approveTransaction(String serviceId) {
        Transaction transaction = this.getTransactionByServiceId(serviceId, false);
        if (transaction == null) {
            return false;
        }

        transaction.setStatusCode(TransactionStatusType.APPROVED);
        transaction.setApprovalDatetime(DateUtility.now());
        getRepository().saveEntity(transaction);
        return true;
    }

    @Override
    public boolean rejectTransaction(String serviceId) {
        Transaction transaction = this.getTransactionByServiceId(serviceId, false);
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
}
