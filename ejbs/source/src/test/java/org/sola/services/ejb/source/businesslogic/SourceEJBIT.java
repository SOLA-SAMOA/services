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

import java.util.List;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import static org.junit.Assert.*;
import org.sola.services.common.EntityAction;
import org.sola.services.ejb.source.repository.entities.Source;

/**
 *
 * @author Manoku
 */
public class SourceEJBIT extends AbstractEJBTest {

    private static String sourceId = "";
    private static String SOURCE_MODULE_NAME = "sola-source-1_0-SNAPSHOT";

    public SourceEJBIT() {
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
    @Ignore
    public void testAddNew() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SourceEJB.class.getSimpleName());
        System.out.println("addNew");
        Source document = new Source();
        //document.setId("NEWSOURCE");
        document.setTypeCode("documentDigital");
        document.setLaNr("lanr1");
        System.out.println("testAddNew-1");
        UserTransaction tx = getUserTransaction();
        System.out.println("testAddNew-2");
        try {
            tx.begin();
            System.out.println("testAddNew-3");
            Source result = instance.saveSource(document);
            System.out.println("testAddNew-4");
            this.sourceId = result.getId();
            System.out.println("testAddNew-5");
            result = instance.getSourceById(sourceId);
            System.out.println("testAddNew-6");
            System.out.println("Source with id:" + sourceId);
            tx.commit();
            System.out.println("testAddNew-5");
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Test of updateSource method, of class SourceEJB.
     */
    @Test
    @Ignore
    public void testUpdateSource() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("updateSource");
        Source document = null;
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SourceEJB.class.getSimpleName());
        document = instance.getSourceById(sourceId);
        if (document == null) {
            System.out.println("No source found for id:" + sourceId);
            return;
        } else {
            System.out.println("Source found for id:" + sourceId);
        }
        document.setLaNr(document.getLaNr() + "-u");
        Source result = instance.saveSource(document);
        System.out.println("Test finished.");
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
    }

    /**
     * Test of getAllsources method, of class SourceEJB.
     */
    @Test
    @Ignore
    public void testGetAllsources() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getAllsources");
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SourceEJB.class.getSimpleName());
        List expResult = null;
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            List<Source> result = instance.getAllsources();
            for(Source source:result){
                if(source.getArchiveDocument()!=null){
                    System.out.println("Found archive document ID " + source.getArchiveDocument().getId());
                }
            }
            assertEquals(expResult, result);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            fail(e.getMessage());
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

        SourceEJBLocal instance = (SourceEJBLocal) 
                getEJBInstance(SourceEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Source result = instance.getSourceById(id);
            if (result != null) {
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

    /**
     * Test of remove method, of class SourceEJB.
     */
    @Test
    @Ignore
    public void testRemoveSource() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("removeSource");
        String id = sourceId;
        System.out.println("Trying to get source with id:" + id);

        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SourceEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Source result = instance.getSourceById(id);
            if (result != null) {
                System.out.println("Source FOUND id:" + result.getId());
            }
            result.markForDelete();
            instance.saveSource(result);
            System.out.println("Source removed");
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Performs CRUD tests for the Source EJB
     */
    @Test
    public void testSaveSource() throws Exception {

        System.out.println("testSaveSource...");
        Source document = new Source();
        document.setTypeCode("deed");
        //document.setLaNr("testDoc1");
        SourceEJBLocal instance = (SourceEJBLocal) getEJBInstance(SOURCE_MODULE_NAME,
                SourceEJB.class.getSimpleName());

        // Create the Source
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            System.out.println(">>> Create Source");
            Source result = instance.saveSource(document);
            assertNotNull(result);
            assertNotNull(result.getId());
            assertEquals(1, result.getRowVersion());
            assertNull(result.getEntityAction());
            System.out.println("Id=" + result.getId());
            String id = result.getId();

            // Retrieve Source
            System.out.println(">>> Retrieve Source");
            Source result5 = instance.getSourceById(id);
            assertNotNull(result5);
            assertEquals(id, result5.getId());
            assertEquals(1, result5.getRowVersion());

            // Update the Source
            System.out.println(">>> Update Source");
            result.setAvailabilityStatusCode("incomplete");
            Source result2 = instance.saveSource(result);
            assertNotNull(result2);
            assertEquals(result.getId(), result2.getId());
            assertEquals(2, result2.getRowVersion());
            assertNull(result2.getEntityAction());


            // Disassociate the Source
            System.out.println(">>> Disassociate Source");
            result2.setEntityAction(EntityAction.DISASSOCIATE);
            Source result3 = instance.saveSource(result2);
            assertNull(result3);
            assertNotNull(instance.getSourceById(id));


            // Delete the Source
            System.out.println(">>> Delete Source");
            result2.setEntityAction(EntityAction.DELETE);
            String deletedSourceId =  result2.getId();
            result3 = instance.saveSource(result2);
            assertNull(result3);

            //System.out.println(">>> Retrieve Deleted Source");
            //Source result4 = instance.getSourceById(deletedSourceId);
            //assertNull(result4);

            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }

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
                fail("Pending source should have been deleted. Somthing wrong happened.");
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
