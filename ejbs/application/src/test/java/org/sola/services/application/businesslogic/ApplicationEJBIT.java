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
package org.sola.services.application.businesslogic;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.persistence.OptimisticLockException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.sola.services.ejb.source.repository.entities.Source;
import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.common.DateUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.contracts.GenericTranslator;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.address.repository.entities.Address;
import org.sola.services.ejb.application.businesslogic.ApplicationEJB;
import org.sola.services.ejb.application.businesslogic.ApplicationEJBLocal;
import org.sola.services.ejb.application.repository.entities.Application;
import org.sola.services.ejb.application.repository.entities.ApplicationLog;
import org.sola.services.ejb.application.repository.entities.ApplicationProperty;
import org.sola.services.ejb.application.repository.entities.RequestType;
import org.sola.services.ejb.application.repository.entities.Service;
import org.sola.services.ejb.party.businesslogic.PartyEJB;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;
import org.sola.services.ejb.source.businesslogic.SourceEJB;
import org.sola.services.ejb.source.businesslogic.SourceEJBLocal;

public class ApplicationEJBIT extends AbstractEJBTest {

    private static String applicationId = null;
    private static int appRowVersion = 0;
    private static String APP_MODULE_NAME = "sola-application-1_0-SNAPSHOT";
    private static String PARTY_MODULE_NAME = "sola-party-1_0-SNAPSHOT";
    private static String ADDR_MODULE_NAME = "sola-address-1_0-SNAPSHOT";
    private static String SOURCE_MODULE_NAME = "sola-source-1_0-SNAPSHOT";

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
        
                if (applicationId != null) {
            return;
        }
        System.out.println("createApplication");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());

        Party contact = new Party();
        contact.setName("Contact");
        contact.setLastName("Smith");
        contact.setTypeCode("naturalPerson");
        contact.setChangeUser("andrew");

        Service ser1 = new Service();
        ser1.setRequestTypeCode("newDigitalTitle");
        ser1.setChangeUser("andrew");
        ser1.setServiceOrder(0);

        Service ser2 = new Service();
        ser2.setRequestTypeCode("newDigitalTitle");
        ser2.setChangeUser("andrew");
        ser2.setServiceOrder(1);

        Application application = new Application();
        //application.setNr("MyFirstTestApp2");
        application.setChangeUser("andrew");
        //application.setAgent(agent);
        application.setContactPerson(contact);

        List<Service> services = new ArrayList<Service>();
        services.add(ser1);
        services.add(ser2);
        application.setServiceList(services);


        System.out.println("Add location");
        //Adding location
        WKTReader wktReader = new WKTReader();
        Geometry geom = wktReader.read("MULTIPOINT((2 3), (4 5))");
        geom.setSRID(2193);
        WKBWriter wkbWritter = new WKBWriter(2);
        byte[] locationAsBytes = wkbWritter.write(geom);
        application.setLocation(locationAsBytes);

        System.out.println("Add properties");
        //Adding properties to the application
        ApplicationProperty applicationProperty1 = new ApplicationProperty();
        applicationProperty1.setNameFirstpart("first_part1");
        applicationProperty1.setNameLastpart("last_part1");

        ApplicationProperty applicationProperty2 = new ApplicationProperty();
        applicationProperty2.setNameFirstpart("first_part2");
        applicationProperty2.setNameLastpart("last_part2");

        List<ApplicationProperty> props = new ArrayList<ApplicationProperty>();
        props.add(applicationProperty1);
        props.add(applicationProperty2);
        application.setPropertyList(props);

        System.out.println("Add sources");
        Source source1 = new Source();
        System.out.println("New source with id:" + source1.getId());
        source1.setLaNr("lanr-1");
        source1.setTypeCode("deed");

        Source source2 = new Source();
        System.out.println("New source with id:" + source2.getId());
        source2.setLaNr("lanr-2");
        source2.setTypeCode("deed");

        List<Source> sources = new ArrayList<Source>();
        sources.add(source1);
        sources.add(source2);
        application.setSourceList(sources);

        PartyEJBLocal partyEJB = (PartyEJBLocal) getEJBInstance(PARTY_MODULE_NAME,
                PartyEJB.class.getSimpleName());

        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();

            // Find an existing Agent to use for the new application
            List<Party> agents = partyEJB.getAgents();
            if (agents != null && agents.size() > 1) {
                Party agent = new Party();
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
            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }
    
     @Test
    public void testSaveApplication() throws Exception {
                 if (applicationId == null) {
            testCreateApplication();
        }
        System.out.println("saveApplication");

        String id = applicationId;


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
            Service ser3 = new Service();
            ser3.getId(); 
            ser3.setRequestTypeCode("newDigitalTitle");
            ser3.setServiceOrder(3);
            ser3.setChangeUser("TESTUSER");
            ser3.setStatusCode(null);
            List<Service> s = new ArrayList<Service>(); 
            s.add(ser3);
            result.setServiceList(s);

            result = instance.saveApplication(result);
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
        application.setAgentId("B61507DD-6185-4BBE-9190-013B3C147DB3");
        application.setContactPersonId("B61507DD-6185-4BBE-9190-013B3C147DB3");
        application.setExpectedCompletionDate(Calendar.getInstance().getTime());
        application.setStatusCode("lodged");

        WKTReader wktReader = new WKTReader();
        Geometry geom = wktReader.read("MULTIPOINT((2 3), (4 5))");
        geom.setSRID(2193);
        WKBWriter wkbWritter = new WKBWriter(2);
        byte[] locationAsBytes = wkbWritter.write(geom);
        application.setLocation(locationAsBytes);
//        application.setFeePaid(Boolean.TRUE);
//        application.setServicesFee(BigDecimal.TEN);
//        application.setTax(BigDecimal.ONE);
//        application.setTotalFee(BigDecimal.ZERO);
//        application.setTotalAmountPaid(BigDecimal.ZERO);
//        

        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());

        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            Application result = instance.createApplication(application);
            applicationId = result.getId();
            appRowVersion = result.getRowVersion();
            System.out.println("ApplicationId = " + applicationId + ", RowVersion = " + appRowVersion);
            tx.commit();
        } catch (Exception ex) {
            System.out.println("Application could not be created: \nReason: " + ex.getMessage());
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
    @Ignore
    public void testGetApplication() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getApplication");

        String id = applicationId;

        if (id == null) {
            System.out.println("Id is null. Cannot get any application");
            return;
        }
        //String id = "284a9f5c304682b701304682c01c0008";
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(
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

                if (result.getLocation() != null) {
                    WKBReader wkbReader = new WKBReader();
                    byte[] locationAsByte = result.getLocation();
                    Geometry geom = wkbReader.read(locationAsByte);
                    System.out.println("Location of application: " + geom.toString());
                } else {
                    System.out.println("Location of application not present.");
                }

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
            // ApplicationTO to = GenericTranslator.toTO(result, ApplicationTO.class);
            // Application testApp = GenericTranslator.fromTO(to, Application.class, result);
            // Application testApp2 = GenericTranslator.fromTO(to, Application.class, null);
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
    public void testRemoveApplicationSource() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("RemoveApplicationSource");

        String id = applicationId;

        if (id == null) {
            return;
        }
        //String id = "284a9f5c304682b701304682c01c0008";
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());
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
                    int i = 0;
                    for (Source source : result.getSourceList()) {
                        if (i == 0) {
                            source.setEntityAction(EntityAction.DELETE);
                            i++;
                        }
                        System.out.println("Source with number:" + source.getLaNr());
                    }
                }
            }
            instance.saveApplication(result);
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

    @Test
    @Ignore
    public void testChangeApplicationAssignment_Assign() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("changeApplicationAssignment_Assign");

        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());

        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            instance.applicationActionAssign(applicationId, "test-id", "en", appRowVersion);
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

    /**
     * Test of getApplication method, of class ApplicationEJB.
     */
    @Test
    @Ignore
    public void testGetRequestTypes() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getRequestTypes");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            List<RequestType> result = instance.getRequestTypes("en");
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
    @Ignore
    public void testApplicationDeleteProperty() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("testApplicationDeleteProperty");
        String id = applicationId;
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(ApplicationEJB.class.getSimpleName());
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
                        //  appProp.setChangeAction('d');
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

    /**
     * Test of validate method, of class ApplicationEJB.
     */
    @Test
    public void testValidate() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("validate");

        String id = applicationId;

        if (id == null) {
            System.out.println("Id is null. Cannot get any application");
            return;
        }
        //String id = "284a9f5c304682b701304682c01c0008";
        ApplicationEJBLocal instance = (ApplicationEJBLocal) getEJBInstance(
                ApplicationEJB.class.getSimpleName());
        System.out.println("Id = " + id);
        List<ValidationResult> result = instance.applicationActionValidate(applicationId, "en", 1);
        System.out.println("Validations found:" + result.size());
    }

   

    /**
     * Test of saveApplication method, of class ApplicationEJB.
     */
    @Test
    public void testSaveApp() throws Exception {

        System.out.println("testSaveApp...");
        Address address = new Address();
        address.setDescription("My party testing Address");
        Party party = new Party();
        party.setTypeCode("naturalPerson");
        party.setChangeUser("andrew");
        party.setAddress(address);
        Application app = new Application();
        app.setActionCode("lodged");
        //app.setNr("Test Number");
        app.setStatusCode("lodged");
        app.setContactPerson(party);
        app.setAgentId("1000");

        Service ser1 = new Service();
        ser1.setRequestTypeCode("newDigitalTitle");
        ser1.setStatusCode("lodged");
        ser1.setActionCode("lodged");
        ser1.setExpectedCompletionDate(DateUtility.now());
        ser1.setServiceOrder(0);
        List<Service> services = new ArrayList<Service>();
        services.add(ser1);
        app.setServiceList(services);


        //PartyEJBLocal partyEJB = (PartyEJBLocal) getEJBInstance(PARTY_MODULE_NAME,
        //        PartyEJB.class.getSimpleName());

        SourceEJBLocal sourceEJB = (SourceEJBLocal) getEJBInstance(SOURCE_MODULE_NAME,
                SourceEJB.class.getSimpleName());

        ApplicationEJBLocal instance = (ApplicationEJBLocal) 
                getEJBInstance(ApplicationEJB.class.getSimpleName());


        // Create the Address
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            System.out.println(">>> Create App");
            Application result = instance.createApplication(app);
            assertNotNull(result);
            assertNotNull(result.getContactPerson());
            assertEquals(1, result.getRowVersion());
            assertEquals(1, result.getContactPerson().getRowVersion());
            assertNotNull(result.getExpectedCompletionDate());
            assertNotNull(result.getLodgingDatetime());
            assertNull(result.getEntityAction());
            assertEquals(false, result.isFeePaid());
            //assertEquals(0, BigDecimal.ZERO.compareTo(result.getTax()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(result.getServicesFee()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalAmountPaid()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(result.getTotalFee()));
            assertNotNull(result.getServiceList());
            assertEquals(1, result.getServiceList().size());
            assertEquals(1, result.getServiceList().get(0).getRowVersion());
            Service ser = result.getServiceList().get(0);
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getAreaFee()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getValueFee()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getBaseFee()));

            String appId = result.getId();
            System.out.println("AppId=" + result.getId());
            System.out.println("PartyId=" + result.getContactPerson().getId());

            System.out.println(">>> Update Application");
            result.setActionNotes("Test Action Note");
            result.setFeePaid(Boolean.TRUE);

            List<Service> newServiceList = new ArrayList<Service>();
            newServiceList.addAll(result.getServiceList());
            Service ser2 = new Service();
            ser2.setStatusCode("lodged");
            ser2.setRequestTypeCode("newDigitalTitle");
            ser2.setActionNotes("Second Service");
            ser2.setExpectedCompletionDate(DateUtility.now());
            ser2.setServiceOrder(1);
            
            newServiceList.add(ser2);
            result.setServiceList(newServiceList);
            ser2 = result.getServiceList().get(1);
            ser2.setActionCode("lodged");


            Application result2 = instance.saveApplication(result);
            assertNotNull(result2);
            assertNotNull(result2.getContactPerson());
            assertEquals(2, result2.getRowVersion());
            assertEquals(1, result2.getContactPerson().getRowVersion());
            assertNotNull(result2.getServiceList());
            assertEquals(2, result2.getServiceList().size());
            assertEquals(1, result2.getServiceList().get(0).getRowVersion());
            assertEquals(1, result2.getServiceList().get(1).getRowVersion());
            assertTrue(result2.isFeePaid());

            if (result2.getServiceList().get(1).getServiceOrder() == 1) {
                ser = result2.getServiceList().get(1);
            } else {
                ser = result2.getServiceList().get(0);
            }
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getAreaFee()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getValueFee()));
            //assertEquals(0, BigDecimal.ZERO.compareTo(ser.getBaseFee()));

            // Should not have any affect a service cannot exist without beign associated 
            // to an application. 
            System.out.println(">>> Disassociate Service");
            result2.getServiceList().get(0).setEntityAction(EntityAction.DISASSOCIATE);
            Application result3 = instance.saveApplication(result2);
            assertNotNull(result3);
            assertNotNull(result3.getContactPerson());
            assertEquals(2, result3.getRowVersion());
            assertEquals(1, result3.getContactPerson().getRowVersion());
            assertNotNull(result3.getServiceList());
            assertEquals(2, result3.getServiceList().size());
            assertEquals(1, result3.getServiceList().get(0).getRowVersion());
            assertEquals(1, result3.getServiceList().get(1).getRowVersion());

            System.out.println(">>> Delete Service");
            result3.getServiceList().get(0).setEntityAction(EntityAction.DELETE);
            String serId = result3.getServiceList().get(1).getId();
            Application result4 = instance.saveApplication(result3);
            assertNotNull(result4);
            assertNotNull(result4.getContactPerson());
            assertEquals(2, result4.getRowVersion());
            assertEquals(1, result4.getContactPerson().getRowVersion());
            assertNotNull(result4.getServiceList());
            assertEquals(1, result4.getServiceList().size());
            assertEquals(1, result4.getServiceList().get(0).getRowVersion());
            assertEquals(serId, result4.getServiceList().get(0).getId());
            assertEquals(0, result4.getPropertyList().size());


            System.out.println(">>> Add Property & Sources");
            ApplicationProperty prop = new ApplicationProperty();
            prop.setNameFirstpart("first");
            prop.setNameLastpart("last");
            List<ApplicationProperty> props = new ArrayList<ApplicationProperty>();
            props.add(prop);
            result4.setPropertyList(props);

            Source source1 = new Source();
            source1.setTypeCode("title");
            source1.setLaNr("sourceNum1");

            Source source2 = new Source();
            source2.setTypeCode("deed");
            source2.setLaNr("sourceNum2");
            List<Source> sources = new ArrayList<Source>();
            sources.add(source1);
            sources.add(source2);
            result4.setSourceList(sources);

            Application result5 = instance.saveApplication(result4);
            assertNotNull(result5);
            assertNotNull(result5.getContactPerson());
            assertEquals(2, result5.getRowVersion());
            assertEquals(1, result5.getContactPerson().getRowVersion());
            assertNotNull(result5.getServiceList());
            assertEquals(1, result5.getServiceList().size());
            assertEquals(1, result5.getServiceList().get(0).getRowVersion());
            assertEquals(serId, result5.getServiceList().get(0).getId());
            assertEquals(1, result5.getPropertyList().size());
            assertEquals(2, result5.getSourceList().size());

            System.out.println(">>> Delete & Disassociate Sources");
            result5.getSourceList().get(0).setEntityAction(EntityAction.DELETE);
            result5.getSourceList().get(1).setEntityAction(EntityAction.DISASSOCIATE);
            String sourceId1 = result5.getSourceList().get(0).getId();
            String sourceId2 = result5.getSourceList().get(1).getId();
            Application result6 = instance.saveApplication(result5);
            assertNotNull(result6);
            assertEquals(2, result6.getRowVersion());
            assertNotNull(result6.getServiceList());
            assertEquals(1, result6.getServiceList().size());
            assertEquals(1, result6.getServiceList().get(0).getRowVersion());
            assertEquals(1, result6.getPropertyList().size());
            assertEquals(0, result6.getSourceList().size());
            assertNull(sourceEJB.getSourceById(sourceId1));
            assertNotNull(sourceEJB.getSourceById(sourceId2));
            

            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }

    }
}
