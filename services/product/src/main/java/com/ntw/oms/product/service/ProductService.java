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

import com.google.gson.Gson;
import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import com.ntw.oms.product.entity.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * Rest interface for adding, searching, modifying and deleting products
 */
@RestController
@RequestMapping(AppConfig.PRODUCTS_RESOURCE_PATH)
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductServiceImpl productServiceBean;

    public ProductServiceImpl getProductServiceBean() {
        return productServiceBean;
    }

    /**
     * Get all products
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Product>> getProducts() {
        logger.debug("Request for get products");
        List<Product> products = getProductServiceBean().getProducts();
        logger.info("Fetched {} products", products.size());
        if (products.size() > 0) {
            // Avoid a large log of products - print just one
            logger.info("First product; context={}", products.get(0));
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get products for a list of ids
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @PostMapping(path="/ids", consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<Product>> getProductsByIds(@RequestBody List<String> ids) {
        logger.debug("Request for get products by ids");
        List<Product> products = getProductServiceBean().getProductsByIds(ids);
        logger.info("Fetched products: ", (new Gson()).toJson(products));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Get product with an id
     * @param id        product id to be fetched
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @GetMapping(path = AppConfig.PRODUCT_PATH, produces = "application/json")
    public ResponseEntity<Product> getProduct(@PathVariable("id") String id) {
        logger.debug("Request for get product {}", id);
        Product product = getProductServiceBean().getProduct(id);
        logger.info("Fetched product; context={}", product);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Insert a new product. Usage limited to admin role.
     * @param product
     * @return
     * @throws URISyntaxException
     */
    @Secured({Role.ADMIN})
    @PutMapping(path= AppConfig.PRODUCT_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> createProduct(@PathVariable("id") String id,
                                                 @RequestBody Product product) throws URISyntaxException {
        if (!id.equals(product.getId())) {
            return ResponseEntity.badRequest().build();
        }
        boolean success = getProductServiceBean().addProduct(product);
        if (success) {
            logger.info("Added new product; context={}", product);
            return new ResponseEntity<>(product, HttpStatus.OK);
        }
        return new ResponseEntity<>(product, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Update an existing product. Usage limited to admin role.
     * @param id
     * @param product
     * @return
     */
    @Secured({Role.ADMIN})
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Product> modifyProduct(@RequestParam("id") String id, Product product) {
        if (!id.equals(product.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Product modifiedProduct = getProductServiceBean().modifyProduct(product);
        if (modifiedProduct != null) {
            logger.info("Modified product; context={}", product);
            return new ResponseEntity<>(modifiedProduct, HttpStatus.OK);
        }
        return new ResponseEntity<>(product, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Delete a product. Usage limited to admin role.
     * @param id    id of product to be deleted.
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(path= AppConfig.PRODUCT_PATH,
            consumes = "application/json", produces = "application/text")
    public ResponseEntity<String> removeProduct(@RequestParam("id") String id) {
        boolean removed = getProductServiceBean().removeProduct(id);
        if (removed) {
            logger.info("Removed product; productId={}", id);
            return new ResponseEntity<>("Removed product id="+id, HttpStatus.OK);
        }
        return new ResponseEntity<>("Cannot remove product id="+id, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Delete all products. Usage limited to admin role.
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeProducts() {
        boolean removed = getProductServiceBean().removeProducts();
        if (removed) {
            logger.info("Removed all products");
            return new ResponseEntity<>("ALL PRODUCTS DELETED", HttpStatus.OK);
        }
        return new ResponseEntity<>("PRODUCTS NOT DELETED", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
