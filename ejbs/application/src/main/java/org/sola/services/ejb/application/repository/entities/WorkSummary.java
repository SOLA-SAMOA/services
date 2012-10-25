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
package org.sola.services.ejb.application.repository.entities;

import javax.persistence.Column;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 * Samoa Customization to improve content of the lodgement report. Entity representing the
 * information obtained from the application.get_work_summary function.
 */
public class WorkSummary extends AbstractReadOnlyEntity {

    public static final String PARAMETER_FROM = "fromDate";
    public static final String PARAMETER_TO = "toDate";
    public static final String QUERY_FROM_WORK_SUMMARY =
            "application.get_work_summary(#{" + PARAMETER_FROM + "}, #{" + PARAMETER_TO + "}) ";
    
    @Column(name = "req_type")
    private String serviceType;
    @Column(name = "req_cat")
    private String serviceCategory;
    @Column
    private int lodged;
    @Column
    private int requisitioned;
    @Column
    private int registered;
    @Column
    private int cancelled;
    @Column
    private int withdrawn;
    @Column(name = "in_progress")
    private int inProgress;
    @Column(name = "on_requisition")
    private int onRequisition;
    @Column
    private int overdue;
    @Column(name = "overdue_apps")
    private String overdueApplications;
    @Column(name = "requisition_apps")
    private String onRequisitionApplications;

    public WorkSummary() {
        super();
    }

    public int getCancelled() {
        return cancelled;
    }

    public void setCancelled(int cancelled) {
        this.cancelled = cancelled;
    }

    public int getInProgress() {
        return inProgress;
    }

    public void setInProgress(int inProgress) {
        this.inProgress = inProgress;
    }

    public int getLodged() {
        return lodged;
    }

    public void setLodged(int lodged) {
        this.lodged = lodged;
    }

    public int getOnRequisition() {
        return onRequisition;
    }

    public void setOnRequisition(int onRequisition) {
        this.onRequisition = onRequisition;
    }

    public int getOverdue() {
        return overdue;
    }

    public void setOverdue(int overdue) {
        this.overdue = overdue;
    }

    public String getOverdueApplications() {
        return overdueApplications;
    }

    public void setOverdueApplications(String overdueApplications) {
        this.overdueApplications = overdueApplications;
    }

    public int getRegistered() {
        return registered;
    }

    public void setRegistered(int registered) {
        this.registered = registered;
    }

    public int getRequisitioned() {
        return requisitioned;
    }

    public void setRequisitioned(int requisitioned) {
        this.requisitioned = requisitioned;
    }

    public String getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(String serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getWithdrawn() {
        return withdrawn;
    }

    public void setWithdrawn(int withdrawn) {
        this.withdrawn = withdrawn;
    }

    public String getOnRequisitionApplications() {
        return onRequisitionApplications;
    }

    public void setOnRequisitionApplications(String onRequisitionApplications) {
        this.onRequisitionApplications = onRequisitionApplications;
    }
}
