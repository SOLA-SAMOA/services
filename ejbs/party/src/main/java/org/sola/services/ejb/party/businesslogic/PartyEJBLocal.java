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
package org.sola.services.ejb.party.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.party.repository.entities.*;

/**
 * Local interface for the {@linkplain PartyEJB}
 */
@Local
public interface PartyEJBLocal extends AbstractEJBLocal {

    /**
     * See {@linkplain PartyEJB#getParty(java.lang.String)
     * PartyEJB.getParty}.
     */
    Party getParty(String id);

    /**
     * See {@linkplain PartyEJB#getParties(java.util.List)
     * PartyEJB.getParties}.
     */
    List<Party> getParties(List<String> partyIds);

    /**
     * See {@linkplain PartyEJB#saveParty(org.sola.services.ejb.party.repository.entities.Party)
     * PartyEJB.saveParty}.
     */
    Party saveParty(Party party);

    /**
     * See {@linkplain PartyEJB#getCommunicationTypes(java.lang.String)
     * PartyEJB.getCommunicationTypes}.
     */
    List<CommunicationType> getCommunicationTypes(String languageCode);

    /**
     * See {@linkplain PartyEJB#getPartyTypes(java.lang.String)
     * PartyEJB.getPartyTypes}.
     */
    List<PartyType> getPartyTypes(String languageCode);

    /**
     * See {@linkplain PartyEJB#getPartyRoles(java.lang.String)
     * PartyEJB.getPartyRoles}.
     */
    List<PartyRoleType> getPartyRoles(String languageCode);

    /**
     * See {@linkplain PartyEJB#getIdTypes(java.lang.String)
     * PartyEJB.getIdTypes}.
     */
    List<IdType> getIdTypes(String languageCode);

    /**
     * See {@linkplain PartyEJB#getGenderTypes(java.lang.String)
     * PartyEJB.getGenderTypes}.
     */
    List<GenderType> getGenderTypes(String languageCode);

    /**
     * See {@linkplain PartyEJB#getAgents()
     * PartyEJB.getAgents}.
     */
    List<Party> getAgents();
}
