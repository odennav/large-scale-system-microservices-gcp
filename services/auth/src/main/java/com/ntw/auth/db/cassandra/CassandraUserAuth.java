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

package com.ntw.auth.db.cassandra;

import com.ntw.common.entity.UserAuth;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Arrays;
import java.util.List;

/**
 * Created by anurag on 17/03/17.
 */

@Table("UserAuth")
public class CassandraUserAuth {
    @PrimaryKey
    private String id;
    private String password;
    private String name;
    private List<String> roles;
    private String emailId;

    public CassandraUserAuth() {}

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

    public UserAuth getUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId(this.getId());
        userAuth.setName(this.getName());
        userAuth.setPassword(this.getPassword());
        userAuth.setRoles(this.getRoles());
        userAuth.setEmailId(this.getEmailId());
        return userAuth;
    }

    public CassandraUserAuth(UserAuth userAuth) {
        this.setId(userAuth.getId());
        this.setName(userAuth.getName());
        this.setPassword(userAuth.getPassword());
        this.setRoles(userAuth.getRoles());
        this.setEmailId(userAuth.getEmailId());
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

        CassandraUserAuth DBUserAuth = (CassandraUserAuth) o;

        if (!id.equals(DBUserAuth.id)) return false;
        if (!name.equals(DBUserAuth.name)) return false;
        return emailId != null ? emailId.equals(DBUserAuth.emailId) : DBUserAuth.emailId == null;

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (emailId != null ? emailId.hashCode() : 0);
        return result;
    }
}
