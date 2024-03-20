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

package com.ntw.oms.product.service;

import com.ntw.oms.product.dao.ProductDao;
import com.ntw.oms.product.dao.ProductDaoFactory;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by anurag on 30/05/17.
 */
@Configuration
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
// https://www.baeldung.com/spring-boot-failed-to-configure-data-source
@Component
public class ProductServiceImpl {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private static final String REDIS_PRODUCTS_MAP_KEY = "products";

    @Autowired
    private ProductDaoFactory productDaoFactory;

    private ProductDao productDaoBean;

    @Value("${database.type}")
    private String productDBType;

    @Autowired(required = false)
    RedisTemplate<String, Product> redisTemplate;

    @PostConstruct
    public void postConstruct()
    {
        this.productDaoBean = productDaoFactory.getProductDao(productDBType);
    }

    public ProductDao getProductDaoBean() {
        return productDaoBean;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    // Redis Product cache is set to no eviction policy which is also the default policy //
    ///////////////////////////////////////////////////////////////////////////////////////

    public List<Product> getProducts() {
        List<Product> products = getProductsFromCache();
        if (products == null || products.size() == 0) {
            products = getProductDaoBean().getProducts();
            addProductsToCache(products);
        }
        products.sort(new Comparator<Product>() {
            @Override
            public int compare(Product o1, Product o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });
        return products;
    }

    public List<Product> getProductsByIds(List<String> ids) {
        List<Product> products = getProductsFromCache(ids);
        if (products == null || products.size() == 0) {
            // Get all from DB
            products = getProductDaoBean().getProducts(ids);
            addProductsToCache(products);
            return products;
        }
        if (products.size() == ids.size()) {
            // All products found in cache
            return products;
        }
        // Find missing products in cache
        Map<String, Product> productMap = new HashMap<>();
        products.forEach(product -> {
            productMap.put(product.getId(), product);
        });
        List<String> missingIds = new LinkedList<>();
        ids.forEach(id -> {
            Product product = productMap.get(id);
            if (product == null) missingIds.add(id);
        });
        // Get missing products from DB
        List<Product> mapProducts = new LinkedList<>();
        products = getProductDaoBean().getProducts(missingIds);
        addProductsToCache(products);
        // Put missing products in map to make it complete
        products.forEach(product -> productMap.put(product.getId(), product));
        // Get all products from map in the order of ids
        ids.forEach(id -> {
            mapProducts.add(productMap.get(id));
        });
        return mapProducts;
    }

    public Product getProduct(String id) {
        Product product = getProductFromCache(id);
        if (product == null) {
            product = getProductDaoBean().getProduct(id);
            addProductToCache(product);
        }
        return product;
    }

    public boolean addProduct(Product product) {
        boolean success = getProductDaoBean().addProduct(product);
        if (success)
            addProductToCache(product);
        return success;
    }

    public Product modifyProduct(Product product) {
        product = getProductDaoBean().modifyProduct(product);
        if (product != null)
            addProductToCache(product);
        return product;
    }

    public boolean removeProduct(String id) {
        removeProductFromCache(id);
        return getProductDaoBean().removeProduct(id);
    }

    public boolean removeProducts() {
        removeProductsFromCache();
        return getProductDaoBean().removeProducts();
    }

    private void addProductsToCache(List<Product> products) {
        if (redisTemplate == null)
            return;
        Map<String, Product> productMap = new HashMap<>();
        products.forEach(product -> productMap.put(product.getId(), product));
        try {
            redisTemplate.opsForHash().putAll(REDIS_PRODUCTS_MAP_KEY, productMap);
        } catch (Exception e) {
            logger.error("Unable to access redis cache for setProducts: ", e);
        }
    }

    private void addProductToCache(Product product) {
        if (redisTemplate == null)
            return;
        try {
            redisTemplate.opsForHash().put(REDIS_PRODUCTS_MAP_KEY, product.getId(), product);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for addProduct: ", e);
        }
    }

    private List<Product> getProductsFromCache() {
        List<Product> products = new LinkedList<>();
        if (redisTemplate != null) {
            Map<String, Product> productMap = null;
            try {
                HashOperations<String, String, Product> hashOps = redisTemplate.opsForHash();
                productMap = hashOps.entries(REDIS_PRODUCTS_MAP_KEY);
            } catch (Exception e) {
                logger.error("Unable to access redis cache for getProducts: ", e);
            }
            if (productMap != null) {
                productMap.values().forEach(product -> products.add(product));
            }
        }
        return products;
    }

    private List<Product> getProductsFromCache(List<String> ids) {
        List<Product> products = new LinkedList<>();
        if (redisTemplate != null) {
            try {
                HashOperations<String, String, Product> hashOps = redisTemplate.opsForHash();
                products = hashOps.multiGet(REDIS_PRODUCTS_MAP_KEY, ids);
            } catch (Exception e) {
                logger.error("Unable to access redis cache for getProductsByIds: ", e);
            }
        }
        return products;
    }

    private Product getProductFromCache(String id) {
        Product product = null;
        if (redisTemplate != null) {
            try {
                HashOperations<String, String, Product> hashOps = redisTemplate.opsForHash();
                product = hashOps.get(REDIS_PRODUCTS_MAP_KEY, id);
            } catch (Exception e) {
                logger.error("Unable to access redis cache for getProduct: ", e);
            }
        }
        return product;
    }

    private void removeProductsFromCache() {
        if (redisTemplate == null)
            return;
        try {
            redisTemplate.delete(REDIS_PRODUCTS_MAP_KEY);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProducts: ", e);
        }
    }

    private void removeProductFromCache(String id) {
        if (redisTemplate == null)
            return;
        try {
            redisTemplate.opsForHash().put(REDIS_PRODUCTS_MAP_KEY, id, null);
        } catch(Exception e) {
            logger.error("Unable to access redis cache for removeProduct: ", e);
        }
    }

}
