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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.imageio.ImageIO;
import org.sola.common.ConfigConstants;
import org.sola.common.DateUtility;
import org.sola.common.FileMetaData;
import org.sola.common.FileUtility;
import org.sola.common.NetworkFolder;
import org.sola.common.RolesConstants;
import org.sola.common.logging.LogUtility;
import org.sola.common.mapping.MappingManager;
import org.sola.common.mapping.MappingUtility;
import org.sola.services.common.ejbs.AbstractEJB;
import org.sola.services.common.repository.CommonSqlProvider;
import org.sola.services.digitalarchive.repository.entities.Document;
import org.sola.services.digitalarchive.repository.entities.FileBinary;
import org.sola.services.digitalarchive.repository.entities.FileInfo;
import org.sola.services.ejb.system.businesslogic.SystemEJBLocal;

/**
 * EJB to manage data in the document schema. Supports retrieving and saving
 * digital documents including functions to create a document from a file or
 * generate a thumbnail image for a image file. <p>The default Network Scan
 * folder location is <b>user.home/sola/scan</b> where user.home is the home
 * folder of the user account running the Glassfish instance.</p>
 */
@Stateless
@EJB(name = "java:global/SOLA/DigitalArchiveEJBLocal", beanInterface = DigitalArchiveEJBLocal.class)
public class DigitalArchiveEJB extends AbstractEJB implements DigitalArchiveEJBLocal {

    @EJB
    private SystemEJBLocal systemEJB;
    // The location of the network scan folder. This may be on another computer. 
    private NetworkFolder scanFolder;
    // The local cache folder is used to manipulate the files from the scan folder.
    // This folder is always located in the SOLA documents cache on the local computer.
    private NetworkFolder localCacheFolder;
    private NetworkFolder thumbFolder;
    private int thumbWidth;
    private int thumbHeight;
    private static String THUMB_SUBFOLDER = "thumb";

    /**
     * Configures the default network location to read scanned images as well as
     * the default folder to use for generating thumbnail images.
     */
    @Override
    protected void postConstruct() {

        String scanFolderLocation = systemEJB.getSetting(ConfigConstants.NETWORK_SCAN_FOLDER,
                System.getProperty("user.home") + "/sola/scan");
        String domain = systemEJB.getSetting(ConfigConstants.NETWORK_SCAN_FOLDER_DOMAIN, null);
        String shareUser = systemEJB.getSetting(ConfigConstants.NETWORK_SCAN_FOLDER_USER, null);
        String pword = systemEJB.getSetting(ConfigConstants.NETWORK_SCAN_FOLDER_PASSWORD, null);

        if (domain != null || shareUser != null) {
            scanFolder = new NetworkFolder(scanFolderLocation, domain, shareUser, pword);
        } else {
            scanFolder = new NetworkFolder(scanFolderLocation);
        }

        try {
            scanFolder.createFolder();
        } catch (Exception ex) {
            // The most likely cause of an exception during createFolder is the unavailablity of
            // the Network scan folder. Configure the scan folder to use a location on the 
            // local hard disk and log the error
            LogUtility.log("Error configuring Network Scan Folder", ex);
            scanFolder = new NetworkFolder(System.getProperty("user.home") + "/sola/scan");
            scanFolder.createFolder();
        }
        localCacheFolder = new NetworkFolder(FileUtility.getCachePath());
        localCacheFolder.createFolder();
        thumbFolder = localCacheFolder.getSubFolder(THUMB_SUBFOLDER);

        // Increse the size of the "thumbnail" so there is more information 
        // in the picture when the user resizes the file dialog. 
        thumbWidth = 500;
        thumbHeight = 707;

        // Set some cache values for the server documents cache. 
        String maxCacheSizeMB = systemEJB.getSetting(ConfigConstants.SERVER_DOCUMENT_CACHE_MAX_SIZE, "500");
        long maxCacheSizeBytes = Long.parseLong(maxCacheSizeMB) * 1024 * 1024;
        FileUtility.setMaxCacheSizeBytes(maxCacheSizeBytes);

        String maxCacheResizedMB = systemEJB.getSetting(ConfigConstants.SERVER_DOCUMENT_CACHE_RESIZED, "200");
        long maxCacheResizeBytes = Long.parseLong(maxCacheResizedMB) * 1024 * 1024;
        FileUtility.setResizedCacheSizeBytes(maxCacheResizeBytes);

        FileUtility.setCachePath(systemEJB.getSetting(ConfigConstants.SERVER_DOCUMENT_CACHE_FOLDER,
                FileUtility.getCachePath()));
    }

    /**
     * Retrieves the document for the specified identifier. This includes the
     * document content (i.e the digital file). <p>Requires the
     * {@linkplain RolesConstants#SOURCE_SEARCH} role.</p>
     *
     * @param documentId Identifier of the document to retrieve
     */
    @Override
    @RolesAllowed({RolesConstants.SOURCE_SEARCH, RolesConstants.APPLICATION_VIEW_APPS})
    public Document getDocument(String documentId) {
        Document result = null;
        if (documentId != null) {
            result = getRepository().getEntity(Document.class, documentId);

        }
        return result;
    }

    /**
     * Returns the meta information recorded for the document but does not
     * retrieve the actual document content.<p>Requires the
     * {@linkplain RolesConstants#SOURCE_SEARCH} role.</p>
     *
     * @param documentId The id of the document to retrieve
     * @see
     */
    @Override
    @RolesAllowed({RolesConstants.SOURCE_SEARCH, RolesConstants.APPLICATION_VIEW_APPS})
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

    /**
     * Can be used to create a new document or save any updates to the details
     * of an existing document. <p>Requires the
     * {@linkplain RolesConstants#SOURCE_SAVE} role.</p>
     *
     * @param document The document to create/save.
     * @return The document after the save is completed.
     * @see
     * #createDocument(org.sola.services.digitalarchive.repository.entities.Document)
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document saveDocument(Document document) {
        return getRepository().saveEntity(document);
    }

    /**
     * Can be used to create a new document. Also assigns the document number.
     * <p>Requires the {@linkplain RolesConstants#SOURCE_SAVE} role.</p>
     *
     * @param document The document to create.
     * @return The document after the save is completed.
     * @see
     * #saveDocument(org.sola.services.digitalarchive.repository.entities.Document)
     * saveDocument
     * @see
     * #createDocument(org.sola.services.digitalarchive.repository.entities.Document,
     * java.lang.String) createDocument
     * @see #allocateNr() allocateNr
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document createDocument(Document document) {
        document.setNr(allocateNr());
        return saveDocument(document);
    }

    /**
     * Determines the local file path name for the given file
     *
     * @param fileName
     * @return
     */
    private String getLocalFilePathName(String fileName) {
        return localCacheFolder.getPath() + fileName;
    }

    /**
     * Can be used to create a new document with the digital content obtained
     * from the specified file. Used to create documents from the network scan
     * folder. After the digital file is loaded, it is deleted from the network
     * scan folder. <p>Requires the {@linkplain RolesConstants#SOURCE_SAVE}
     * role.</p>
     *
     * @param document The document to create.
     * @param fileName The filename of the digital file to save with the
     * document.
     * @return The document after the save is completed.
     * @see
     * #createDocument(org.sola.services.digitalarchive.repository.entities.Document)
     * createDocument
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public Document createDocument(Document document, String fileName) {
        if (fileName == null || document == null) {
            return null;
        }

        FileMetaData fileMetaData = scanFolder.getMetaData(fileName);
        if (fileMetaData == null) {
            //File no longer exists in the scan folder. 
            return null;
        }

        FileMetaData localFileMetaData = localCacheFolder.getMetaData(fileName);
        if (localFileMetaData == null || !DateUtility.areEqual(fileMetaData.getModificationDate(),
                localFileMetaData.getModificationDate())) {
            // Copy the file from the remote folder location to the local folder.
            localCacheFolder.deleteFile(fileName);
            thumbFolder.deleteFile(fileName);
            scanFolder.copyFileToLocal(fileName, new File(getLocalFilePathName(fileName)));
        }

        // Get file from shared folder
        byte[] fileBytes = FileUtility.getFileBinary(getLocalFilePathName(fileName));
        if (fileBytes == null) {
            return null;
        }

        document.setExtension(FileUtility.getFileExtension(fileName));
        document.setBody(fileBytes);
        document.setDescription(fileName);
        document = createDocument(document);

        // Delete the file from the network scan folder. 
        // Let the user manually delete the document in case they pick the
        // wrong one. 
        //deleteFile(fileName);

        return document;
    }

    /**
     * Determines the number to assign to the document based on the date and the
     * <code>document.document_nr_seq</code> number sequence.
     *
     * @return The allocated document number.
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

    /**
     * Loads the specified file from the Network Scan folder. Updated to avoid
     * loading the file. The file is uploaded using the FileStreaming service
     * instead. <p>Requires the {@linkplain RolesConstants#SOURCE_SEARCH}
     * role.</p>
     *
     * @param fileName The name of the file to load
     * @return The binary file along with some attributes of the file
     */
    @Override
    @RolesAllowed({RolesConstants.SOURCE_SEARCH, RolesConstants.APPLICATION_VIEW_APPS})
    public FileInfo getFileBinary(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        FileMetaData fileMetaData = scanFolder.getMetaData(fileName);
        if (fileMetaData == null) {
            //File no longer exists in the scan folder. 
            return null;
        }

        FileMetaData localFileMetaData = localCacheFolder.getMetaData(fileName);
        if (localFileMetaData == null || !DateUtility.areEqual(fileMetaData.getModificationDate(),
                localFileMetaData.getModificationDate())) {
            // Copy the file from the remote folder location to the local folder.
            localCacheFolder.deleteFile(fileName);
            thumbFolder.deleteFile(fileName);
            scanFolder.copyFileToLocal(fileName, new File(getLocalFilePathName(fileName)));
        }

        File file = new File(getLocalFilePathName(fileName));
        FileInfo fileBinary = new FileInfo();
        // The file will be uploaded from the disk by the FileStreaming Service
        // if needed. 
        //FileBinary fileBinary = new FileBinary();
        //fileBinary.setContent(fileBytes);
        fileBinary.setFileSize(file.length());
        fileBinary.setName(fileName);
        fileBinary.setModificationDate(new Date(file.lastModified()));
        return fileBinary;
    }

    /**
     * Loads the specified file from the Network Scan folder then generates a
     * thumbnail image of the file if one does not already exist. <p>Requires
     * the {@linkplain RolesConstants#SOURCE_SEARCH} role.</p>
     *
     * @param fileName The name of the file to load
     * @return A thumbnail image of the file
     * @see FileUtility#createImageThumbnail(java.lang.String, int, int)
     * FileUtility.createImageThumbnail
     */
    @Override
    @RolesAllowed({RolesConstants.SOURCE_SEARCH, RolesConstants.APPLICATION_VIEW_APPS})
    public FileInfo getFileThumbnail(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return null;
        }

        FileMetaData fileMetaData = scanFolder.getMetaData(fileName);
        if (fileMetaData == null) {
            //File no longer exists in the scan folder. 
            return null;
        }

        FileMetaData localFileMetaData = localCacheFolder.getMetaData(fileName);
        if (localFileMetaData == null || !DateUtility.areEqual(fileMetaData.getModificationDate(),
                localFileMetaData.getModificationDate())) {
            // Copy the file from the remote folder location to the local folder.
            localCacheFolder.deleteFile(fileName);
            thumbFolder.deleteFile(fileName);
            scanFolder.copyFileToLocal(fileName, new File(getLocalFilePathName(fileName)));
        }

        // Generate the name of the thumb and check if it exists
        String thumbName = getThumbName(fileName);
        String thumbFilePathName = thumbFolder.getPath() + thumbName;

        if (!thumbFolder.fileExists(thumbName)) {
            if (!createThumbnail(fileName)) {
                return null;
            }
        }

        // Get thumbnail 
        byte[] fileBytes = FileUtility.getFileBinary(thumbFilePathName);
        if (fileBytes == null) {
            return null;
        }

        File file = new File(thumbFilePathName);
        // Download the file using the File Streaming Service
        //FileBinary fileBinary = new FileBinary();
        //fileBinary.setContent(fileBytes);
        FileInfo fileBinary = new FileBinary();
        fileBinary.setFileSize(file.length());
        fileBinary.setName(thumbName);
        fileBinary.setModificationDate(fileMetaData.getModificationDate());
        return fileBinary;
    }

    /**
     * Construct thumbnail file name out of original file name
     *
     * @param fileName The name of original file to create thumbnail from
     */
    private String getThumbName(String thumbName) {
        if (thumbName.contains(".")) {
            thumbName = thumbName.substring(0, thumbName.lastIndexOf("."));
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
        String thumbName = getThumbName(fileName);
        String thumbFilePathName = thumbFolder.getPath() + thumbName;
        try {
            BufferedImage image = FileUtility.createImageThumbnail(getLocalFilePathName(fileName), thumbWidth, -1);

            if (image == null) {
                return false;
            }

            File thumbFile = new File(thumbFilePathName);
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

    /**
     * Retrieves the list of all files in the Network Scan Folder. Only meta
     * data about the file is returned. The content of the file is omitted to
     * avoid transferring a large amount of file data across the network.
     * <p>Requires the {@linkplain RolesConstants#SOURCE_SEARCH} role.</p>
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SEARCH)
    public List<FileInfo> getAllFiles() {
        List<FileMetaData> fileInfoList = scanFolder.getAllFiles(
                ".*pdf$|.*png$|.*jpg$|.*jpeg$|.*tif$|.*tiff$");

        List<FileInfo> result = new ArrayList<FileInfo>();
        MappingUtility.translateList(fileInfoList, result, FileInfo.class, MappingManager.getMapper());

        // Sort list by modification date
        Collections.sort(result, new FileInfoSorterByModificationDate());
        return result;
    }

    /**
     * Deletes the specified file from the Network Scan folder. Also attempts to
     * delete any thumbnail for the file if one exists. <p>Requires the
     * {@linkplain RolesConstants#SOURCE_SEARCH} role.</p>
     *
     * @param fileName The name of the file to delete.
     * @return true if the file is successfully deleted.
     */
    @Override
    @RolesAllowed(RolesConstants.SOURCE_SAVE)
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.equals("")) {
            return false;
        }
        scanFolder.deleteFile(fileName);
        thumbFolder.deleteFile(getThumbName(fileName));
        if (localCacheFolder != null) {
            localCacheFolder.deleteFile(fileName);
        }

        return true;
    }
}
