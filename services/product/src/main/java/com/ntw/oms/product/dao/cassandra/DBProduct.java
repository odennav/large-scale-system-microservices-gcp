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

package com.ntw.oms.product.dao.cassandra;

import com.ntw.oms.product.entity.Product;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Created by anurag on 24/03/17.
 */

@Table("Product")
public class DBProduct {
    @PrimaryKey
    private String id;
    private String name;
    private Float price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + (id == null ? "null" : "\"" + id + "\"") + ", " +
                "\"name\":" + (name == null ? "null" : "\"" + name + "\"") + ", " +
                "\"price\":" + (price == null ? "null" : "\"" + price + "\"") + ", " +
                "}";
    }

    public static DBProduct createProduct(Product product) {
        DBProduct dbProduct = new DBProduct();
        dbProduct.setId(product.getId());
        dbProduct.setName(product.getName());
        dbProduct.setPrice(product.getPrice());
        return dbProduct;
    }

    public Product getProduct() {
        Product product = new Product();
        product.setId(getId());
        product.setName(getName());
        product.setPrice(getPrice());
        return product;
    }
}
