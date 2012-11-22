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
package org.sola.services.ejb.search.repository.entities;

import javax.persistence.Column;
import org.sola.services.common.repository.entities.AbstractReadOnlyEntity;

/**
 *
 * Entity representing the unregistered dealing details
 */
public class UnregisteredDealing extends AbstractReadOnlyEntity {

    public static final String PARAM_BA_UNIT_ID = "baUnitId";
    public static final String PARAM_QUERY_GET_UNREGISTERED_DEALINGS =
            " SELECT app.id, app.nr, appProp.ba_unit_id, "
            + " COALESCE(req.description, req.display_value)  AS pending_services "
            + " FROM application.application app, "
            + "      application.application_property appProp,"
            + "      application.service ser,"
            + "      application.request_type req "
            + " WHERE appProp.ba_unit_id = #{" + PARAM_BA_UNIT_ID + "} "
            + " AND  app.id = appProp.application_id "
            + " AND  app.status_code IN ('lodged', 'requisitioned') "
            + " AND  ser.application_id = app.id "
            + " AND  ser.request_type_code = req.code "
            + " ORDER BY app.lodging_datetime ";
    @Column(name = "ba_unit_id")
    private String baUnitId;
    @Column(name = "nr")
    private String appNr;
    @Column(name = "id")
    private String appId;
    @Column(name = "pending_services")
    private String pendingServices;

    public UnregisteredDealing() {
        super();
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppNr() {
        return appNr;
    }

    public void setAppNr(String appNr) {
        this.appNr = appNr;
    }

    public String getBaUnitId() {
        return baUnitId;
    }

    public void setBaUnitId(String baUnitId) {
        this.baUnitId = baUnitId;
    }

    public String getPendingServices() {
        return pendingServices;
    }

    public void setPendingServices(String pendingServices) {
        this.pendingServices = pendingServices;
    }
}
