/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.source.repository.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.ChildEntity;
import org.sola.services.common.repository.ExternalEJB;
import org.sola.services.common.repository.RepositoryUtility;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;
import org.sola.services.digitalarchive.businesslogic.DigitalArchiveEJBLocal;
import org.sola.services.digitalarchive.repository.entities.Document;
import org.sola.services.ejb.system.br.Result;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;
import org.sola.services.ejb.transaction.businesslogic.TransactionEJBLocal;
import org.sola.services.ejb.transaction.repository.entities.Transaction;
import org.sola.services.ejb.transaction.repository.entities.TransactionStatusType;

/**
 * Entity representing the source.source table. 
 * @author soladev
 */
@Table(name = "source", schema = "source")
public class Source extends AbstractVersionedEntity {

    public static final String QUERY_PARAMETER_TRANSACTIONID = "transactionId";
    public static final String QUERY_WHERE_BYTRANSACTIONID = "transaction_id = "
            + "#{" + QUERY_PARAMETER_TRANSACTIONID + "}";
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "maintype")
    private String mainType;
    @Column(name = "type_code")
    private String typeCode;
    @Column(name = "la_nr")
    private String laNr;
    @Column(name = "reference_nr")
    private String referenceNr;
    @Column(name = "archive_id")
    private String archiveId;
    @Column(name = "acceptance")
    private Date acceptance;
    @Column(name = "recordation")
    private Date recordation;
    @Column(name = "submission")
    private Date submission;
    @Column(name = "ext_archive_id")
    private String archiveDocumentId;
    @Column(name = "availability_status_code")
    private String availabilityStatusCode;
    @Column(name = "content")
    private String content;
    @Column(name = "status_code", updatable = false)
    private String statusCode;
    @Column(name = "transaction_id", updatable = false)
    private String transactionId;
    @ExternalEJB(ejbLocalClass = DigitalArchiveEJBLocal.class,
    loadMethod = "getDocumentInfo")
    @ChildEntity(childIdField = "archiveDocumentId")
    private Document archiveDocument;
    @Column(name="owner_name")
    private String ownerName;
    @Column
    private String version;
    @Column
    private String description;
    private Boolean locked = null;
    private String laNrReferenceId = null; 

    public Source() {
        super();
    }

    private String generateSourceNumber() {
        String result = "";
        SystemEJBLocal systemEJB = RepositoryUtility.tryGetEJB(SystemEJBLocal.class);
        if (systemEJB != null) {
            HashMap<String, Serializable> params = new HashMap<String, Serializable>(); 
            params.put("refId", getLaNrReferenceId()); 
            Result newNumberResult = systemEJB.checkRuleGetResultSingle("generate-source-nr", params);
            if (newNumberResult != null && newNumberResult.getValue() != null) {
                result = newNumberResult.getValue().toString();
            }
        }
        return result;
    }

    private Transaction getTransaction() {
        Transaction result = null;
        TransactionEJBLocal transactionEJB = RepositoryUtility.tryGetEJB(TransactionEJBLocal.class);
        if (transactionEJB != null) {
            result = transactionEJB.getTransactionById(getTransactionId(), Transaction.class);
        }
        return result;
    }

    public String getId() {
        id = id == null ? generateId() : id;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatusCode() {
        return this.statusCode;
    }

    public void setStatusCode(String statusCode) {
        // Prevent changes to the status code if the value has been loaded from the database
        if (isNew()) {
            this.statusCode = statusCode;
        }
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        // Prevent changes to the transaction id if the value has been loaded from the database. 
        if (isNew()) {
            this.transactionId = transactionId;
        }
    }

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

    public String getAvailabilityStatusCode() {
        return availabilityStatusCode;
    }

    public void setAvailabilityStatusCode(String availabilityStatusCode) {
        this.availabilityStatusCode = availabilityStatusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getArchiveDocumentId() {
        return archiveDocumentId;
    }

    public void setArchiveDocumentId(String extArchiveId) {
        this.archiveDocumentId = extArchiveId;
    }

    public String getLaNr() {
        return laNr;
    }

    public void setLaNr(String laNr) {
        this.laNr = laNr;
    }

    public String getMainType() {
        return mainType;
    }

    public void setMainType(String mainType) {
        this.mainType = mainType;
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

    public Document getArchiveDocument() {
        return archiveDocument;
    }

    public void setArchiveDocument(Document archiveDocument) {
        this.archiveDocument = archiveDocument;
        if (archiveDocument != null) {
            this.setArchiveDocumentId(archiveDocument.getId());
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
        
        public String getLaNrReferenceId() {
        return laNrReferenceId;
    }

    /**
     * The id of an entity (Application, RRR, BAUnit, Transaction) that can
     * be used to determine the appropriate laNr number for the source
     * during number generation. 
     * @param laNrReferenceId 
     */
    public void setLaNrReferenceId(String laNrReferenceId) {
        this.laNrReferenceId = laNrReferenceId;
    }
    
    public Boolean isLocked() {
        if (locked == null) {
            locked = false;
            Transaction transaction = getTransaction();
            if (transaction != null
                    && transaction.getStatusCode().equals(TransactionStatusType.COMPLETED)) {
                locked = true;
            }
        }
        return locked;
    }

    @Override
    public void preSave() {

        if (isNew() && getLaNr() == null) {
            // Assign a generated number to the source if it is not currently set. 
            setLaNr(generateSourceNumber());
        }

        // Call super.preSave after updates to the Source are completed. 
        super.preSave();

    }
}
