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
package org.sola.services.ejb.cadastre.businesslogic;

import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObject;
import org.sola.services.ejb.cadastre.repository.entities.CadastreObjectNode;

/**
 *
 * @author Manoku
 */
public class CadastreEJBIT extends AbstractEJBTest {

    public CadastreEJBIT() {
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
    public void testDev() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        String cadastreObjectType = "parcel";
        System.out.println("testDev");
        CadastreEJBLocal instance = (CadastreEJBLocal) getEJBInstance(CadastreEJB.class.getSimpleName());

        List resultList = instance.getCadastreObjectByParts("Lot");
        System.out.println("Number of cadastre objects found:" + resultList.size());
        String id = "test";
        if (resultList.size() > 0) {
            CadastreObject co = (CadastreObject) resultList.get(0);
            id = co.getId();
            System.out.println("Result of Areas (total):" + co.getSpatialValueAreaList().size());
        }

        double x = 1778224, y = 5928786;
        int srid = 2193;
        System.out.println("getCadastreObjectByPoint");
        CadastreObject resultObject = 
                instance.getCadastreObjectByPoint(x, y, srid, cadastreObjectType);
        if (resultObject != null) {
            System.out.println("Result :" + resultObject);
        } else {
            System.out.println("Result : NOT FOUND");
        }

        System.out.println("getCadastreObjectTypes");
        resultList = instance.getCadastreObjectTypes("en");
        System.out.println("Result :" + resultList.size());

        System.out.println("getCadastreObject");
        resultObject = instance.getCadastreObject(id);
        if (resultObject != null) {
            System.out.println("Result :" + resultObject);
        } else {
            System.out.println("Result : NOT FOUND");
        }

        System.out.println("getCadastreObjects");
        List<String> listIds = new ArrayList<String>();
        listIds.add(id);
        listIds.add("id2");
        resultList = instance.getCadastreObjects(listIds);
        System.out.println("Result :" + resultList);

        System.out.println("getCadastreObjectNode");
        CadastreObjectNode nodeObj = instance.getCadastreObjectNode(
                1782700, 5926205, 1782726, 5926209, 2193, cadastreObjectType);
        System.out.println("Result :" + nodeObj.toString());

        System.out.println("getCadastreObjectNodePotential");
        nodeObj = instance.getCadastreObjectNodePotential(
                1784900, 5925445, 1784950, 5925512, 2193, cadastreObjectType);
        System.out.println("Result :" + nodeObj.toString());
    }
}
