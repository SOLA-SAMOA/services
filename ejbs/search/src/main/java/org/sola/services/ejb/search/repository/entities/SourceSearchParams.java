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
package org.sola.services.ejb.search.repository.entities;

import java.util.Date;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

public class SourceSearchParams extends AbstractReadOnlyEntity {
    private String locale;
    private String typeCode;
    private String laNumber;
    private String refNumber;
    private Date fromRecordationDate;
    private Date toRecordationDate;
    private Date fromSubmissionDate;
    private Date toSubmissionDate;
    private String ownerName;
    private String version;
    private String description;
    
    public SourceSearchParams(){
        super();
    }

    public Date getFromRecordationDate() {
        return fromRecordationDate;
    }

    public void setFromRecordationDate(Date fromRecordationDate) {
        this.fromRecordationDate = fromRecordationDate;
    }

    public String getLaNumber() {
        return laNumber;
    }

    public void setLaNumber(String laNumber) {
        this.laNumber = laNumber;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Date getToRecordationDate() {
        return toRecordationDate;
    }

    public void setToRecordationDate(Date toRecordationDate) {
        this.toRecordationDate = toRecordationDate;
    }

    public Date getFromSubmissionDate() {
        return fromSubmissionDate;
    }

    public void setFromSubmissionDate(Date fromSubmissionDate) {
        this.fromSubmissionDate = fromSubmissionDate;
    }

    public Date getToSubmissionDate() {
        return toSubmissionDate;
    }

    public void setToSubmissionDate(Date toSubmissionDate) {
        this.toSubmissionDate = toSubmissionDate;
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
}
