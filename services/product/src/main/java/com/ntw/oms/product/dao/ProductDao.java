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

package com.ntw.oms.product.dao;

import com.ntw.oms.product.entity.Product;

import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */
public interface ProductDao {

    public List<Product> getProducts();

    public List<Product> getProducts(List<String> ids);

    public Product getProduct(String id);

    public boolean addProduct(Product product);

    Product modifyProduct(Product product);

    public boolean removeProduct(String id);

    public boolean removeProducts();
}
