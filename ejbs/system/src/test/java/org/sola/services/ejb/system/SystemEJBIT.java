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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package org.sola.services.ejb.system;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.br.ResultFeedback;
import org.sola.services.ejb.system.businesslogic.SystemEJB;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.system.repository.entities.BrValidation;

/**
 *
 * @author manoku
 */
public class SystemEJBIT extends AbstractEJBTest {

    public SystemEJBIT() {
        super();
    }

    /**
     * Test of getBr.
     */
    @Test
    public void testGetBrMethods() throws Exception {
        if (skipIntegrationTest()) {
            return;
        }
        System.out.println("getBrMethods");
        SystemEJBLocal instance = (SystemEJBLocal) getEJBInstance(SystemEJB.class.getSimpleName());

        System.out.println("getBrForValidatingApplication");
        List<BrValidation> result2 = instance.getBrForValidatingApplication("start");
        printResult(result2);

        System.out.println("getBrForValidatingRrr");
        List<BrValidation> result4 = instance.getBrForValidatingRrr("approve", "ownership");
        printResult(result4);
        
        System.out.println("checkRuleGetFeedback");
        HashMap<String, Serializable> params = new HashMap<String, Serializable>();
        params.put("var", 2);
        ResultFeedback result5 = instance.checkRuleGetFeedback("check-test", "it", params);
        System.out.println(String.format("BR: %s Feedback: %s",result5.getName(), result5.getValue()));

        System.out.println("checkRuleGetResultSingle");
        String brName = "generate-application-nr";
        Result result6 = instance.checkRuleGetResultSingle(brName, null);
        System.out.println(String.format("BR: %s Returned value: %s",brName, result6.getValue()));

        brName = "generate-source-nr";
        result6 = instance.checkRuleGetResultSingle(brName, null);
        System.out.println(String.format("BR: %s Returned value: %s",brName, result6.getValue()));

        System.out.println("getBrForValidatingService");
        List<BrValidation> result7 = instance.getBrForValidatingService("complete", "newFreehold");
        printResult(result7);

        System.out.println("getBrForValidatingBaUnit");
        List<BrValidation> result8 = instance.getBrForValidatingBaUnit("approve");
        printResult(result8);

        System.out.println("getBrForValidatingCadastreObject");
        List<BrValidation> result9 = instance.getBrForValidatingCadastreObject("pending");
        printResult(result9);
    }

    private void printResult(List<BrValidation> result) {
        System.out.println("Found: " + (result == null ? "!None!" : result.size()));
    }
}
