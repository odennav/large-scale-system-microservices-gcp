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

package com.ntw.oms.cart.service;

import com.ntw.common.config.AppConfig;
import com.ntw.common.config.ServiceID;
import com.ntw.common.status.ServiceAgent;
import com.ntw.oms.cart.entity.Cart;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * Rest interface for adding, searching, modifying and deleting products
 */
@RestController
@RequestMapping(AppConfig.CARTS_RESOURCE_PATH)
public class CartService {

    @Autowired
    private CartServiceImpl cartServiceBean;

    public CartServiceImpl getCartServiceBean() {
        return cartServiceBean;
    }

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Secured({Role.ADMIN, Role.USER})
    @GetMapping(path= AppConfig.CART_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cart> getCart(@PathVariable("id") String id) {
        if (id.equals("")) {
            id = getUser();
        } else if (!id.equals(getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Cart cart = getCartServiceBean().getCart(id);
        return ResponseEntity.ok().body(cart);
    }

    @Secured({Role.ADMIN, Role.USER})
    @PutMapping(path= AppConfig.CART_PATH, consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Cart> saveCart(@PathVariable("id") String id, @RequestBody Cart cart) {
        if (!cart.getId().equals(getUser()) || !cart.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boolean success = getCartServiceBean().saveCart(cart);
        if (success) {
            logger.info("Cart replaced; context={}", cart);
            try {
                return ResponseEntity.created(new URI("/cart")).body(cart);
            } catch (URISyntaxException e) {
                logger.error("Error saving cart; context={}", cart);
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cart);
    }

    @Secured({Role.ADMIN, Role.USER})
    @PostMapping(consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity modifyCart(@RequestParam("id") String id,
                            @RequestParam("productId") String productId,
                            @RequestParam("quantity") float quantity) {
        logger.info("Modify cart; cartId={}, productId={}, quantity={}", id, productId, quantity);
        if (id == null || id.equals("")) {
            id = getUser();
        } else if (!id.equals(getUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Cart modifiedCart = getCartServiceBean().modifyCart(id, productId, quantity);
        if (modifiedCart != null) {
            logger.info("Cart modified; cartId={}, productId={}, quantity={}", id, productId, quantity);
            try {
                return ResponseEntity.created(new URI("/cart")).body("Saved cart");
            } catch (URISyntaxException e) {
                logger.error("Error while modifying cart; cartId={}, productId={}, quantity={}, error={}",
                        id, productId, quantity, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Secured({Role.ADMIN, Role.USER})
    @DeleteMapping(path= AppConfig.CART_PATH, consumes=MediaType.APPLICATION_JSON_VALUE,
            produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeCart(@PathVariable("id") String id) {
        if (id.equals(getUser())) {
            if (getCartServiceBean().removeCart(id)) {
                logger.info("Cart removed; cartId={}", getUser());
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @Secured({Role.ADMIN})
    @DeleteMapping(produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeCarts() {
        if (getCartServiceBean().removeCarts()) {
                logger.info("All carts removed");
                return ResponseEntity.ok().body("REMOVED ALL CARTS");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

//    @GetMapping(path= AppConfig.STATUS_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity getServiceStatus() {
//        logger.info("Status request received");
//        String status = ServiceAgent.getServiceStatus(ServiceID.CartSvc);
//        logger.info("Status request response is {}",status);
//        return ResponseEntity.ok(status);
//    }

    private String getUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
