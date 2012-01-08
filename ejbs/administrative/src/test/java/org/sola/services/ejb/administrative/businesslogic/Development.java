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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.administrative.businesslogic;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.ejb.party.businesslogic.PartyEJB;
import org.sola.services.ejb.party.repository.entities.Party;
import static org.junit.Assert.*;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.administrative.repository.entities.BaUnit;
import org.sola.services.ejb.administrative.repository.entities.BaUnitNotation;
import org.sola.services.ejb.administrative.repository.entities.Rrr;
//import org.sola.services.ejb.administrative.repository.RrrDetails;
import org.sola.services.ejb.administrative.repository.entities.RrrShare;
//import org.sola.services.ejb.administrative.repository.RrrShareDetails;
//import org.sola.services.ejb.administrative.repository.RrrSource;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.RegistrationStatusType;

/**
 *
 * @author soladev
 */
public class Development extends AbstractEJBTest {

    private static String baUnitId = null;

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
     * Test of createBaUnit method, of class AdministrativeEJB.
     */
    @Test
    //@Ignore
    public void testBaUnitOperations() throws Exception {

        System.out.println("Create ba unit operations");

        AdministrativeEJBLocal instance = (AdministrativeEJBLocal) getEJBInstance(AdministrativeEJB.class.getSimpleName());


        // Manage the scope of the transction 
        UserTransaction tx = getUserTransaction();
        try {
            tx.begin();
            System.out.println("Create new baunit with 2 rrrs, 2 cadastre objects");
            BaUnit baUnit = this.getBaUnit();
            BaUnit result = instance.createBaUnit(null, baUnit);
            assertNotNull(result);
            System.out.println("Creation of baunit succeeded.");
            baUnitId = result.getId();
            System.out.println("Update ba unit. Adding a new rrr");
            result.getRrrList().add(this.getRrr("rrr-update", "ownership"));
            result = instance.saveBaUnit(null, result);
            assertNotNull(result);
            System.out.println("Succeeded.");
//            System.out.println("Update ba unit. Adding a new rrrShare to existing rrr");
//            result.getRrrList().get(1).getRrrShareList().add(this.getRrrShare("1002"));
//            result = instance.saveBaUnit(null, result);
//            assertNotNull(result);
//            System.out.println("Succeeded.");
//            System.out.println("Adding new rrr in an existing baunit succeeded.");
//            System.out.println("Update ba unit. Remove first rrr in the list");
//            result.getRrrList().get(1).setEntityAction(EntityAction.DELETE);
//            result = instance.saveBaUnit("4000", result);
//            assertNotNull(result);
//            System.out.println("Succeeded.");
            System.out.println("Approve transaction of the first rrr");
            String transactionId = result.getRrrList().get(0).getTransactionId();
            instance.approveTransaction(transactionId, 
                    RegistrationStatusType.STATUS_CURRENT, false, "en");
            System.out.println("Succeeded.");
            
            tx.commit();
        } catch (Exception ex) {
            System.out.println("BA Unit could not be created: \nReason: " + ex);
            fail("Failed.");
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
            baUnit.setTypeCode("administrativeUnit");

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
        co.setNameLastpart(lastPart);
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
