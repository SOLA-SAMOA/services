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
package org.sola.services.digitalarchive.businesslogic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import org.sola.common.DateUtility;
import org.sola.common.FileUtility;
import org.sola.common.RolesConstants;
import org.sola.common.logging.LogUtility;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.digitalarchive.repository.entities.Document;
import org.sola.services.digitalarchive.repository.entities.FileInfo;
import org.sola.services.digitalarchive.repository.entities.FileBinary;
import org.sola.services.common.repository.CommonSqlProvider; 

/**
 * 
 * Provides methods to work with digital archive.
 */
@Stateless
@EJB(name = "java:global/SOLA/DigitalArchiveEJBLocal", beanInterface = DigitalArchiveEJBLocal.class)
public class DigitalArchiveEJB extends AbstractEJB implements DigitalArchiveEJBLocal {

    private File scanFolder;
    private File thumbFolder;
    private int thumbWidth;
    private int thumbHeight; 

    @Override
    protected void postConstruct() {
        
        // TODO: Implement reading config from DB
        // Set user's home folder
        scanFolder = new File(System.getProperty("user.home") + "/sola/scan");
        thumbFolder = new File(scanFolder.getAbsolutePath() + File.separatorChar + "thumb");
        thumbWidth = 225;
        thumbHeight = 322;

        // Init folder
        if (!scanFolder.exists()) {
            new File(scanFolder.getAbsolutePath()).mkdirs();
        }

        if (!thumbFolder.exists()) {
            new File(thumbFolder.getAbsolutePath()).mkdirs();
        }
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public Document getDocument(String documentId) {
        Document result = null;
        if (documentId != null) {
            result = getRepository().getEntity(Document.class, documentId);
        }
        return result;
    }

    /** 
     * Returns the meta information recorded for the document but does not retrieve the actual
     * document content. 
     * @param documentId The id of the document to obtain meta data
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public Document getDocumentInfo(String documentId) {
        Document result = null;
        if (documentId != null) {
            Map params = new HashMap<String, Object>();
            params.put(CommonSqlProvider.PARAM_WHERE_PART, Document.QUERY_WHERE_BYID);
            params.put("id", documentId);
            // Exclude the body field from the generated SELECT statement
            params.put(CommonSqlProvider.PARAM_EXCLUDE_LIST, Arrays.asList("body"));
            result = getRepository().getEntity(Document.class, params);
        }
        return result;
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document saveDocument(Document document) {
        return getRepository().saveEntity(document);
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document createDocument(Document document) {
        document.setNr(allocateNr());
        return saveDocument(document);
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document createDocument(Document document, String fileName) {
        if (fileName == null || document == null) {
            return null;
        }

        // Check if file exists in scan folder name to exclude jumping from the folder
        String filePath = getFullFilePath(fileName, scanFolder);
        if (filePath == null) {
            return null;
        }

        // Get file from shared folder
        byte[] fileBytes = FileUtility.getFileBinary(filePath);
        if (fileBytes == null) {
            return null;
        }

        document.setExtension(FileUtility.getFileExtesion(fileName));
        document.setBody(fileBytes);
        document.setDescription(fileName);
        document = createDocument(document);
        
        // TODO: Uncomment in production to deleted uploaded files
        //deleteFile(fileName);

        return document;
    }

    /**
     * Gets the next available number for the document
     * 
     */
    private String allocateNr() {
        String datePart = "";
        String numPart = null;

        Map params = new HashMap<String, Object>();
        params.put(CommonSqlProvider.PARAM_SELECT_PART, Document.QUERY_ALLOCATENR);
        numPart = getRepository().getScalar(Long.class, params).toString();

        if (numPart != null) {
            // Prefix with 0 to get a 4 digit number.
            while (numPart.length() < 4) {
                numPart = "0" + numPart;
            }
            if (numPart.length() > 4) {
                numPart = numPart.substring(numPart.length() - 4);
            }
            datePart = DateUtility.simpleFormat("yyMM");
        } else {
            // Use the current datetime
            numPart = DateUtility.simpleFormat("yyMMddHHmmss");
        }
        return datePart + numPart;
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public FileBinary getFileBinary(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        // Check if file exists in scan folder name to exclude jumping from the folder
        String filePath = getFullFilePath(fileName, scanFolder);

        if (filePath == null) {
            return null;
        }

        // Get file from shared folder
        byte[] fileBytes = FileUtility.getFileBinary(filePath);
        if (fileBytes == null) {
            return null;
        }

        File file = new File(filePath);
        FileBinary fileBinary = new FileBinary();
        fileBinary.setContent(fileBytes);
        fileBinary.setFileSize(file.length());
        fileBinary.setName(fileName);
        fileBinary.setModificationDate(new Date(file.lastModified()));
        return fileBinary;
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public FileBinary getFileThumbnail(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        // Check if file exists in scan folder name to exclude jumping from the folder
        String thumbName = getThumbName(fileName);

        String filePath = getFullFilePath(thumbName, thumbFolder);

        if (filePath == null) {
            // Try to create
            if (!createThumbnail(fileName)) {
                return null;
            }
            filePath = thumbFolder.getAbsolutePath() + File.separator + thumbName;
        }

        // Get thumbnail 
        byte[] fileBytes = FileUtility.getFileBinary(filePath);
        if (fileBytes == null) {
            return null;
        }

        File file = new File(filePath);
        FileBinary fileBinary = new FileBinary();
        fileBinary.setContent(fileBytes);
        fileBinary.setFileSize(file.length());
        fileBinary.setName(fileName);
        fileBinary.setModificationDate(new Date(file.lastModified()));
        return fileBinary;
    }

    /**
     * Construct thumbnail file name out of original file name
     * @param  fileName The name of original file to create thumbnail from
     * @return 
     */
    private String getThumbName(String fileName) {
        File tmpFile = new File(fileName);
        String thumbName = tmpFile.getName();

        if (thumbName.contains(".")) {
            thumbName = thumbName.substring(0, thumbName.lastIndexOf(".") - 1);
        }

        thumbName += ".jpg";
        return thumbName;
    }

    /**
     * Creates thumbnail image for the file in the shared folder
     * 
     * @param fileName The name of the file in the shared folder 
     */
    private boolean createThumbnail(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return false;
        }

        // Check if file exists in scan folder name to exclude jumping from the folder
        String filePath = getFullFilePath(fileName, scanFolder);
        String thumbPath = thumbFolder.getAbsolutePath() + File.separator + getThumbName(fileName);

        if (filePath == null) {
            return false;
        }

        try {
            BufferedImage image = FileUtility.createImageThumbnail(filePath, thumbWidth, -1);

            if (image == null) {
                return false;
            }

            File thumbFile = new File(thumbPath);
            if (thumbFile.exists()) {
                thumbFile.delete();
            }

            ImageIO.write(image, "JPEG", thumbFile);

        } catch (IOException ex) {
            LogUtility.log(ex.getLocalizedMessage(), Level.SEVERE);
            return false;
        }

        return true;
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public List<FileInfo> getAllFiles() {
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        // Read folder
        // TODO: Make folder filtering for appropriate files
        if (scanFolder != null && scanFolder.isDirectory()) {
            for (String fileName : scanFolder.list()) {
                File file = new File(scanFolder.getAbsolutePath() + File.separator + fileName);
                if (file.exists() && file.isFile()) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setModificationDate(new Date(file.lastModified()));
                    fileInfo.setFileSize(file.length());
                    fileInfo.setName(fileName);
                    fileInfoList.add(fileInfo);
                }
            }
        }
        // Sort list by modification date
        Collections.sort(fileInfoList, new FileInfoSorterByModificationDate());
        return fileInfoList;
    }

    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return false;
        }

        // Check if file exists in scan folder name to exclude jumping from the folder
        String filePath = getFullFilePath(fileName, scanFolder);
        String thumbnailPath = getFullFilePath(fileName, thumbFolder);

        if (filePath == null) {
            return false;
        }

        // Delete file
        File file = new File(filePath);

        boolean result = false;

        try {
            result = file.delete();
            // try to delete thumbnail
            if (result && thumbnailPath != null) {
                File thumbnail = new File(thumbnailPath + File.separator + fileName);
                if (thumbnail.exists()) {
                    thumbnail.delete();
                }
            }
        } catch (Throwable e) {
            LogUtility.log(e.toString(), Level.SEVERE);
        }

        return result;
    }

    /**
     * Returns the full path for the requested file in the shared folder. Used to
     * control the file name exists in a given folder and doesn't contain any 
     * dangerous characters to jump out the folder path.
     * 
     * @param fileName File name in the shared folder
     * @param folder The folder to check for the file name
     * @return 
     */
    private String getFullFilePath(String fileName, File folder) {
        if (folder != null && folder.isDirectory()) {
            for (String fName : folder.list()) {
                if (fName.equals(fileName)) {
                    return folder.getAbsolutePath() + File.separator + fileName;
                }
            }
        }
        return null;
    }
}
