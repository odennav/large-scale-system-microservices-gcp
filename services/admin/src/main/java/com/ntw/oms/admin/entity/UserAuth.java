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

package com.ntw.oms.admin.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 17/03/17.
 */

public class UserAuth {
    private String id;
    private String password;
    private String name;
    private List<String> roles;
    private String emailId;

    public UserAuth() {
        roles = new LinkedList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + (id == null ? "null" : "\"" + id + "\"") + ", " +
                "\"password\":" + (password == null ? "null" : "\"" + password + "\"") + ", " +
                "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"roles\":" + (roles == null ? "null" : Arrays.toString(roles.toArray())) + ", " +
                "\"emailId\":" + (emailId == null ? "null" : "\"" + emailId + "\"") +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserAuth userAuth = (UserAuth) o;

        if (!id.equals(userAuth.id)) return false;
        if (!name.equals(userAuth.name)) return false;
        return emailId != null ? emailId.equals(userAuth.emailId) : userAuth.emailId == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        return result;
    }
}
