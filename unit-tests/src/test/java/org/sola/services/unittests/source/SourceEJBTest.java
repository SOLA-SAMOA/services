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
package org.sola.services.unittests.source;

import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.source.businesslogic.SourceEJB;
import org.sola.services.ejb.source.businesslogic.SourceEJBLocal;
import org.sola.services.ejb.source.repository.entities.Source;

/**
 *
 * @author Manoku
 */
public class SourceEJBTest extends AbstractEJBTest {

    private static String sourceId = "";
    private static String SOURCE_MOUDULE_NAME = "sola-source-1_0-SNAPSHOT";
    
    public SourceEJBTest() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of addNew method, of class SourceEJB.
     */
    @Test
    public void testAddNew() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("Source - addNew");
        Source document = new Source();
        document.setTypeCode("documentDigital");
        document.setLaNr("lanr1");
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SOURCE_MOUDULE_NAME,
                SourceEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Source result = instance.saveSource(document);
            this.sourceId = result.getId();
            System.out.println("Source with id:" + sourceId);
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }



 

    /**
     * Test of getSourceById method, of class SourceEJB.
     */
    @Test
    public void testGetSourceById() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getSourceById");
        String id = this.sourceId;
        System.out.println("Trying to get source with id:" + id);
        
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SOURCE_MOUDULE_NAME,
                SourceEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Source result = instance.getSourceById(id);
            if (result != null){
            System.out.println("Source FOUND id:" + result.getId());
            }
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }
}
