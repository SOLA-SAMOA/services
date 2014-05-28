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
package org.sola.services.ejbs.admin.businesslogic.repository.entities;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import org.sola.services.common.repository.AccessFunctions;
import org.sola.services.common.repository.ChildEntityList;
import org.sola.services.common.repository.DefaultSorter;
import org.sola.services.common.repository.entities.AbstractVersionedEntity;

@Table(name = "appuser", schema = "system")
@DefaultSorter(sortString = "username, last_name")
public class User extends AbstractVersionedEntity {

    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "passwd";
    public static final String PARAM_CHANGE_USER = "changeUser";
    public static final String QUERY_WHERE_USERNAME = "username = #{" + PARAM_USERNAME + "}";
    public static final String QUERY_SET_PASSWORD = "select system.setPassword(#{"
            + PARAM_USERNAME + "}, #{" + PARAM_PASSWORD + "}, #{" + PARAM_CHANGE_USER + "})";
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "username")
    private String userName;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "active")
    private boolean active;
    @Column(name = "description")
    private String description;
    @Column(name = "passwd", insertable = false, updatable = false)
    private String password;
    @AccessFunctions(onSelect = "(SELECT pword_change_user"
            + " FROM system.user_pword_expiry"
            + " WHERE uname = username)")
    @Column(name = "pword_change_user", insertable = false, updatable = false)
    private String lastPwordChangeUser;
    @AccessFunctions(onSelect = "(SELECT pword_expiry_days"
            + " FROM system.user_pword_expiry"
            + " WHERE uname = username)")
    @Column(name = "pword_expiry_days", insertable = false, updatable = false)
    private Integer pwordExpiryDays;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    @ChildEntityList(parentIdField = "userId")
    private List<UserGroup> userGroups;

    public User() {
        super();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getId() {
        if (id == null) {
            id = generateId();
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<UserGroup> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<UserGroup> userGroups) {
        this.userGroups = userGroups;
    }

    public String getLastPwordChangeUser() {
        return lastPwordChangeUser;
    }

    public void setLastPwordChangeUser(String lastPwordChangeUser) {
        this.lastPwordChangeUser = lastPwordChangeUser;
    }

    public Integer getPwordExpiryDays() {
        return pwordExpiryDays;
    }

    public void setPwordExpiryDays(Integer pwordExpiryDays) {
        this.pwordExpiryDays = pwordExpiryDays;
    }
}
