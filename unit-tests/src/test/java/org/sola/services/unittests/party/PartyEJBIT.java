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
package org.sola.services.unittests.party;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.sola.services.ejb.address.repository.entities.Address;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.party.businesslogic.PartyEJB;
import org.sola.services.ejb.party.repository.entities.Party;

import org.sola.services.ejb.party.businesslogic.PartyEJBLocal;


/**
 *
 * @author soladev
 */
public class PartyEJBIT extends AbstractEJBTest {
    
     private static String PARTY_MODULE_NAME = "sola-party-1_0-SNAPSHOT";

    public PartyEJBIT() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of saveParty method, of class PartyEJB.
     */
    @Test
    @Ignore
    public void testSaveParty() throws Exception {

        System.out.println("saveParty");
        Address address = new Address();
        address.setDescription("My party testing Address");
        Party party = new Party();
        party.setTypeCode("naturalPerson");
        party.setChangeUser("andrew");
       // party.setChangeAction('i');
        party.setAddress(address);

        PartyEJBLocal instance = (PartyEJBLocal) getEJBInstance(PARTY_MODULE_NAME, 
                PartyEJB.class.getSimpleName());

        Party result = instance.saveParty(party);
        //PartyTO result2 = GenericTranslator.toTO(result, PartyTO.class);
        //Party result3 = GenericTranslator.fromTO(result2, Party.class, null);

        System.out.println("PartyId=" + result.getId());
        System.out.println("AddressId=" + result.getAddress().getId());

    }

}
