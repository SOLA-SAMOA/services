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
package org.sola.services.common.faults;

import java.util.ArrayList;
import java.util.List;
import org.sola.services.common.br.ValidationResult;

/**
 * The Java type that goes as soapenv:Fault detail element.
 * Used in web services exceptions, fault beans just hold the details
 * of the SOAP fault.
 * See http://io.typepad.com/eben_hewitt_on_java/2009/07/using-soap-faults-and-exceptions-in-java-jaxws-web-services.html.
 * 
 * @author soladev
 */
public class FaultInfoBean {

    private String messageCode;
    private List<String> messageParameters;
    // Identifier for the fault that can be used to correlate the fault message
    // displayed to the user back to the fault details recorded in the server
    // exception log.
    private String faultId;
    
    private List<ValidationResult> validationResultList;

    public FaultInfoBean() {
    }

    public String getFaultId() {
        return faultId;
    }

    public void setFaultId(String faultId) {
        this.faultId = faultId;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public List<String> getMessageParameters() {
        return messageParameters;
    }

    public void setMessageParameters(List<String> messageParameters) {
        this.messageParameters = messageParameters;
    }

    public void addMessageParameter(String param) {
        if (this.messageParameters == null) {
            this.messageParameters = new ArrayList<String>();

        }
        this.messageParameters.add(param);
    }

    public List<ValidationResult> getValidationResultList() {
        return validationResultList;
    }

    public void setValidationResultList(List<ValidationResult> validationResultList) {
        this.validationResultList = validationResultList;
    }
    
}
