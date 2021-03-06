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
package org.sola.services.ejb.transaction.repository.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntity;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.ExternalEJB;
import org.sola.services.ejb.cadastre.businesslogic.CadastreEJBLocal;
import org.sola.services.ejb.cadastre.repository.entities.UnitParcelGroup;

/**
 *
 * @author soladev
 */
@Table(name = "transaction", schema = "transaction")
public class TransactionUnitParcels extends Transaction {

    /**
     * Query Parameter: transactionId *
     */
    public static final String QUERY_PARAMETER_TRANSACTIONID = "transactionId";
    /**
     * Retrieves the spatial unit group id from the transaction *
     */
    public static final String QUERY_SCALAR_GETSPATIALUNITGROUPID =
            " SELECT spatial_unit_group_id "
            + " FROM transaction.transaction "
            + " WHERE id = #{" + QUERY_PARAMETER_TRANSACTIONID + "} ";
    @ChildEntityList(parentIdField = "transactionId")
    private List<TransactionSource> transactionSourceList;
    @Column(name = "spatial_unit_group_id")
    private String unitParcelGroupId;
    @ChildEntity(childIdField = "unitParcelGroupId")
    @ExternalEJB(ejbLocalClass = CadastreEJBLocal.class,
    loadMethod = "getUnitParcelGroup",
    saveMethod = "saveUnitParcelGroup")
    private UnitParcelGroup unitParcelGroup;

    public List<TransactionSource> getTransactionSourceList() {
        return transactionSourceList;
    }

    public void setTransactionSourceList(List<TransactionSource> transactionSourceList) {
        this.transactionSourceList = transactionSourceList;
    }

    public UnitParcelGroup getUnitParcelGroup() {
        return unitParcelGroup;
    }

    public void setUnitParcelGroup(UnitParcelGroup unitParcelGroup) {
        this.unitParcelGroup = unitParcelGroup;
    }

    public String getUnitParcelGroupId() {
        return unitParcelGroupId;
    }

    public void setUnitParcelGroupId(String unitParcelGroupId) {
        this.unitParcelGroupId = unitParcelGroupId;
    }
}
