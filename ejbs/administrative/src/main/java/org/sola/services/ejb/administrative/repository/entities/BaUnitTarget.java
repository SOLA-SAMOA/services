/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.administrative.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

@Table(name = "ba_unit_target", schema = "administrative")
public class BaUnitTarget extends AbstractVersionedEntity{
    @Id
    @Column(name = "ba_unit_id")
    private String baUnitId;
    @Id
    @Column(name = "transaction_id")
    private String transactionId;
    public static final String QUERY_WHERE_GET_BY_TRANSACTION = 
             "transaction_id = #{transaction_id}";
    
    public BaUnitTarget(){
        super();
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
