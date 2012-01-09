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
package org.sola.services.ejbs.admin.businesslogic;

import java.util.List;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Role;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.User;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Group;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.GroupSummary;
import org.sola.services.ejbs.admin.businesslogic.repository.entities.Language;

public interface AdminEJBLocal {
    /** Returns list of all users. */
    List<User> getUsers();
    
    /** Returns user. */
    User getUser(String userName);
    
    /** Returns currently logged user. */
    User getCurrentUser();
 
    /** Saves user. */
    User saveUser(User user);
    
    /** Sets password for user. */
    boolean changePassword(String userName, String password);
    
    /** Returns all application roles. */
    List<Role> getRoles();
    
    /** Returns user roles, assigned through the group. */
    List<Role> getUserRoles(String userName);
    
    /** Returns current user roles, assigned through the group. */
    List<Role> getCurrentUserRoles();
    
    /** Returns application role. */
    Role getRole(String roleCode);
    
    /** Returns all groups. */
    List<Group> getGroups();
    
    /** Returns all groups with summary information. */
    List<GroupSummary> getGroupsSummary();
    
    /** Returns group by ID. */
    Group getGroup(String groupId);
    
    /** Creates/saves group. */
    Group saveGroup(Group group);
    
    /** Updates role. */
    Role saveRole(Role role);
    
    /** Returns true if current user belongs to any of admin roles. */
    boolean isUserAdmin();
    
    /** Returns list of available languages. */
    List<Language> getLanguages(String lang);
}
