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
package org.sola.services.ejb.scheduler.businesslogic;

import java.io.File;
import java.util.Calendar;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import org.sola.common.logging.LogUtility;

/**
 * 
 * Scheduler class to cleanup shared folder with scanned images. Cleaning event 
 * occurs on regular base. Configuration of cleaning period and files lifetime is
 * taken from configuration service.
 */
@Singleton
@Startup
public class SharedFolderCleaner implements SharedFolderCleanerLocal {

    @Resource
    TimerService timerService;
    private File scanFolder;
    private File thumbFolder;
    // The time for the file to live in minutes
    private int fileLifetime = 0;
    // Cleanup period in minutes
    private int period = 30000;

    /**
     * Initialization method to setup timer, shared folder path with scanned 
     * images and lifetime of files.
     */
    @PostConstruct
    @Override
    public void init() {
        // TODO: Get initialization values from configuration service
        scanFolder = new File(System.getProperty("user.home") + "/sola/scan");
        thumbFolder = new File(scanFolder.getAbsolutePath() + File.separatorChar + "thumb");
        fileLifetime = 43200;
        period = 30;

        long periodMs = (long) period * 60 * 1000;
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerService.createIntervalTimer(periodMs, periodMs, timerConfig);
    }

    /**
     * This method is triggered automatically upon timer timeout event.
     * @param timer Timer instance passed to the method automatically by {@link TimerService}
     */
    @Timeout
    public void cleanUpTimeout(Timer timer) {
        cleanUp();
    }

    /**
     * Cleans shared folder with scanned images, based on configuration parameters.
     */
    public void cleanUp() {
        try {
            LogUtility.log("Start cleaning shared folder at "
                    + Calendar.getInstance().getTime(), Level.INFO);

            int filesDeleted = 0;
            long currentDate = Calendar.getInstance().getTimeInMillis();
            long fileLifetimeMs = (long) fileLifetime * 60 * 1000;

            if (scanFolder != null && scanFolder.exists()) {
                for (File file : scanFolder.listFiles()) {
                    if (file.lastModified() + fileLifetimeMs <= currentDate) {
                        try {
                            if (file.isFile()) {

                                String fileName = file.getName();

                                file.delete();

                                // Try to delete thumbnail
                                if (scanFolder != null && scanFolder.exists()) {
                                    File thumbNail = new File(thumbFolder.getPath()
                                            + File.separator + fileName);

                                    if (thumbNail.exists()) {
                                        thumbNail.delete();
                                    }
                                }

                                filesDeleted += 1;
                            }
                        } catch (Throwable t) {
                            LogUtility.log(t.getLocalizedMessage(), Level.SEVERE);
                        }
                    }
                }
            }

            LogUtility.log(String.format("Finished folder cleaning at %s. Deleted %s file(s)",
                    Calendar.getInstance().getTime(), filesDeleted), Level.INFO);

        } catch (Throwable t) {
            LogUtility.log(t.getLocalizedMessage(), Level.SEVERE);
        }
    }
}