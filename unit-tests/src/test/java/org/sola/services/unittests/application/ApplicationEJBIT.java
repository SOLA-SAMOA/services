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
package org.sola.services.unittests.application;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.OptimisticLockException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.sola.services.ejb.application.repository.entities.ApplicationProperty;
import org.sola.services.common.LocalInfo;
import org.sola.services.ejb.source.repository.entities.Source;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.services.boundary.transferobjects.referencedata.RequestTypeTO;
import org.sola.services.common.contracts.GenericTranslator;
import org.sola.services.ejb.address.repository.entities.Address;
import org.sola.services.ejb.application.businesslogic.ApplicationEJB;
import org.sola.services.ejb.application.businesslogic.ApplicationEJBLocal;
import org.sola.services.ejb.application.repository.entities.Application;
import org.sola.services.ejb.application.repository.entities.ApplicationLog;
import org.sola.services.ejb.application.repository.entities.RequestType;
import org.sola.services.ejb.application.repository.entities.Service;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.party.businesslogic.PartyEJB;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;

/**
 *
 * @author soladev
 */
public class ApplicationEJBIT extends AbstractEJBTest {

    private static String applicationId = java.util.UUID.randomUUID().toString();
    private static int appRowVersion = 0;
    private static String APP_MODULE_NAME = "sola-application-1_0-SNAPSHOT";
    private static String PARTY_MODULE_NAME = "sola-party-1_0-SNAPSHOT";
    private static String USER_MODULE_NAME = "sola-user-1_0-SNAPSHOT";

    public ApplicationEJBIT() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of createApplication method, of class ApplicationEJB.
     */
    @Test
    public void testCreateApplication() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("createApplication");

        Address address = new Address();
        address.setDescription("My agent Address");

        Party agent = new Party();
        agent.setName("First");
        agent.setLastName("Last");
        agent.setTypeCode("naturalPerson");
        agent.setChangeUser("andrew");
        agent.setAddress(address);

        Party contact = new Party();
        contact.setName("Contact");
        contact.setLastName("Smith");
        contact.setTypeCode("naturalPerson");
        contact.setChangeUser("andrew");

        Service ser1 = new Service();
        ser1.setRequestTypeCode("newTitle");
        ser1.setChangeUser("andrew");
        ser1.setServiceOrder(0);

        Service ser2 = new Service();
        ser2.setRequestTypeCode("regnOnTitle");
        ser2.setChangeUser("andrew");
        ser2.setServiceOrder(1);

        Application application = new Application();
        //application.setId(applicationId);
        //application.setNr("MyFirstTestApp2");
        application.setChangeUser("andrew");
        application.setAgent(agent);
        application.setContactPerson(contact);
//        application.addService(ser1);
//        application.addService(ser2);

        System.out.println("Add sources");
        Source source1 = new Source();
        System.out.println("New source with id:" + source1.getId());
        source1.setLaNr("lanr-1");
        source1.setTypeCode("documentDigital");

        Source source2 = new Source();
        System.out.println("New source with id:" + source2.getId());
        source2.setLaNr("lanr-2");
        source2.setTypeCode("documentDigital");

//        application.addSource(source1);
//        application.addSource(source2);


        //Adding properties to the application
        ApplicationProperty applicationProperty1 = new ApplicationProperty();
        applicationProperty1.setNameFirstpart("first_part1");
        applicationProperty1.setNameLastpart("last_part1");
        applicationProperty1.setArea(new BigDecimal("0.23"));
        applicationProperty1.setTotalValue(new BigDecimal("10000"));

        ApplicationProperty applicationProperty2 = new ApplicationProperty();
        applicationProperty2.setNameFirstpart("first_part2");
        applicationProperty2.setNameLastpart("last_part2");
        applicationProperty2.setTotalValue(new BigDecimal("12300"));

//        application.addProperty(applicationProperty1);
//        application.addProperty(applicationProperty2);

        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());
        PartyEJBLocal partyEJB = (PartyEJBLocal) getEJBInstance(PARTY_MODULE_NAME,
                PartyEJB.class.getSimpleName());

        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();

            // Find an existing Agent to use for the new application
            List<Party> agents = partyEJB.getAgents();
            if (agents != null && agents.size() > 1) {
                agent.setId(agents.get(0).getId());
                application.setAgentId(agent.getId());
            } else {
                fail("Database does not have any agents in it. Add at least 1 agent to perform"
                        + " this test. Note that subsequent tests may also fail!");
            }
            Application result = instance.createApplication(application);
            applicationId = result.getId();
            appRowVersion = result.getRowVersion();
            System.out.println("ApplicationId = " + applicationId + ", RowVersion = " + appRowVersion);

            // Check the fee calculation
            assertEquals("Fee Calculation incorrect: ", new BigDecimal("1688.82"), result.getTotalFee());
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Test of createApplication method, of class ApplicationEJB.
     */
    @Test
    @Ignore
    public void testCreateApplicationSimple() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("createApplication Simple");

        Application application = new Application();
        //application.setNr("MyFirstTestApp2");
        application.setChangeUser("andrew");
        application.setAgentId("284a9f5c30417975013041797c4e0003");
        application.setContactPersonId("284a9f5c30417975013041797c4e0003");
//        application.setFeePaid(Boolean.TRUE);
//        application.setServicesFee(BigDecimal.TEN);
//        application.setTax(BigDecimal.ONE);
//        application.setTotalFee(BigDecimal.ZERO);
//        application.setTotalAmountPaid(BigDecimal.ZERO);
//        

        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());

        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Application result = instance.createApplication(application);
            applicationId = result.getId();
            appRowVersion = result.getRowVersion();
            System.out.println("ApplicationId = " + applicationId + ", RowVersion = " + appRowVersion);
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Test of getApplication method, of class ApplicationEJB.
     */
    @Test
    public void testGetApplication() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getApplication");

        String id = applicationId;

        if (id == null) {
            return;
        }
        //String id = "284a9f5c304682b701304682c01c0008";
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());
        System.out.println("Id = " + id);
        // Manage the scope of the transction so the Application entity does not
        // detach before the ServicesInAPplication can be lazy loadded. 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Application result = instance.getApplication(id);
            LocalInfo.getUserName();
            if (result == null) {
                System.out.println("Couldn't find app for id " + id.toString());
            } else {
                System.out.println("Got Application");

                if (result.getServiceList() != null) {
                    System.out.println("Number of services=" + result.getServiceList().size());
                }
                if (result.getPropertyList() != null) {
                    System.out.println("Number of properties=" + result.getPropertyList().size());
                }
                if (result.getSourceList() != null) {
                    System.out.println("Number of Source=" + result.getSourceList().size());
                    for (Source source : result.getSourceList()) {
                        System.out.println("Source with number:" + source.getLaNr());
                    }
                }
            }
            //ApplicationTO to = GenericTranslator.toTO(result, ApplicationTO.class);
            //Application testApp = GenericTranslator.fromTO(to, Application.class, result);
            //Application testApp2 = GenericTranslator.fromTO(to, Application.class, null);
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Test of getUnassignedApplications method, of class ApplicationEJB.
     */
//    @Test
//    @Ignore
//    public void testGetApplicationLog() throws Exception {
//        System.out.println("getApplicationLog");
//        String applicationId = "";
//        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
//    ApplicationEJB.class.getSimpleName());
//        List expResult = null;
//        List result = instance.getApplicationLog(applicationId);
//
//        // TODO review the generated test code and remove the default call to fail.
//
//    }
    @Test
    public void testGetRefData_RequestTypeServiceTypes() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("testGetRefData_RequestTypeServiceTypes");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());

        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            List<RequestType> result = instance.getRequestTypes("en");
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    /**
     * Test of getApplication method, of class ApplicationEJB.
     */
    @Test
    public void testGetRequestTypes() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getRequestTypes");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            List<RequestType> result = instance.getRequestTypes("en");
            List<RequestTypeTO> resultTO = GenericTranslator.toTOList(result, RequestTypeTO.class);
            List<RequestType> result2 = GenericTranslator.fromTOList(resultTO, RequestType.class, null);
            if (result == null || result.isEmpty()) {
                System.out.println("Couldn't find any request type in database ");
            } else {
                System.out.println("Number of requesttypes found is: " + result.size());
            }
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    @Test
    public void testGetApplicationLog() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("testGetApplicationLog");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            List<ApplicationLog> result = instance.getApplicationLog(applicationId);
            if (result == null || result.isEmpty()) {
                System.out.println("Couldn't find any application log details in database ");
            } else {
                System.out.println("Number of log records found found is: " + result.size());
            }
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }

    @Test
    @Ignore
    public void testApplicationDeleteProperty() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("testApplicationDeleteProperty");
        String id = applicationId;
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(APP_MODULE_NAME,
                ApplicationEJB.class.getSimpleName());
        System.out.println("Id = " + applicationId);
        // Manage the scope of the transction so the Application entity does not
        // detach before the ServicesInAPplication can be lazy loadded. 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Application result = instance.getApplication(id);
            if (result == null) {
                System.out.println("Couldn't find app for id " + id.toString());
            } else {
                System.out.println("Got Application");

                if (result.getPropertyList() != null) {
                    System.out.println("Number of properties=" + result.getPropertyList().size());
                    if (result.getPropertyList().size() > 0) {
                        ApplicationProperty appProp = result.getPropertyList().get(0);
                       // appProp.setChangeAction('d');
                        System.out.println("Changed change action status to delete ");
                    }
                }
            }
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
        //this.testGetApplication();
    }
}
