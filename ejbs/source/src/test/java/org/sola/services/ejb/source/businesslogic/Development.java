/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
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
package org.sola.services.ejb.source.businesslogic;

import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.source.repository.entities.Source;

/**
 *
 * @author Manoku
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


    /**
     * Transaction related operations
     */
    @Test
    public void testTransactionOperations() throws Exception {

        System.out.println("testSaveSource...");
        Source source = new Source();
        source.setTypeCode("powerOfAttorney");
        SourceEJBLocal instance = (SourceEJBLocal) 
                getEJBInstance(SourceEJB.class.getSimpleName());

        // Create the Source
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            System.out.println(">>> Create Source");
            Source result = instance.saveSource(source);
            String id = result.getId();
            System.out.println("Id=" + id);

            
            System.out.println(">>> AttachToTransaction");
            Source result2Pending = instance.attachSourceToTransaction("4000", id, "en");
            
            System.out.println(
                    String.format("Source origin: %s Source pending: %s",
                    id,result2Pending.getId()));
            
            System.out.println(">>> DetachFromTransaction");
            boolean result2 = instance.dettachSourceFromTransaction(result2Pending.getId());
            Source resultMustBeNull = instance.getSourceById(result2Pending.getId());
            if (resultMustBeNull != null){
               // fail("Pending source should have been deleted. Somthing wrong happened.");
            }
            System.out.println("Success!");
            

            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }
}
