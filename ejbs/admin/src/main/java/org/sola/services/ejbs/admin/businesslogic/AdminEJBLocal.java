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
package org.sola.services.ejbs.admin.businesslogic;

import java.util.List;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Role;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.User;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Group;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.GroupSummary;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Language;

public interface AdminEJBLocal {

    /**
     * See {@linkplain AdminEJB#getUsers()
     * AdminEJB.getUsers}
     */
    List<User> getUsers();

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getUser(java.lang.String)
     * AdminEJB.getUser}
     */
    User getUser(String userName);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getCurrentUser()
     * AdminEJB.getCurrentUser}
     */
    User getCurrentUser();

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#saveUser(org.sola.services.ejbs.admin.businesslogic.repository.entities.User)
     * AdminEJB.saveUser}
     */
    User saveUser(User user);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#changePassword(java.lang.String, java.lang.String)
     * AdminEJB.changePassword}
     */
    boolean changePassword(String userName, String password);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getRoles()
     * AdminEJB.getRoles}
     */
    List<Role> getRoles();

    /**
     * See {@linkplain AdminEJB#getUserRoles(java.lang.String)
     * AdminEJB.getUserRoles}
     */
    List<Role> getUserRoles(String userName);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getCurrentUserRoles()
     * AdminEJB.getCurrentUserRoles}
     */
    List<Role> getCurrentUserRoles();

    /**
     * See {@linkplain AdminEJB#getRole(java.lang.String)
     * AdminEJB.getRole}
     */
    Role getRole(String roleCode);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getGroups()
     * AdminEJB.getGroups}
     */
    List<Group> getGroups();

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getGroupsSummary()
     * AdminEJB.getGroupsSummary}
     */
    List<GroupSummary> getGroupsSummary();

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#getGroup(java.lang.String)
     * AdminEJB.getGroup}
     */
    Group getGroup(String groupId);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#saveGroup(org.sola.services.ejbs.admin.businesslogic.repository.entities.Group)
     * AdminEJB.saveGroup}
     */
    Group saveGroup(Group group);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#saveRole(org.sola.services.ejbs.admin.businesslogic.repository.entities.Role)
     * AdminEJB.saveRole}
     */
    Role saveRole(Role role);

    /**
     * See {@linkplain org.sola.services.ejbs.admin.businesslogic.AdminEJB#isUserAdmin()
     * AdminEJB.isUserAdmin}
     */
    boolean isUserAdmin();

    /**
     * See {@linkplain AdminEJB#getLanguages(java.lang.String)
     * AdminEJB.getLanguages}
     */
    List<Language> getLanguages(String lang);
}
