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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.ejb.application.businesslogic;

import java.util.Date;
import java.util.List;
import javax.ejb.Local;
import org.sola.services.common.br.ValidationResult;
import org.sola.services.common.ejbs.AbstractEJBLocal;
import org.sola.services.ejb.application.repository.entities.Application;
import org.sola.services.ejb.application.repository.entities.ApplicationActionType;
import org.sola.services.ejb.application.repository.entities.ApplicationLog;
import org.sola.services.ejb.application.repository.entities.ApplicationStatusType;
import org.sola.services.ejb.application.repository.entities.RequestCategoryType;
import org.sola.services.ejb.application.repository.entities.RequestType;
import org.sola.services.ejb.application.repository.entities.RrrTypeAction;
import org.sola.services.ejb.application.repository.entities.ServiceActionType;
import org.sola.services.ejb.application.repository.entities.ServiceStatusType;


/**
 *
 * @author soladev, manoku
 */
@Local
public interface ApplicationEJBLocal extends AbstractEJBLocal {

    Application getApplication(String id);

    Application createApplication(Application application);

    Application saveApplication(Application application);

    List<RequestType> getRequestTypes(String languageCode);
    
    List<RequestCategoryType> getRequestCategoryTypes(String languageCode);

//    int changeApplicationAssignment(String applicationId,
//            String userId,
//            int rowVersion);

    List<ApplicationLog> getApplicationLog(String id);

    List<ApplicationLog> getUserActions(String username, Date fromTime, Date toTime);

    Application calculateFeesAndDates(Application application);

    List<ApplicationStatusType> getApplicationStatusTypes(String languageCode);

    List<ApplicationActionType> getApplicationActionTypes(String languageCode);

    List<ServiceStatusType> getServiceStatusTypes(String languageCode);

    List<ServiceActionType> getServiceActionTypes(String languageCode);

    List<RrrTypeAction> getRrrTypeActions(String languageCode);

//    List<ValidationResult> validate(String applicationId, String languageCode, String momentCode);
//
//    List<ValidationResult> approveApplication(String applicationId, String languageCode);
//
//    List<ValidationResult> rejectApplication(String applicationId, String languageCode);

    List<ValidationResult> serviceActionComplete(
            String serviceId, String languageCode, int rowVersion);

    List<ValidationResult> serviceActionRevert(
            String serviceId, String languageCode, int rowVersion);
    
    List<ValidationResult> serviceActionStart(
            String serviceId, String languageCode, int rowVersion);

    List<ValidationResult> serviceActionCancel(
            String serviceId, String languageCode, int rowVersion);
    
    List<ValidationResult> applicationActionWithdraw(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionCancel(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionRequisition(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionValidate(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionApprove(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionArchive(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionDespatch(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionLapse(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionUnassign(
            String applicationId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionAssign(
            String applicationId, String userId, String languageCode, int rowVersion);

    List<ValidationResult> applicationActionResubmit(
            String applicationId, String languageCode, int rowVersion);
}
