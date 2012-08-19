/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO). All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,this list of conditions
 * and the following disclaimer. 2. Redistributions in binary form must reproduce the above
 * copyright notice,this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution. 3. Neither the name of FAO nor the names of its
 * contributors may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
import org.sola.services.ejb.application.repository.entities.LodgementTiming;
import org.sola.services.ejb.application.repository.entities.LodgementView;
import org.sola.services.ejb.application.repository.entities.LodgementViewParams;
import org.sola.services.ejb.application.repository.entities.RequestCategoryType;
import org.sola.services.ejb.application.repository.entities.RequestType;
import org.sola.services.ejb.application.repository.entities.TypeAction;
import org.sola.services.ejb.application.repository.entities.Service;
import org.sola.services.ejb.application.repository.entities.ServiceActionType;
import org.sola.services.ejb.application.repository.entities.ServiceStatusType;

/**
 * Local interface for the {@linkplain ApplicationEJB}.
 */
@Local
public interface ApplicationEJBLocal extends AbstractEJBLocal {

    /**
     * See {@linkplain ApplicationEJB#getApplication(java.lang.String)
     * ApplicationEJB.getApplication}.
     */
    Application getApplication(String id);

    /**
     * See {@linkplain ApplicationEJB#createApplication(org.sola.services.ejb.application.repository.entities.Application)
     * ApplicationEJB.createApplication}.
     */
    Application createApplication(Application application);

    /**
     * See {@linkplain ApplicationEJB#saveApplication(org.sola.services.ejb.application.repository.entities.Application)
     * ApplicationEJB.saveApplication}.
     */
    Application saveApplication(Application application);

    /**
     * See {@linkplain ApplicationEJB#getLodgementView(org.sola.services.ejb.application.repository.entities.LodgementViewParams)
     * ApplicationEJB.getLodgementView}.
     */
    List<LodgementView> getLodgementView(LodgementViewParams params);

    /**
     * See {@linkplain ApplicationEJB#getLodgementTiming(org.sola.services.ejb.application.repository.entities.LodgementViewParams)
     * ApplicationEJB.getLodgementTiming}.
     */
    List<LodgementTiming> getLodgementTiming(LodgementViewParams params);

    /**
     * See {@linkplain ApplicationEJB#getRequestTypes(java.lang.String)
     * ApplicationEJB.getRequestTypes}.
     */
    List<RequestType> getRequestTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getRequestCategoryTypes(java.lang.String)
     * ApplicationEJB.getRequestCategoryTypes}.
     */
    List<RequestCategoryType> getRequestCategoryTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getApplicationLog(java.lang.String)
     * ApplicationEJB.getApplicationLog}.
     */
    List<ApplicationLog> getApplicationLog(String id);

    /**
     * See {@linkplain ApplicationEJB#getUserActions(java.lang.String, java.util.Date, java.util.Date)
     * ApplicationEJB.getUserActions}.
     */
    List<ApplicationLog> getUserActions(String username, Date fromTime, Date toTime);

    /**
     * See {@linkplain ApplicationEJB#calculateFeesAndDates(org.sola.services.ejb.application.repository.entities.Application)
     * ApplicationEJB.calculateFeesAndDates}.
     */
    Application calculateFeesAndDates(Application application);

    /**
     * See {@linkplain ApplicationEJB#getApplicationStatusTypes(java.lang.String)
     * ApplicationEJB.getApplicationStatusTypes}.
     */
    List<ApplicationStatusType> getApplicationStatusTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getApplicationActionTypes(java.lang.String)
     * ApplicationEJB.getApplicationActionTypes}.
     */
    List<ApplicationActionType> getApplicationActionTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getServiceStatusTypes(java.lang.String)
     * ApplicationEJB.getServiceStatusTypes}.
     */
    List<ServiceStatusType> getServiceStatusTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getServiceActionTypes(java.lang.String)
     * ApplicationEJB.getServiceActionTypes}.
     */
    List<ServiceActionType> getServiceActionTypes(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#getTypeActions(java.lang.String)
     * ApplicationEJB.getTypeActions}.
     */
    List<TypeAction> getTypeActions(String languageCode);

    /**
     * See {@linkplain ApplicationEJB#serviceActionComplete(java.lang.String, java.lang.String, int)
     * ApplicationEJB.serviceActionComplete}.
     */
    List<ValidationResult> serviceActionComplete(
            String serviceId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#serviceActionRevert(java.lang.String, java.lang.String, int)
     * ApplicationEJB.serviceActionRevert}.
     */
    List<ValidationResult> serviceActionRevert(
            String serviceId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#serviceActionStart(java.lang.String, java.lang.String, int)
     * ApplicationEJB.serviceActionStart}.
     */
    List<ValidationResult> serviceActionStart(
            String serviceId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#serviceActionCancel(java.lang.String, java.lang.String, int)
     * ApplicationEJB.serviceActionCancel}.
     */
    List<ValidationResult> serviceActionCancel(
            String serviceId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionWithdraw(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionWithdraw}.
     */
    List<ValidationResult> applicationActionWithdraw(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionCancel(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionCancel}.
     */
    List<ValidationResult> applicationActionCancel(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionRequisition(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionRequisition}.
     */
    List<ValidationResult> applicationActionRequisition(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionValidate(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionValidate}.
     */
    List<ValidationResult> applicationActionValidate(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionApprove(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionApprove}.
     */
    List<ValidationResult> applicationActionApprove(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionArchive(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionArchive}.
     */
    List<ValidationResult> applicationActionArchive(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionDespatch(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionDespatch}.
     */
    List<ValidationResult> applicationActionDespatch(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionLapse(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionLapse}.
     */
    List<ValidationResult> applicationActionLapse(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionUnassign(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionUnassign}.
     */
    List<ValidationResult> applicationActionUnassign(
            String applicationId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionAssign(java.lang.String, java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionAssign}.
     */
    List<ValidationResult> applicationActionAssign(
            String applicationId, String userId, String languageCode, int rowVersion);

    /**
     * See {@linkplain ApplicationEJB#applicationActionResubmit(java.lang.String, java.lang.String, int)
     * ApplicationEJB.applicationActionResubmit}.
     */
    List<ValidationResult> applicationActionResubmit(
            String applicationId, String languageCode, int rowVersion);
    /**
     * See {@linkplain ApplicationEJB#saveInformationService(org.sola.services.ejb.application.repository.entities.Service, java.lang.String) 
     * ApplicationEJB.saveInformationService}.
     */
    Service saveInformationService(Service service, String languageCode);
    
    /**
     * Returns {@link Application} by the given transaction ID.
     */
    Application getApplicationByTransactionId(String transactionId);
}
