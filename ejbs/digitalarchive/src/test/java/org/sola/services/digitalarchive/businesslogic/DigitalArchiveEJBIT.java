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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sola.services.common.test.AbstractEJBTest;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.sola.services.digitalarchive.repository.entities.Document;
import org.sola.services.digitalarchive.repository.entities.FileBinary;
import org.sola.services.digitalarchive.repository.entities.FileInfo;

public class DigitalArchiveEJBIT extends AbstractEJBTest {

    private final String fileName1 = "file1.txt";
    private final String fileName2 = "file2.txt";
    private final String fileName3 = "file3.txt";
    private static String documentId;
    private File scanFolder;
    private File thumbFolder;

    public DigitalArchiveEJBIT() {
        super();
    }

    @Before
    public void setUp() {
        // Prepare some data. Load config in future
        scanFolder = new File(System.getProperty("user.home") + "/sola/scan");
        thumbFolder = new File(scanFolder.getAbsolutePath() + File.separatorChar + "thumb");

        // Init folder
        if (!scanFolder.exists()) {
            new File(scanFolder.getAbsolutePath()).mkdirs();
        }

        if (!thumbFolder.exists()) {
            new File(thumbFolder.getAbsolutePath()).mkdirs();
        }

        createFile(scanFolder.getAbsolutePath() + File.separator + fileName1);
        createFile(scanFolder.getAbsolutePath() + File.separator + fileName2);
        createFile(scanFolder.getAbsolutePath() + File.separator + fileName3);

        createFile(thumbFolder.getAbsolutePath() + File.separator + fileName1);
        createFile(thumbFolder.getAbsolutePath() + File.separator + fileName2);
        createFile(thumbFolder.getAbsolutePath() + File.separator + fileName3);
    }

    @After
    public void tearDown() {
        if(scanFolder!=null && scanFolder.exists()){
            File file = new File(scanFolder.getAbsolutePath() + File.separator + fileName1);
            if(file.exists()){ file.delete();}
            file = new File(scanFolder.getAbsolutePath() + File.separator + fileName2);
            if(file.exists()){ file.delete();}
            file = new File(scanFolder.getAbsolutePath() + File.separator + fileName3);
            if(file.exists()){ file.delete();}
        }
        
        if(thumbFolder!=null && thumbFolder.exists()){
            File file = new File(thumbFolder.getAbsolutePath() + File.separator + fileName1);
            if(file.exists()){ file.delete();}
            file = new File(thumbFolder.getAbsolutePath() + File.separator + fileName2);
            if(file.exists()){ file.delete();}
            file = new File(thumbFolder.getAbsolutePath() + File.separator + fileName3);
            if(file.exists()){ file.delete();}
        }
    }
    
    private void createFile(String filePath) {
        try {
            FileWriter outFile = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(outFile);

            // Write text to file
            out.println("This is line 1");
            out.println("This is line 2");
            out.print("This is line3 part 1, ");
            out.println("this is line 3 part 2");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test of getFileBinary method, of class DigitalArchiveEJB.
     */
    @Test
    public void testGetFileBinary() throws Exception {
        System.out.println("Trying to get file from scan folder");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());
        FileInfo result = instance.getFileBinary(fileName1);
        assertNotNull(result);

        if (result != null) {
            System.out.println("File Name = " + result.getName());
        } else {
            fail("Can't get file from shared folder");
        }
    }

    /**
     * Test of getFileThumbnail method, of class DigitalArchiveEJB.
     */
    @Test
    @Ignore
    public void testGetFileThumbnail() throws Exception {
        System.out.println("Trying to get file's thumbnail");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());
        FileInfo result = instance.getFileThumbnail(fileName1);
        assertNotNull(result);

        if (result != null) {
            //System.out.println("Thumbnail size = " + result.getContent().length);
        } else {
            fail("Can't get thumbnail from shared folder");
        }
    }

    /**
     * Test of getAllFiles method, of class DigitalArchiveEJB.
     */
    @Test
    public void testGetAllFiles() throws Exception {
        System.out.println("Getting list of files in the shared folder");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());
        List result = instance.getAllFiles();
        assertNotNull(result);

        if (result != null && result.size() <= 0) {
            fail("Shared folder list is empty.");
        } else {
            if (result == null) {
                fail("Can't get file from shared folder");
            } else {
                System.out.println("Found " + result.size() + " files.");
            }
        }
    }

    /**
     * Test of createDocument method, of class DigitalArchiveEJB.
     */
    @Test
    public void testCreateDocument_Document() throws Exception {
        System.out.println("Creating document");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());
        Document document = new Document();
        document.setBody(new byte[]{1, 2, 3, 4, 5});
        document.setDescription("test description");
        document.setExtension("txt");

        Document result = instance.createDocument(document);
        assertNotNull(result);

        if (result != null) {
            documentId = result.getId();
            System.out.println(String.format("New document has been created. ID = %s, number = %s ",
                    result.getId(), result.getNr()));
        } else {
            fail("Document creation failed");
        }
    }

    /**
     * Test of getDocument method, of class DigitalArchiveEJB.
     */
    @Test
    public void testGetDocument() throws Exception {
        System.out.println("Getting document by id = " + documentId);

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());
        Document result = instance.getDocument(documentId);

        assertNotNull(result);

        if (result != null) {
            assertEquals(documentId, result.getId());
            System.out.println(String.format("Document has been extracted from DB. ID = %s, row version = %s ",
                    result.getId(), result.getRowVersion()));
        } else {
            fail("Can't get document id = " + documentId);
        }
    }

    /**
     * Test of saveDocument method, of class DigitalArchiveEJB.
     */
    @Test
    public void testSaveDocument() throws Exception {
        System.out.println("Saving document");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());

        Document document = instance.getDocument(documentId);
        document.setDescription("updated description");

        Document result = instance.saveDocument(document);
        assertNotNull(result);

        if (result != null) {
            System.out.println(String.format("Document ID=%s was saved. Row version = %s",
                    result.getId(), result.getRowVersion()));
            assertEquals(result.getDescription(), document.getDescription());
        } else {
            fail("Can't save document id " + documentId);
        }
    }

    /**
     * Test of createDocument method, of class DigitalArchiveEJB.
     */
    @Test
    public void testCreateDocumentFromServerFile() throws Exception {
        System.out.println("Trying to create new document from server file");

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());

        Document document = new Document();
        document.setExtension("txt");
        document.setDescription("document inserted from server file");

        Document result = instance.createDocument(document, fileName2);

        assertNotNull(result);

        if (result == null) {
            fail("Failed to create document from server file " + fileName2);
        } else {
            System.out.println(String.format("New document has been created. ID = %s, number = %s ",
                    result.getId(), result.getNr()));
        }
    }

    /**
     * Test of deleteFile method, of class DigitalArchiveEJB.
     */
    @Test
    public void testDeleteFile() throws Exception {
        System.out.println("Trying to delete file " + fileName3);

        DigitalArchiveEJBLocal instance = (DigitalArchiveEJBLocal) getEJBInstance(DigitalArchiveEJB.class.getSimpleName());

        boolean result = instance.deleteFile(fileName3);
        assertTrue(result);

        if (!result) {
            fail("The test case is a prototype.");
        } else {
            System.out.println("File " + fileName3 + " was successfully deleted");
        }
    }
}
