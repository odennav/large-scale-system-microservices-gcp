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

package com.ntw.oms.product.dao.sql;

import com.ntw.oms.product.dao.ProductDao;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by anurag on 24/03/17.
 */
@Component("SQL")
public class DBProductDao implements ProductDao {

    private static final Logger logger = LoggerFactory.getLogger(DBProductDao.class);

    @Autowired(required = false)
    JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> getProducts() {
        String allProductSql = "select * from Product";
        return queryProducts(allProductSql);
    }

    private List<Product> queryProducts(String query) {
        List<Product> products;
        try {
            products = jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            logger.error("Exception fetching products: ", e);
            return null;
        }
        if (products.isEmpty()) {
            logger.error("Unable to get Products; context={}", query);
            return new LinkedList<>();
        }
        logger.debug("Fetched {} products", products.size());
        logger.debug("Fetched products; context={}", products);
        return products;
    }

    @Override
    public List<Product> getProducts(List<String> ids) {
        StringBuilder productSql = new StringBuilder("select * from product where id IN ");
        productSql.append("(");
        int i=0;
        for (String id : ids) {
            productSql.append("'").append(id).append("'");
            if (++i < ids.size()) productSql.append(",");
        }
        productSql.append(")");
        return queryProducts(productSql.toString());
    }

    @Override
    public Product getProduct(String id) {
        String productSql = "select * from product where id=?";
        Product product;
        try {
            product = jdbcTemplate.queryForObject(productSql,
                    new Object[]{id}, new BeanPropertyRowMapper<>(Product.class));
        } catch (Exception e) {
            logger.debug("No product found; productId={}", id);
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
        String addProductSql = "insert into Product (id, name, price) values (?,?,?)";
        int retValue = jdbcTemplate.update(addProductSql,
                new Object[] {product.getId(), product.getName(), Float.valueOf(product.getPrice())});
        if (retValue <= 0) {
            logger.error("Unable to add product; productId={}", product);
            return false;
        }
        logger.debug("Added product; context={}", product);
        return true;
    }

    @Override
    public Product modifyProduct(Product product) {
        String modifyProductSql = "update Product set id=?, name=?, price=?";
        int retValue;
        try {
            retValue = jdbcTemplate.update(modifyProductSql,
                    new Object[]{product.getId(), product.getName(), Float.valueOf(product.getPrice())});
        } catch (Exception e) {
            logger.error("Exception modifying a product; context={}", product);
            logger.error("Exception message: ", e);
            return null;
        }
        if (retValue <= 0) {
            logger.error("Unable to modify product; productId={}", product);
            return null;
        }
        logger.debug("Modified product; context={}", product);
        return product;
    }

    @Override
    public boolean removeProduct(String id) {
        String removeProductSql = "delete from product where id=?";
        int retValue;
        try {
            retValue = jdbcTemplate.update(removeProductSql,
                    new Object[]{id});
        } catch (Exception e) {
            logger.error("Exception deleting product {}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        if (retValue <= 0) {
            logger.debug("Unable to delete product with id {}", id);
            return false;
        }
        logger.debug("Removed product; productId={}", id);
        return true;
    }

    @Override
    public boolean removeProducts() {
        String removeProductSql = "delete from product";
        int retValue;
        try {
            retValue = jdbcTemplate.update(removeProductSql);
        } catch (Exception e) {
            logger.error("Exception deleting product");
            logger.error("Exception message: ", e);
            return false;
        }
        if (retValue < 0) {
            logger.debug("Unable to delete products");
            return false;
        }
        logger.debug("Removed all products");
        return true;
    }
}
