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
package org.sola.services.common.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Uses Java utility logging to configure a common logger for the SOLA Services called 
 * org.sola.services. This logger will write messages to the Glassfish server log which can be 
 * viewed from the Log Viewer in the  Glassfish Admin Console. 
 * By default, the logger will capture all INFO, WARNING and SEVERE messages. To capture 
 * messages for the levels below INFO, or to further limit the log level, you must add the 
 * appropriate Log Level for the org.sola.services logger in the Module Log Levels tab of the 
 * server-config > Logger Settings configuration node. 
 * For details configuring the log level for the MyBatis database connection provider, refer to 
 * {@linkplain org.sola.services.common.repository.DatabaseConnectionManager}
 * 
 * @author soladev
 */
public final class LogUtility {

    private final static Logger logger = Logger.getLogger("org.sola.services");

    /**
     * Logs a message to the Glassfish Server log with the default log level of INFO.
     * @param msg The message to log.
     */
    public static void log(String msg) {
        log(msg, Level.INFO);
    }

    /**
     * Logs a message to the Glassfish Server log with the specified log level. Note that
     * unless the log level settings in Glassfish have been configured to allow logging of
     * messages below the INFO level, those messages will not get logged. 
     * @param msg The message to log.
     * @param level The level to log the message at. 
     */
    public static void log(String msg, Level level) {
        logger.log(level, msg);
    }
}
