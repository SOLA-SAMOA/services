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
package org.sola.services.ejb.address.businesslogic;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.ejb.address.repository.entities.Address;

/**
 * EJB to manage data in the address schema. Supports retrieving and saving addresses.
 */
@Stateless
@EJB(name = "java:global/SOLA/AddressEJBLocal", beanInterface = AddressEJBLocal.class)
public class AddressEJB extends AbstractEJB implements AddressEJBLocal {

    /**
     * Returns the details for the specified address.
     *
     * <p>No role is required to execute this method.</p>
     *
     * @param id The identifier of the address to retrieve.
     */
    @Override
    public Address getAddress(String id) {
        return getRepository().getEntity(Address.class, id);
    }

    /**
     * Can be used to create a new address or save any updates to the details of an existing
     * address. <p>No role is required to execute this method.</p>
     *
     * @param address The address to create/save
     * @return The address after the save is completed.
     */
    @Override
    public Address saveAddress(Address address) {
        return getRepository().saveEntity(address);
    }

    /**
     * Returns a list of addresses matching the supplied ids. <p>No role is required to execute this
     * method.</p>
     *
     * @param ids The list of address ids.
     */
    @Override
    public List<Address> getAddresses(List<String> ids) {
        return getRepository().getEntityListByIds(Address.class, ids);
    }
}
