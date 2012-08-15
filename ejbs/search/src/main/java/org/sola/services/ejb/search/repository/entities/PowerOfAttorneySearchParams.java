package org.sola.services.ejb.search.repository.entities;

public class PowerOfAttorneySearchParams extends SourceSearchParams {
    private String attorneyName;
    private String personName;
    
    public PowerOfAttorneySearchParams(){
        super();
    }

    public String getAttorneyName() {
        return attorneyName;
    }

    public void setAttorneyName(String attorneyName) {
        this.attorneyName = attorneyName;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }
}
