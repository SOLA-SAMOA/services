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
package org.sola.services.common;

/**
 * Contains compile time constants for the SOLA Services.
 * @author amcdowell
 */
public class ServiceConstants {

    // These constants control the namespaces used for the SOLA Services and their TOs. These values
    // should not need to change unless it is necessary to support multiple deployed versions of the
    // SOLA Web Services. In this case, a version number could be added into each namespace to 
    // differentiate between the different versions of the web service interfaces. e.g. 
    // http://webservices.sola.org/v001/transferobjects/ or a date based version number such as
    // http://webservices.sola.org/v201201/transferobjects (using a date based version number is 
    // consistent with general guidelines from the W3C for XML namespace definitions). 
    public final static String BASE_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/";
    public final static String CASE_MAN_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/casemanagement/";
    public final static String REF_DATA_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/referencedata/";
    public final static String SECURITY_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/security/";
    public final static String CADASTRE_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/cadastre/";
    public final static String SEARCH_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/search/";
    public final static String FAULTS_NAMESPACE = "http://webservices.sola.org/faults/";
    public final static String CASE_MAN_WS_NAMESPACE = "http://webservices.sola.org/casemanagement";
    public final static String REF_DATA_WS_NAMESPACE = "http://webservices.sola.org/referencedata";
    public final static String CADASTRE_WS_NAMESPACE = "http://webservices.sola.org/cadastre";
    public final static String SEARCH_WS_NAMESPACE = "http://webservices.sola.org/search";
    public final static String DIGITAL_ARCHIVE_WS_NAMESPACE = "http://webservices.sola.org/digitalarchive";
    public final static String DIGITAL_ARCHIVE_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/digitalarchive/";
    public final static String SPATIAL_WS_NAMESPACE = "http://webservices.sola.org/spatial";
    public final static String ADMINISTRATIVE_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/administrative/";
    public final static String ADMINISTRATIVE_WS_NAMESPACE = "http://webservices.sola.org/administrative";
    public final static String ADMIN_WS_NAMESPACE = "http://webservices.sola.org/admin";
    public final static String TRANSACTION_TO_NAMESPACE = "http://webservices.sola.org/transferobjects/transaction/";
    public final static String FILE_STREAMING_WS_NAMESPACE = "http://webservices.sola.org/filestreaming";
}
