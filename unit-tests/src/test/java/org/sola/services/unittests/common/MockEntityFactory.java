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
package org.sola.services.unittests.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.sola.common.DateUtility;
import org.sola.services.common.EntityAction;
import org.sola.services.ejb.address.repository.entities.Address;
import org.sola.services.ejb.application.repository.entities.Application;
import org.sola.services.ejb.application.repository.entities.RequestType;
import org.sola.services.ejb.application.repository.entities.Service;
import org.sola.services.ejb.party.repository.entities.Party;

/**
 *
 * @author soladev
 */
public class MockEntityFactory {

    public Application createApplication() {
        Application result = new Application();
        result.setId("applicationId");
        result.setNr("12345678");
        result.setStatusCode("lodged");
        result.setLodgingDatetime(DateUtility.now());
        result.setExpectedCompletionDate(DateUtility.addDays(10, true));
        result.setRowVersion(2);
        result.setAgent(createParty("agentId"));
        result.setContactPerson(createParty("contactPersonId"));
        List<Service> services = new ArrayList<Service>(); 
        services.add(createService("serviceId1", 0));
        services.add(createService("serviceId2", 1));
        result.setServiceList(services);
        result.setFeePaid(true);
        return result;

    }

    public Party createParty(String id) {
        Party result = new Party();
        result.setId(id);
        result.setName("FirstName");
        result.setLastName("LastName");
        result.setTypeCode("naturalPerson");
        result.setAddress(createAddress("partyAddressId"));
        result.setRowVersion(4);
        result.setFax("MyFaxNumber"); 
        return result;
    }

    public Service createService(String id, int order) {
        Service result = new Service();
        result.setId(id);
        result.setRequestTypeCode("newTitle");
        result.setStatusCode("lodged");
        result.setServiceOrder(order);
        result.setLodgingDatetime(DateUtility.now());
        result.setExpectedCompletionDate(DateUtility.addDays(10, true));
        result.setRowVersion(3); 
        result.setAreaFee(BigDecimal.ZERO);
        result.setBaseFee(new BigDecimal(24.34));
        result.setValueFee(new BigDecimal(456354566));
        result.setEntityAction(EntityAction.DELETE);

        return result;
    }

    public Address createAddress(String id) {
        Address result = new Address();
        result.setId(id);
        result.setDescription("Address Details");
        result.setRowVersion(7);
        return result;
    }
    
    public RequestType createRequestType(String code){
        RequestType result = new RequestType();
        result.setCode(code);
        result.setDisplayValue("Display " + code);
        result.setEntityAction(EntityAction.DISASSOCIATE);
        result.setNrDaysToComplete(4);
        result.setNrPropertiesRequired(2);
        result.setRequestCategoryCode("informationServices");
        result.setStatus("c");
        result.setValueBaseFee(new BigDecimal(34.15));
        result.setAreaBaseFee(BigDecimal.ZERO);
        result.setBaseFee(new BigDecimal(20459.84));
//        result.setSourceTypeCodes(Arrays.asList("source1", "source2", code));
        return result; 
    }
}
