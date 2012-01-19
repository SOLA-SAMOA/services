/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.cadastre.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

/**
 *
 * @author Elton Manoku
 */
@Table(name = "cadastre_object_node_target", schema = "cadastre")
public class CadastreObjectNodeTarget extends AbstractVersionedEntity {
    
     public static final String QUERY_WHERE_SEARCHBYTRANSACTION = 
             "transaction_id = #{transaction_id}";

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Id
    @Column(name = "node_id")
    private String nodeId;

    @Column(name = "geom")
    @AccessFunctions(onSelect = "st_asewkb(geom)",
    onChange = "get_geometry_with_srid(#{geom})")
    private byte[] geom;

    public byte[] getGeom() {
        return geom;
    }

    public void setGeom(byte[] geom) {
        this.geom = geom;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
}
