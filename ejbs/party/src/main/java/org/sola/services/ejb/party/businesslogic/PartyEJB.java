/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.services.ejb.party.businesslogic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.common.RolesConstants;
import org.sola.common.SOLAAccessException;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.ejb.address.repository.entities.Address;
import org.sola.services.ejb.party.repository.entities.*;

/**
 * EJB to manage data in the party schema. Supports retrieving and saving party details.
 */
@Stateless
@EJB(name = "java:global/SOLA/PartyEJBLocal", beanInterface = PartyEJBLocal.class)
public class PartyEJB extends AbstractEJB implements PartyEJBLocal {

    /**
     * Sets the entity package for the EJB to Party.class.getPackage().getName(). This is used to
     * restrict the save and retrieval of Code Entities.
     *
     * @see AbstractEJB#getCodeEntity(java.lang.Class, java.lang.String, java.lang.String)
     * AbstractEJB.getCodeEntity
     * @see AbstractEJB#getCodeEntityList(java.lang.Class, java.lang.String)
     * AbstractEJB.getCodeEntityList
     * @see
     * AbstractEJB#saveCodeEntity(org.sola.services.common.repository.entities.AbstractCodeEntity)
     * AbstractEJB.saveCodeEntity
     */
    @Override
    protected void postConstruct() {
        setEntityPackage(Party.class.getPackage().getName());
    }

    /**
     * Returns the details for the specified party.
     *
     * <p>No role is required to execute this method.</p>
     *
     * @param id The identifier of the party to retrieve.
     */
    @Override
    public Party getParty(String id) {
        return getRepository().getEntity(Party.class, id);
    }

    /**
     * Returns a list of parties matching the supplied ids. <p>No role is required to execute this
     * method.</p>
     *
     * @param partyIds The list of party ids
     */
    @Override
    public List<Party> getParties(List<String> partyIds) {
        return getRepository().getEntityListByIds(Party.class, partyIds);
    }

    /**
     * Can be used to create a new party or save any updates to the details of an existing party.
     * <p>Requires the {@linkplain RolesConstants#PARTY_RIGHTHOLDERS_SAVE} or
     * {@linkplain RolesConstants#PARTY_SAVE} role.</p>
     *
     * @param party The party to create/save
     * @return The party after the save is completed.
     * @throws SOLAAccessException Where the party being saved is a right holder but the user does
     * not have the {@linkplain RolesConstants#PARTY_RIGHTHOLDERS_SAVE} role
     */
    @Override
    @RolesAllowed({RolesConstants.PARTY_SAVE, RolesConstants.PARTY_RIGHTHOLDERS_SAVE})
    public Party saveParty(Party party) {
        if (party.isRightHolder() && !isInRole(RolesConstants.PARTY_RIGHTHOLDERS_SAVE)) {
            throw new SOLAAccessException();
        }
        return getRepository().saveEntity(party);
    }

    /**
     * Retrieves all party.communication_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<CommunicationType> getCommunicationTypes(String languageCode) {
        return getRepository().getCodeList(CommunicationType.class, languageCode);
    }

    /**
     * Retrieves all party.party_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<PartyType> getPartyTypes(String languageCode) {
        return getRepository().getCodeList(PartyType.class, languageCode);
    }

    /**
     * Retrieves all party.id_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<IdType> getIdTypes(String languageCode) {
        return getRepository().getCodeList(IdType.class, languageCode);
    }

    /**
     * Retrieves all party.gender_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<GenderType> getGenderTypes(String languageCode) {
        return getRepository().getCodeList(GenderType.class, languageCode);
    }

    /**
     * Returns all parties that have the lodgingAgent party role. Note that the address and party
     * role details for each agent are not loaded. <p>No role is required to execute this
     * method.</p>
     */
    @Override
    public List<Party> getAgents() {
        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_WHERE_PART, Party.QUERY_WHERE_LODGING_AGENTS);

        // Don't load Address as it is not required for the agents list. 
        getRepository().setLoadInhibitors(new Class<?>[]{Address.class});
        List<Party> agents = getRepository().getEntityList(Party.class, params);
        getRepository().clearLoadInhibitors();

        // Customizatino for SOLA Samoa - determine the primary role of the agent  see LH #27
        for (Party p : agents) {
            for (PartyRole pr : p.getRoleList()) {
                if (PartyRoleType.ROLE_CODE_LAWYER.equals(pr.getRoleCode())) {
                    p.setPrimaryRole(pr.getRoleCode());
                    break;
                }
                if (PartyRoleType.ROLE_CODE_SURVEYOR.equals(pr.getRoleCode())) {
                    p.setPrimaryRole(pr.getRoleCode());
                }
            }
        }
        return agents;
    }

    /**
     * Retrieves all party.party_role_type code values.
     *
     * @param languageCode The language code to use for localization of display values.
     */
    @Override
    public List<PartyRoleType> getPartyRoles(String languageCode) {
        return getRepository().getCodeList(PartyRoleType.class, languageCode);
    }
}
