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

package com.ntw.oms.user.dao.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

/**
 * Created by anurag on 30/05/17.
 */
@PrimaryKeyClass
public class DBAddressKey {

    @PrimaryKeyColumn(name = "userProfileId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String userProfileId;
    @PrimaryKeyColumn(name = "type", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private String type;
    @PrimaryKeyColumn(name = "id", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private int id;

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "\"userProfileId\":" + (userProfileId == null ? "null" : "\"" + userProfileId + "\"") + ", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"id\":\"" + id + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBAddressKey that = (DBAddressKey) o;

        if (id != that.id) return false;
        if (!userProfileId.equals(that.userProfileId)) return false;
        return type.equals(that.type);

    }

    @Override
    public int hashCode() {
        int result = userProfileId.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + id;
        return result;
    }
}
