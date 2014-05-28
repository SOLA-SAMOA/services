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
package org.sola.services.digitalarchive.businesslogic;

import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.digitalarchive.repository.entities.Document;
import org.sola.services.digitalarchive.repository.entities.FileBinary;
import org.sola.services.digitalarchive.repository.entities.FileInfo;

/**
 * Local interface for the {@linkplain DigitalArchiveEJB}
 */
@Local
public interface DigitalArchiveEJBLocal extends AbstractEJBLocal {

    /**
     * See {@linkplain DigitalArchiveEJB#getDocument(java.lang.String)
     * AddressEJB.getDocument}.
     */
    public Document getDocument(String documentId);

    /**
     * See {@linkplain DigitalArchiveEJB#getDocumentInfo(java.lang.String)
     * AddressEJB.getDocumentInfo}.
     */
    public Document getDocumentInfo(String documentId);

    /**
     * See {@linkplain DigitalArchiveEJB#createDocument(org.sola.services.digitalarchive.repository.entities.Document)
     * AddressEJB.createDocument}.
     */
    public Document createDocument(Document document);

    /**
     * See {@linkplain DigitalArchiveEJB#createDocument(org.sola.services.digitalarchive.repository.entities.Document, java.lang.String)
     * AddressEJB.createDocument}.
     */
    public Document createDocument(Document document, String fileName);

    /**
     * See {@linkplain DigitalArchiveEJB#saveDocument(org.sola.services.digitalarchive.repository.entities.Document)
     * AddressEJB.saveDocument}.
     */
    public Document saveDocument(Document document);

    /**
     * See {@linkplain DigitalArchiveEJB#getFileBinary(java.lang.String)
     * AddressEJB.getFileBinary}.
     */
    public FileInfo getFileBinary(String fileName);

    /**
     * See {@linkplain DigitalArchiveEJB#getFileThumbnail(java.lang.String)
     * AddressEJB.getFileThumbnail}.
     */
    public FileInfo getFileThumbnail(String fileName);

    /**
     * See {@linkplain DigitalArchiveEJB#getAllFiles()
     * AddressEJB.getAllFiles}.
     */
    public List<FileInfo> getAllFiles();

    /**
     * See {@linkplain DigitalArchiveEJB#deleteFile(java.lang.String)
     * AddressEJB.deleteFile}.
     */
    public boolean deleteFile(String fileName);
}
