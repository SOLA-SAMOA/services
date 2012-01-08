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
package org.sola.services.ejb.search.repository;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.sola.services.common.entities.AbstractResultEntity;

@Entity
@Table(name = "source", schema = "source")
@NamedNativeQueries({
    @NamedNativeQuery(name = "SourceSummary.searchSources",
    query = "SELECT id, la_nr, reference_nr, archive_id, type_code, get_translation(st.display_value,'en') " 
    + "AS typeDisplayValue, acceptance, recordation, submission, status_code, get_translation(t.display_value,:locale) AS statusDisplayValue "
    + "FROM (source.source AS s LEFT JOIN transaction.reg_status_type AS t on s.status_code = t.code) "
    + "LEFT JOIN source.administrative_source_type AS st ON s.type_code = st.code "
    + "WHERE (type_code = :typeCode OR COALESCE(:typeCode, '') = '') "
    + "AND POSITION(COALESCE(:laNumber, '') IN COALESCE(la_nr, '')) > 0 "
    + "AND POSITION(COALESCE(:refNumber, '') IN COALESCE(reference_nr, '')) > 0 "
    + "AND (recordation BETWEEN :fromRecordationDate AND :toRecordationDate OR (recordation IS NULL)) "
    + "AND (submission BETWEEN :fromSubmissionDate AND :toSubmissionDate OR (submission IS NULL)) "
    + "LIMIT 101",
    readOnly = true,
    resultClass = SourceSearchResult.class)
})
public class SourceSearchResult extends AbstractResultEntity {

    @Column(name = "la_nr")
    private String laNr;
    
    @Column(name = "reference_nr")
    private String referenceNr;
    
    @Column(name = "archive_id")
    private String archiveId;
    
    @Column(name = "type_code")
    private String typeCode;
    
    @Column(name = "typeDisplayValue")
    private String typeDisplayValue;
    
    @Column(name = "acceptance")
    @Temporal(TemporalType.DATE)
    private Date acceptance;
    
    @Column(name = "recordation")
    @Temporal(TemporalType.DATE)
    private Date recordation;
    
    @Column(name = "submission")
    @Temporal(TemporalType.DATE)
    private Date submission;
    
    @Column(name = "statusDisplayValue")
    private String statusDisplayValue;
    
    @Column(name = "status_code")
    private String statusCode;
 
    public Date getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Date acceptance) {
        this.acceptance = acceptance;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getLaNr() {
        return laNr;
    }

    public void setLaNr(String laNr) {
        this.laNr = laNr;
    }

    public Date getRecordation() {
        return recordation;
    }

    public void setRecordation(Date recordation) {
        this.recordation = recordation;
    }

    public String getReferenceNr() {
        return referenceNr;
    }

    public void setReferenceNr(String referenceNr) {
        this.referenceNr = referenceNr;
    }

    public String getStatusDisplayValue() {
        return statusDisplayValue;
    }

    public void setStatusDisplayValue(String statusDisplayValue) {
        this.statusDisplayValue = statusDisplayValue;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public Date getSubmission() {
        return submission;
    }

    public void setSubmission(Date submission) {
        this.submission = submission;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeDisplayValue() {
        return typeDisplayValue;
    }

    public void setTypeDisplayValue(String typeDisplayValue) {
        this.typeDisplayValue = typeDisplayValue;
    }
}
