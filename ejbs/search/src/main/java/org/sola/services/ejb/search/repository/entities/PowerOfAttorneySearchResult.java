package org.sola.services.ejb.search.repository.entities;

import javax.persistence.Column;
import org.sola.services.common.repository.CommonSqlProvider;

public class PowerOfAttorneySearchResult extends SourceSearchResult {
    public static final String QUERY_PARAM_PERSON_NAME = "personName";
    public static final String QUERY_PARAM_ATTORNEY_NAME = "attorneyName";
    
    public static final String SEARCH_POWER_OF_ATTORNEY_QUERY =
            "SELECT p.id, s.la_nr, s.reference_nr, s.archive_id, s.ext_archive_id, s.type_code, "
            + " get_translation(st.display_value, #{" + CommonSqlProvider.PARAM_LANGUAGE_CODE + "}) AS typeDisplayValue, "
            + " s.acceptance, s.recordation, s.submission, s.status_code, p.person_name, p.attorney_name,"
            + " get_translation(t.display_value, #{" + CommonSqlProvider.PARAM_LANGUAGE_CODE + "}) AS statusDisplayValue "
            + " FROM ((source.source AS s INNER JOIN source.power_of_attorney p ON s.id=p.id)"
            + " LEFT JOIN transaction.reg_status_type AS t on s.status_code = t.code) "
            + " LEFT JOIN source.administrative_source_type AS st ON s.type_code = st.code "
            + " WHERE POSITION(COALESCE(#{" + QUERY_PARAM_LA_NUMBER + "}, '') IN COALESCE(s.la_nr, '')) > 0 "
            + " AND POSITION(COALESCE(#{" + QUERY_PARAM_REF_NUMBER + "}, '') IN COALESCE(s.reference_nr, '')) > 0 "
            + " AND (s.submission BETWEEN #{" + QUERY_PARAM_FROM_SUBMISSION_DATE + "} "
            + " AND #{" + QUERY_PARAM_TO_SUBMISSION_DATE + "} OR (s.submission IS NULL)) "
            + " AND POSITION(COALESCE(#{" + QUERY_PARAM_PERSON_NAME + "}, '') IN COALESCE(p.person_name, '')) > 0"
            + " AND POSITION(COALESCE(#{" + QUERY_PARAM_ATTORNEY_NAME + "}, '') IN COALESCE(p.attorney_name, '')) > 0"
            + " LIMIT 101";
    
    @Column(name="attorney_name")
    private String attorneyName;
    @Column(name="person_name")
    private String personName;
    
    public PowerOfAttorneySearchResult(){
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
