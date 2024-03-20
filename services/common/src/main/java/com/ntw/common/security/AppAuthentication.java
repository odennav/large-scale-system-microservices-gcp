//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.common.security;

import com.ntw.common.entity.Role;
import com.ntw.common.entity.UserAuth;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.security.Principal;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 06/08/19.
 */
public class AppAuthentication implements Authentication {

    Principal principal;
    String credentials;
    List<Role> roles;
    boolean secure;
    String details;
    boolean isAuthenticated;
    UserAuth userAuth;

    public AppAuthentication(UserAuth userAuth, boolean secure, String authHeader) {
        this.principal = new AppPrincipal(userAuth.getId());
        this.credentials = userAuth.getPassword();
        this.roles = new LinkedList<>();
        for (String roleStr : userAuth.getRoles()) {
            this.roles.add(Role.getRole(roleStr));
        }
        this.secure = secure;
        this.details = authHeader;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        this.isAuthenticated = b;
    }

    @Override
    public String getName() {
        return principal.getName();
    }

    public boolean isSecure() {
        return secure;
    }

}
