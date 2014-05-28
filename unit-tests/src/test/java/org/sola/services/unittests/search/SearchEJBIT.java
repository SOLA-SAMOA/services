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
package org.sola.services.unittests.search;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.contracts.GenericTranslator;
import static org.junit.Assert.*;
import org.sola.services.boundary.transferobjects.search.PropertyVerifierTO;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.search.businesslogic.SearchEJB;
import org.sola.services.ejb.search.businesslogic.SearchEJBLocal;
import org.sola.services.ejb.search.repository.entities.PropertyVerifier;

/**
 *
 * @author soladev
 */
public class SearchEJBIT extends AbstractEJBTest {

    private static String SEARCH_MODULE_NAME = "sola-search-1_0-SNAPSHOT";

    public SearchEJBIT() {
        super();
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getParty method, of class PartyEJB.
     */
    @Test
    public void testGetVerifyProperty() throws Exception {

        System.out.println("testGetVerifyProperty - Parcel Exists");

        SearchEJBLocal instance = (SearchEJBLocal) getEJBInstance(SEARCH_MODULE_NAME,
                SearchEJB.class.getSimpleName());
        String firstPart = "17"; // Samoan data
        String lastPart = "4841";
        PropertyVerifier result = instance.getPropertyVerifier("", firstPart, lastPart);
        if (result != null) {
            System.out.println("Parcel Exists: " + result.toString());
            assertFalse(result.isHasLocation());
            PropertyVerifierTO to = GenericTranslator.toTO(result, PropertyVerifierTO.class);
            assertFalse(to.isHasLocation());
        } else {
            System.out.println("Result: nothing returned - check Samoan data is in DB");
        }

        System.out.println("testGetVerifyProperty - Parcel Exists with Location");
        firstPart = "335"; // Samoan data
        lastPart = "2775";
        result = instance.getPropertyVerifier("", firstPart, lastPart);
        if (result != null) {
            System.out.println("Parcel Exists with Location: " + result.toString());
            assertTrue(result.isHasLocation());
            PropertyVerifierTO to = GenericTranslator.toTO(result, PropertyVerifierTO.class);
            assertTrue(to.isHasLocation());
        } else {
            System.out.println("Result: nothing returned - check Samoan data is in DB");
        }
    }
}
