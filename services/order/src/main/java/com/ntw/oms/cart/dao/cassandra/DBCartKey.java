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

package com.ntw.oms.cart.dao.cassandra;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

/**
 * Created by anurag on 19/04/17.
 */
@PrimaryKeyClass
public class DBCartKey {
    @PrimaryKeyColumn(name = "cartId", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String cartId;
    @PrimaryKeyColumn(name = "cartLineId", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.ASCENDING)
    private int cartLineId;

    public int getCartLineId() {
        return cartLineId;
    }

    public void setCartLineId(int cartLineId) {
        this.cartLineId = cartLineId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public String toString() {
        return "{" +
                "\"cartId\":" + (cartId == null ? "null" : "\"" + cartId + "\"") + ", " +
                "\"id\":\"" + cartLineId + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DBCartKey cartKey = (DBCartKey) o;

        if (cartLineId != cartKey.cartLineId) return false;
        return cartId.equals(cartKey.cartId);

    }

    @Override
    public int hashCode() {
        int result = cartId.hashCode();
        result = 31 * result + cartLineId;
        return result;
    }
}
