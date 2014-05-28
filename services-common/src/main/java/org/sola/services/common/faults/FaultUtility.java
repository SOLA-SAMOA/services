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
package org.sola.services.common.faults;

import org.sola.common.SOLAException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import javax.ejb.EJBAccessException;
import javax.persistence.OptimisticLockException;
import org.sola.common.DateUtility;
import org.sola.common.messaging.ServiceMessage;
import org.sola.services.common.LocalInfo;
import org.sola.services.common.logging.LogUtility;

/**
 *
 * @author soladev
 */
public final class FaultUtility {

    /**
     * Processes SOLA web service exceptions by logging them to the SOLA services log and
     * reformats the exceptions as SOLA SOAP Faults. 
     * @param t The exception that was caught
     * @return A SOLA SOAP fault. One of SOLAFault, UnhandledFault or OptimisticLockingFault
     */
    public static Exception ProcessException(Exception t) {

        Exception fault;
        FaultInfoBean faultInfoBean = new FaultInfoBean();
        faultInfoBean.setFaultId(createFaultId(LocalInfo.getUserName()));

        String stackTraceAsStr = getStackTraceAsString(t);

        try {
            String msg = "SOLA FaultId = " + faultInfoBean.getFaultId()
                    + System.getProperty("line.separator")
                    + stackTraceAsStr;
            LogUtility.log(msg, Level.SEVERE);
        } catch (Exception logEx) {
            // Failed to log the exception details. The log may be full or some
            // other error may have occurred. Get the user to contact the sys
            // admin to fix this. Include the Log Exception message in the Fault 
            // so the administrator has some idea what the cause of the exception is. 
            faultInfoBean.setMessageCode(ServiceMessage.EXCEPTION_FAILED_LOGGING);
            faultInfoBean.addMessageParameter(logEx.getLocalizedMessage());
            return new UnhandledFault(faultInfoBean.getMessageCode(), faultInfoBean);
        }

        try {
            // Identify the type of exception and raise the appropriate Service Fault
            if (hasCause(t, SOLAValidationException.class)) {

                SOLAValidationException ex = getCause(t, SOLAValidationException.class);
                faultInfoBean.setValidationResultList(ex.getValidationResultList());
                faultInfoBean.setMessageCode(ex.getMessage());
                fault = new SOLAValidationFault(ex.getMessage(), faultInfoBean);

            } else if (hasCause(t, EJBAccessException.class)) {

                faultInfoBean.setMessageCode(ServiceMessage.EXCEPTION_INSUFFICIENT_RIGHTS);
                fault = new SOLAAccessFault(ServiceMessage.EXCEPTION_INSUFFICIENT_RIGHTS, faultInfoBean);

            } else if (hasCause(t, SOLAException.class)) {

                SOLAException ex = getCause(t, SOLAException.class);
                faultInfoBean.setMessageCode(ex.getMessage());
                Object[] msgParms = ex.getMessageParameters();
                if (msgParms != null) {
                    for (Object param : msgParms) {
                        if (param != null) {
                            faultInfoBean.addMessageParameter(param.toString());
                        } else {
                            // Add null as there may be other paramters in the list and the 
                            // position of parameters is important. 
                            faultInfoBean.addMessageParameter(null);
                        }
                    }
                }
                fault = new SOLAFault(ex.getMessage(), faultInfoBean);

            } else if (hasCause(t, SOLAFault.class)) {
                // We need to create another SOLAFault to minimise any
                // exception detail leakage (i.e. apply Exception Sheilding Pattern)
                SOLAFault f = getCause(t, SOLAFault.class);
                FaultInfoBean tempBean = f.getFaultInfo();
                if (tempBean != null) {
                    faultInfoBean.setMessageCode(tempBean.getMessageCode());
                    faultInfoBean.setMessageParameters(tempBean.getMessageParameters());
                }
                fault = new SOLAFault(f.getMessage(), faultInfoBean);

            } else if (isOptimisticLocking(t, stackTraceAsStr)) {
                // Optimistic locking exception
                faultInfoBean.setMessageCode(ServiceMessage.GENERAL_OPTIMISTIC_LOCK);
                fault = new OptimisticLockingFault(faultInfoBean.getMessageCode(),
                        faultInfoBean);

            } //            else if (isDatabaseConstraintViolation(t, stackTraceAsStr)){
            //                Throwable dbException = t;
            //                while(dbException!= null && dbException.getClass() 
            //                        != org.apache.ibatis.exceptions.PersistenceException.class){
            //                    dbException = dbException.getCause();
            //                }
            //                String constraintName = "NOT SET";
            //                if (dbException != null){
            //                    if (dbException.getCause() != null)
            //                    constraintName = dbException.getCause().getMessage();
            //                }
            //                faultInfoBean.setMessageCode(constraintName);
            //                fault = new ConstraintViolationFault(constraintName, faultInfoBean);
            //            } 
            else {
                // Unhandled Exception. Do not provide the details of the exception as this would
                // violate the Exception Sheilding Pattern. The administrator can refer to the
                // log file to obtain the details of the exception. 
                faultInfoBean.setMessageCode(ServiceMessage.GENERAL_UNEXPECTED);
                fault = new UnhandledFault(faultInfoBean.getMessageCode(), faultInfoBean);
            }

        } catch (Exception formatEx) {
            // Catch all in case the format throws an exception. Note that the
            // exception details in the log will not match the details of the
            // format exception (i.e. the one in the log will be the original
            // exception).      
            faultInfoBean.setMessageCode(ServiceMessage.EXCEPTION_FAILED_FORMATTING);
            faultInfoBean.addMessageParameter(formatEx.getLocalizedMessage());
            return new UnhandledFault(faultInfoBean.getMessageCode(), faultInfoBean);
        }

        return fault;
    }

    private static boolean isOptimisticLocking(Throwable t, String traceInfo) {
        return traceInfo.contains("row_has_different_change_time")
                || hasCause(t, OptimisticLockException.class);
    }

    /**
     * Not in use.
     * @param t
     * @param traceInfo
     * @return 
     */
    private static boolean isDatabaseConstraintViolation(Throwable t, String traceInfo) {
        return traceInfo.contains("org.postgresql.util.PSQLException");
    }

    /**
     * Creates an identifier for the fault based on the userName and
     * the current datetime. If the userName is null, ERR is used instead.
     * @param userName
     * @return FaultId
     */
    public static String createFaultId(String userName) {
        String faultId = "ERR";
        if (userName != null && !userName.isEmpty()) {
            faultId = userName.toUpperCase();
        }
        faultId += "-" + DateUtility.simpleFormat("yyMMddHHmmss");
        return faultId;
    }

    /**
     * Formats the stacktrace for an exception into a string
     * @param t The throwable exception
     * @return The stacktrace of the exception formatted as a string
     */
    public static String getStackTraceAsString(Throwable t) {
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Loops through the exception hierarchy to determine if it has
     * the specified cause
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for. 
     * @return true if the cause type exists in the exception hierarchy. 
     */
    public static boolean hasCause(Throwable t, Class causeType) {
        return !(getCause(t, causeType) == null);
    }

    /**
     * Loops through the exception hierarchy to get the cause of the specified type
     * @param t The throwable exception that has been caught
     * @param causeType The type of cause to check for. 
     * @return the exception of the specified type or null. 
     */
    public static <T> T getCause(Throwable t, Class<T> causeType) {
        T result = null;
        if (t.getClass() == causeType) {
            result = (T) t;
        } else {
            if (t.getCause() != null) {
                result = getCause(t.getCause(), causeType);
            }
        }
        return result;
    }
}
