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
package org.sola.services.ejb.source.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractSolaTransactionEJBLocal;
import org.sola.services.ejb.source.repository.entities.*;

/**
 * Local interface for the {@linkplain SourceEJB}
 */
@Local
public interface SourceEJBLocal extends AbstractSolaTransactionEJBLocal {

    /**
     * See {@linkplain SourceEJB#saveSource(org.sola.services.ejb.source.repository.entities.Source)
     * SourceEJB.saveSource}.
     */
    public Source saveSource(Source source);

    /**
     * See {@linkplain SourceEJB#getSourceById(java.lang.String)
     * SourceEJB.getSourceById}.
     */
    public Source getSourceById(String id);

    /**
     * See {@linkplain SourceEJB#getAllsources()
     * SourceEJB.getAllsources}.
     */
    public List<Source> getAllsources();

    /**
     * See {@linkplain SourceEJB#getSources(java.util.List)
     * SourceEJB.getSources}.
     */
    List<Source> getSources(List<String> sourceIds);

    /**
     * See {@linkplain SourceEJB#getSourceTypes(java.lang.String)
     * SourceEJB.getSourceTypes}.
     */
    List<SourceType> getSourceTypes(String languageCode);

    /**
     * See {@linkplain SourceEJB#getAvailabilityStatusList(java.lang.String)
     * SourceEJB.getAvailabilityStatusList}.
     */
    List<AvailabilityStatus> getAvailabilityStatusList(String languageCode);

    /**
     * See {@linkplain SourceEJB#getPresentationFormTypes(java.lang.String)
     * SourceEJB.getPresentationFormTypes}.
     */
    List<PresentationFormType> getPresentationFormTypes(String languageCode);

    /**
     * See {@linkplain SourceEJB#attachSourceToTransaction(java.lang.String, java.lang.String, java.lang.String)
     * SourceEJB.attachSourceToTransaction}.
     */
    Source attachSourceToTransaction(String serviceId, String sourceId, String languageCode);
    
    /**
     * Attaches Power of attorney to the transaction.
     */
    PowerOfAttorney attachPowerOfAttorneyToTransaction(String serviceId, PowerOfAttorney powerOfAttorney, String languageCode);
    
    /** Returns Power of attorney by id */
    PowerOfAttorney getPowerOfAttorneyById(String id);

    /**
     * See {@linkplain SourceEJB#dettachSourceFromTransaction(java.lang.String)
     * SourceEJB.dettachSourceFromTransaction}.
     */
    boolean dettachSourceFromTransaction(String sourceId);

    /**
     * See {@linkplain SourceEJB#getSourcesByServiceId(java.lang.String)
     * SourceEJB.getSourcesByServiceId}.
     */
    List<Source> getSourcesByServiceId(String serviceId);
    
    /**
     * See {@linkplain SourceEJB#getPowerOfAttorneyByServiceId(java.lang.String)
     * SourceEJB.getPowerOfAttorneyByServiceId}.
     */
    List<PowerOfAttorney> getPowerOfAttorneyByServiceId(String serviceId);
}
