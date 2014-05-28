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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.services.application.businesslogic;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sola.common.DateUtility;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.ejb.administrative.businesslogic.AdministrativeEJB;
import org.sola.services.ejb.administrative.businesslogic.AdministrativeEJBLocal;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import static org.junit.Assert.*;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.administrative.repository.entities.BaUnit;
import org.sola.services.ejb.administrative.repository.entities.BaUnitNotation;
import org.sola.services.ejb.administrative.repository.entities.Rrr;
import org.sola.services.ejb.administrative.repository.entities.RrrShare;
import org.sola.services.ejb.application.businesslogic.ApplicationEJB;
import org.sola.services.ejb.application.businesslogic.ApplicationEJBLocal;
import org.sola.services.ejb.application.repository.entities.Application;
import org.sola.services.ejb.application.repository.entities.ApplicationProperty;
import org.sola.services.ejb.application.repository.entities.Service;
import org.sola.services.ejb.party.businesslogic.PartyEJB;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.party.repository.entities.Party;
import org.sola.services.ejb.source.repository.entities.Source;

/**
 *
 * @author Manoku
 */
public class Development extends AbstractEJBTest{

    private static String PARTY_MODULE_NAME = "sola-party-1_0-SNAPSHOT";
    private static String ADMINISTRATIVE_MODULE_NAME = "sola-administrative-1_0-SNAPSHOT";

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
     * Test of createApplication method, of class ApplicationEJB.
     */
    @Test
    @Ignore
    public void testDev() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("testDev");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) 
                getEJBInstance(ApplicationEJB.class.getSimpleName());        
        //Application app = instance.getApplication("3009");
        //List<ValidationResult> result = instance.applicationActionValidate(
        //        app.getId(),"en", app.getRowVersion());
        List<ValidationResult> result = instance.serviceActionComplete(
                "22895e4d-6bdc-403d-9282-34a661b2a986", "en", 1);
        System.out.println("Number of validation rules is:" + result.size());
        for(ValidationResult validationResult: result){
            System.out.println(String.format("Result \nname:%s \nfeedback:%s \nvalue:%s", 
                    validationResult.getName(), 
                    validationResult.getFeedback(), 
                    validationResult.isSuccessful()));
        }

    }    

    /**
     * Test of createApplication method, of class ApplicationEJB.
     */
    @Test
    //@Ignore
    public void testCreateApplication() throws Exception {
        
        System.out.println("createApplication");
        ApplicationEJBLocal instance = (ApplicationEJBLocal) 
                getEJBInstance(ApplicationEJB.class.getSimpleName());

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
        ser2.setRequestTypeCode("newOwnership");
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
        //TransactionEJBLocal transactionEJB = (TransactionEJBLocal) 
        //        getEJBInstance(TransactionEJB.class.getSimpleName());
        AdministrativeEJBLocal administrativeEJB = (AdministrativeEJBLocal)
                getEJBInstance(AdministrativeEJB.class.getSimpleName());

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
            System.out.println("Saving the application ... ");
            Application result = instance.createApplication(application);
            String applicationId = result.getId();
           
            int appRowVersion = result.getRowVersion();
            System.out.println("Succeeded. ApplicationId = " + applicationId + ", RowVersion = " + appRowVersion);
            Service firstService = result.getServiceList().get(0);
            System.out.println("Create new baUnit as started by the first service in the list.");
            BaUnit baUnit = this.getBaUnit();
            baUnit = administrativeEJB.createBaUnit(firstService.getId(), baUnit);
            System.out.println("Succeeded");
            System.out.println("Complete first service");
            instance.serviceActionComplete(firstService.getId(), "en", firstService.getRowVersion());
            System.out.println("Succeeded.");
            
            System.out.println("Approve application");
            instance.applicationActionApprove(applicationId, "en", appRowVersion);
            System.out.println("Succeeded.");

            tx.commit();
        } finally {
            if (tx.getStatus() != Status.STATUS_NO_TRANSACTION) {
                tx.rollback();
                System.out.println("Failed Transction!");
            }
        }
    }


    private BaUnit getBaUnit()  throws Exception{
            BaUnit baUnit = new BaUnit();
            baUnit.setName("Test BA Unit Name");
            baUnit.setNameFirstpart("nameFirstPart");
            baUnit.setNameLastpart("nameLastPart");

            baUnit.setBaUnitNotationList(new ArrayList<BaUnitNotation>());
            baUnit.getBaUnitNotationList().add(this.getNotation("ba unit"));

            List<Rrr> rrrs = new ArrayList<Rrr>();
            //rrrs.add(this.getRrr("createbaunit-1", "ownership"));
            rrrs.add(this.getRrr("createbaunit-2", "mortgage"));
            baUnit.setRrrList(rrrs);

            List<CadastreObject> objs = new ArrayList<CadastreObject>();
            objs.add(this.getCadastreObject("part1", "part1"));
            objs.add(this.getCadastreObject("part2", "part2"));
            baUnit.setCadastreObjectList(objs);
            return baUnit;
        
    }
    
    private Rrr getRrr(String notationText, String type) throws Exception {
        Rrr rrr = new Rrr();
        rrr.setTypeCode(type);

        rrr.setNotation(this.getNotation(notationText));

        if (type.equals("ownership")) {
            RrrShare rrrShare = this.getRrrShare("1000");

            rrr.setRrrShareList(new ArrayList<RrrShare>());
            rrr.getRrrShareList().add(rrrShare);
        } else {
            Party party = this.getParty("1001");
            rrr.setRightHolderList(new ArrayList<Party>());
            rrr.getRightHolderList().add(party);
        }

        return rrr;
    }

    private CadastreObject getCadastreObject(String firstPart, String lastPart) {
        CadastreObject co = new CadastreObject();
        co.setNameFirstpart(firstPart);
        co.setNameLastpart(lastPart + DateUtility.now());
        co.setSourceReference("Ref1");
        co.setTypeCode("parcel");
        return co;
    }

    private RrrShare getRrrShare(String partyId) throws Exception {

        RrrShare rrrShare = null;
        Party party = this.getParty(partyId);
        if (party != null) {
            rrrShare = new RrrShare();
            rrrShare.setNominator(Short.parseShort("1"));
            rrrShare.setDenominator(Short.parseShort("1"));
            rrrShare.setRightHolderList(new ArrayList<Party>());
            rrrShare.getRightHolderList().add(party);
        }

        return rrrShare;
    }

    private Party getParty(String id) throws Exception {
        PartyEJBLocal instance = (PartyEJBLocal) getEJBInstance(PartyEJB.class.getSimpleName());
        System.out.println(
                "get party with id:" + id);
        Party party = instance.getParty(id);

        return party;
    }

    private BaUnitNotation getNotation(String txt) {
        BaUnitNotation notation = new BaUnitNotation();
        notation.setNotationText(txt);
        return notation;
    }
}
