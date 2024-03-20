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

package com.ntw.oms.order.service;

import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import com.ntw.oms.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * Rest interface for adding, searching, modifying and deleting products
 */
@RestController
@RequestMapping(AppConfig.ORDERS_RESOURCE_PATH)
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderServiceImpl orderServiceBean;

    public OrderServiceImpl getOrderServiceBean() {
        return orderServiceBean;
    }

    /**
     * Get all orders
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @GetMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = getOrderServiceBean().getOrders(getUser());
        logger.debug("Fetched orders; context={}", orders);
        return ResponseEntity.ok().body(orders);
    }

    /**
     * Get order for an order id
     * @param id        order id to be fetched
     * @return          order object
     */
    @Secured({Role.ADMIN,Role.USER})
    @GetMapping(path= AppConfig.ORDER_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> getOrder(@PathVariable("id") String id) {
        String userId = getUser();
        if (id.equals("")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Order order = getOrderServiceBean().fetchOrder(userId, id);
        return ResponseEntity.ok().body(order);
    }

    /**
     * Create a new order. To be used by admin user only. Users create orders through cart.
     * @param order     order object to create order
     * @return          order created
     */
    @Secured({Role.ADMIN})
    @PutMapping(path= AppConfig.ORDER_PATH, consumes=MediaType.APPLICATION_JSON_VALUE,
        produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Order> saveOrder(@PathVariable("id") String orderId, @RequestBody Order order) {
        boolean success = getOrderServiceBean().saveOrder(order);
        if (success) {
            logger.debug("Order replaced; context={}", order);
            try {
                return ResponseEntity.created(new URI("/order")).body(order);
            } catch (URISyntaxException e) {
                logger.error("Error saving order; context={}", order);
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(order);
    }

    /**
     * Create order from a cart
     * @param cartId        id of cart that needs to converted into an order
     * @return
     * @throws URISyntaxException
     */
    @Secured({Role.ADMIN,Role.USER})
    @PostMapping(path= AppConfig.ORDER_CART_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createOrderFromCart(@PathVariable("cartId") String cartId) throws URISyntaxException {
        Order order = null;
        try {
            order = getOrderServiceBean().createOrder(cartId, getAuthHeader());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            logger.error("Unable to create order; cartId={}", cartId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        if (order == null) {
            logger.error("Unable to create order; cartId={}", cartId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        logger.debug("Order created; context={}; order={}", cartId, order);
        return ResponseEntity.created(new URI("/order")).body(order);
    }

    /**
     * Delete order
     * @param id        id of order to be deleted
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @DeleteMapping(path= AppConfig.ORDER_PATH, consumes=MediaType.APPLICATION_JSON_VALUE,
        produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeOrder(@PathVariable("id") String id) {
        boolean success = getOrderServiceBean().removeOrder(getUser(), id);
        if (success) {
            logger.debug("Order removed; orderId={}", id);
            return ResponseEntity.ok("ORDER DELETED");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ORDER NOT DELETED");
    }

    /**
     * Delete all orders. To be used only by admin role.
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeOrders() {
        boolean success = getOrderServiceBean().removeOrders();
        if (success) {
            logger.debug("All orders removed");
            return ResponseEntity.ok("ORDERS DELETED");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ORDERS NOT DELETED");
    }

    private String getAuthHeader() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }

    private String getUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
