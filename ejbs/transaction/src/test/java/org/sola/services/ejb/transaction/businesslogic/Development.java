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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.transaction.businesslogic;

import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.cadastre.repository.entities.SurveyPoint;
import org.sola.services.ejb.transaction.repository.entities.TransactionCadastreChange;

/**
 *
 * @author manoku
 */
public class Development extends AbstractEJBTest {

    public Development() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void testTransactionOperations() throws Exception {

        TransactionEJBLocal instance = (TransactionEJBLocal) 
                getEJBInstance(TransactionEJB.class.getSimpleName());


        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            String transactionId = "7ce22c22-21e1-449a-820f-8533233515a7";
            System.out.println("Test getTransactionById for id:" + transactionId);
            
            TransactionCadastreChange trns = 
                    instance.getTransactionById(transactionId, TransactionCadastreChange.class);
            SurveyPoint surveyPoint = trns.getSurveyPointList().get(0);
            surveyPoint.setBoundary(false);
            //surveyPoint.setEntityAction(EntityAction.UPDATE);
            instance.saveTransaction(trns, "cadastreChange", "en");
            tx.commit();
        } catch (Exception ex) {
            System.out.println("Transaction operation failed: \nReason: " + ex.getMessage());
            fail("Failed.");
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }

    }

    
}
