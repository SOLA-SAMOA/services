package org.sola.services.ejb.source.repository.entities;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntity;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

@Table(name="power_of_attorney", schema="source")
public class PowerOfAttorney extends AbstractVersionedEntity {
    public static final String QUERY_PARAMETER_TRANSACTIONID = "transactionId";
    public static final String QUERY_GET_BY_TRANSACTION_ID = "select p.id, p.person_name, p.attorney_name "
            + "from source.source s inner join source.power_of_attorney p on s.id=p.id "
            + "where s.transaction_id = #{" + QUERY_PARAMETER_TRANSACTIONID + "}";
    
    @Id
    @Column
    private String id;
    
    @ChildEntity(childIdField="id", readOnly=true)
    private Source source;
    
    @Column(name="person_name")
    private String personName;
    
    @Column(name="attorney_name")
    private String attorneyName;
    
    public PowerOfAttorney(){
        super();
    }

    public String getAttorneyName() {
        return attorneyName;
    }

    public void setAttorneyName(String attorneyName) {
        this.attorneyName = attorneyName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
