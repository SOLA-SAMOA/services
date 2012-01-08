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
package org.sola.services.common.webservices;

import javax.annotation.Resource;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.xml.ws.WebServiceContext;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.faults.FaultUtility;
import org.sola.services.common.faults.OptimisticLockingFault;
import org.sola.services.common.faults.SOLAAccessFault;
import org.sola.services.common.faults.SOLAFault;
import org.sola.services.common.faults.SOLAValidationFault;
import org.sola.services.common.faults.UnhandledFault;

/**
 *
 * @author soladev
 */
public abstract class AbstractWebService {

    /**
     * Holds a reference to the UserTransction. Injected using @Resource
     */
    @Resource
    private UserTransaction tx;
    
    /**
     * Starts a transaction.
     * @throws Exception 
     */
    protected void beginTransaction() throws Exception {
        tx.begin();
    }

    /**
     * Commits a transaction as long as the transaction is not
     * in the NO_TRANSACTION state.
     * @throws Exception 
     */
    protected void commitTransaction() throws Exception {
        if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
            tx.commit();
        }
    }

    /**
     * Rolls back the transaction as long as the transaction is not
     * in the NO_TRANSACTION state. This method should be called in 
     * the finally clause wherever a transaction is started. 
     * @throws Exception 
     */
    protected void rollbackTransaction() throws Exception {
        if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
            tx.rollback();
        }
    }

    protected void runGeneralMethod(WebServiceContext wsContext,
            Runnable generalMethod) throws UnhandledFault, SOLAFault {
        try {
            try {
                LocalInfo.setUserName(wsContext.getUserPrincipal().getName());
                beginTransaction();
                generalMethod.run();
                commitTransaction();
            } finally {
                rollbackTransaction();
            }
        } catch (Throwable t) {
            Throwable fault = FaultUtility.ProcessException(t);
            if (fault.getClass() == SOLAFault.class) {
                throw (SOLAFault) fault;
            }
            throw (UnhandledFault) fault;
        } finally {
            cleanUp();
        }
    }

    protected void runUpdateMethod(WebServiceContext wsContext, 
            Runnable updateMethod) throws UnhandledFault, SOLAAccessFault,
            SOLAFault, OptimisticLockingFault, SOLAValidationFault {
        try {
            try {
                LocalInfo.setUserName(wsContext.getUserPrincipal().getName());
                beginTransaction();
                updateMethod.run();
                commitTransaction();
            } finally {
                rollbackTransaction();
            }
        } catch (Throwable t) {
            Throwable fault = FaultUtility.ProcessException(t);
            
            if (fault.getClass() == SOLAAccessFault.class) {
                throw (SOLAAccessFault) fault;
            }
            
            if (fault.getClass() == SOLAFault.class) {
                throw (SOLAFault) fault;
            }

            if (fault.getClass() == OptimisticLockingFault.class) {
                throw (OptimisticLockingFault) fault;
            }

            if (fault.getClass() == SOLAValidationFault.class) {
                throw (SOLAValidationFault) fault;
            }
            throw (UnhandledFault) fault;
        } finally {
            cleanUp();
        }
    }

    protected void cleanUp() {
        LocalInfo.remove();
    }
}
