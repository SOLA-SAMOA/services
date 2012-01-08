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
 * Food and Agriculture Orgainsation (FAO) of the United Nations
 * Solutions for Open Source Land Administration  - Sola.
 */
package org.sola.services.ejb.source.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractSolaTransactionEJBLocal;
import org.sola.services.ejb.source.repository.entities.AvailabilityStatus;
import org.sola.services.ejb.source.repository.entities.PresentationFormType;
import org.sola.services.ejb.source.repository.entities.Source;
import org.sola.services.ejb.source.repository.entities.SourceType;

/**
 * 
 * @author dounnaah, manoku
 * 
 */
@Local
public interface SourceEJBLocal extends AbstractSolaTransactionEJBLocal {

    public Source saveSource(Source source);

    public Source getSourceById(String id);

    public List<Source> getAllsources();

    List<Source> getSources(List<String> sourceIds);

    List<SourceType> getSourceTypes(String languageCode);

    List<AvailabilityStatus> getAvailabilityStatusList(String languageCode);

    List<PresentationFormType> getPresentationFormTypes(String languageCode);

    Source attachSourceToTransaction(String serviceId, String sourceId, String languageCode);

    boolean dettachSourceFromTransaction(String sourceId);

    List<Source> getSourcesByServiceId(String serviceId);
    
    List<Source> getSourcesByIds(List<String> sourceIds);
}
