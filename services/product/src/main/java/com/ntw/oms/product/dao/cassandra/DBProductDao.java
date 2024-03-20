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

import com.ntw.oms.product.dao.ProductDao;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */
@Component("CQL")
public class DBProductDao implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(DBProductDao.class);

    @Autowired(required = false)
    private CassandraOperations cassandraOperations;

    @Autowired(required = false)
    private CqlTemplate cqlTemplate;

    public CassandraOperations getCassandraOperations() {
        return cassandraOperations;
    }

    public void setCassandraOperations(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    public CqlTemplate getCqlTemplate() {
        return cqlTemplate;
    }

    public void setCqlTemplate(CqlTemplate cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    @Override
    public List<Product> getProducts() {
        String allProductCql = "select * from Product";
        List<Product> products;
        try {
            products = getCassandraOperations().select(allProductCql, Product.class);
        } catch (Exception e) {
            logger.error("Exception when fetching products", e);
            return null;
        }
        if (products.isEmpty()) {
            logger.error("Unable to get Products; context={}", allProductCql);
            return new LinkedList<>();
        }
        logger.debug("Fetched all products; context={}", products);
        return products;
    }

    @Override
    public List<Product> getProducts(List<String> ids) {
        List<Product> products = new LinkedList<>();
        ids.forEach(id -> products.add(getProduct(id)));
        return products;
    }

    @Override
    public Product getProduct(String id) {
        String productCql = "select * from product where id='"+id+"'";
        Product product;
        try {
            product = getCassandraOperations().selectOne(productCql, Product.class);
        } catch (Exception e) {
            logger.error("Exception while fetching product with id {}", id);
            logger.error("Exception message: ", e);
            return null;
        }
        if (product == null) {
            logger.debug("No product found; productId={}", id);
            return null;
        }
        logger.debug("Fetched product; context={}", product);
        return product;
    }

    @Override
    public boolean addProduct(Product product) {
        Product retProduct;
        try {
            retProduct = getCassandraOperations().insert(product);
        } catch (Exception e) {
            logger.error("Exception while adding a product; context={}", product);
            logger.error("Exception message: ", e);
            return false;
        }
        if (retProduct == null) {
            logger.error("Unable to add product; productId={}", product);
            return false;
        }
        logger.debug("Added product; context={}", product);
        return true;
    }

    @Override
    public Product modifyProduct(Product product) {
        Product retProduct;
        try {
            retProduct = getCassandraOperations().insert(product);
        } catch (Exception e) {
            logger.error("Exception while modifying a product; context={}", product);
            logger.error("Exception message: ", e);
            return null;
        }
        if (retProduct == null) {
            logger.error("Unable to modify product; productId={}", product);
            return null;
        }
        logger.debug("Modified product; context={}", product);
        return retProduct;
    }

    @Override
    public boolean removeProduct(String id) {
        String removeProductCql = "delete from product where id='"+id+"'";
        try {
            getCassandraOperations().delete(removeProductCql);
        } catch (Exception e) {
            logger.error("exception deleting product {}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Removed product; productId={}", id);
        return true;
    }

    @Override
    public boolean removeProducts() {
        try {
            getCassandraOperations().truncate(Product.class);
        } catch (Exception e) {
            logger.error("Exception deleting products: ", e);
            return false;
        }
        logger.debug("Removed all products");
        return true;
    }
}
