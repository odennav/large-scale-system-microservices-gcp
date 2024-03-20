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

import com.ntw.oms.product.config.TestConfig;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */
public class ProductMockDao implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(ProductMockDao.class);
    @Override
    public List<Product> getProducts() {
        List<Product> products = new LinkedList<Product>();
        products.add(getProduct(TestConfig.productTestId_1));
        return products;
    }

    @Override
    public Product getProduct(String id) {
        if (id.equals(TestConfig.productTestId_Bad)) {
            logger.debug("Unable to find product; context={}", id);
            return null;
        }
        Product product = TestConfig.createProduct(id);
        logger.debug("Found product; context={}", product);
        return product;
    }

    @Override
    public List<Product> getProducts(List<String> ids) {
        return null;
    }

    @Override
    public boolean addProduct(Product product) {
        if (product.getId().equals(TestConfig.productTestId_Bad)) {
            logger.debug("Unable to add product; context={}", product.getId());
            return false;
        }
        logger.debug("Added product; context={}", product);
        return true;
    }

    @Override
    public Product modifyProduct(Product product) {
        logger.debug("Modified product; context={}", product);
        return product;
    }

    @Override
    public boolean removeProduct(String id) {
        if (id.equals(TestConfig.productTestId_Bad)) {
            logger.debug("Unable to remove product; context={}", id);
            return false;
        }
        Product product = getProduct(id);
        logger.debug("Removed product; context={}", product);
        return true;
    }

    @Override
    public boolean removeProducts() {
        return false;
    }
}
