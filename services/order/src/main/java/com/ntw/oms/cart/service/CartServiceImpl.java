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

import com.ntw.oms.cart.dao.CartDaoFactory;
import com.ntw.oms.cart.entity.Cart;
import com.ntw.oms.cart.entity.CartLine;
import com.ntw.oms.cart.dao.CartDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 12/05/17.
 */

/**
 * Provides implementation of Cart Service methods
 */
@Component
public class CartServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    @Autowired
    private CartDaoFactory cartDaoFactory;

    private CartDao cartDaoBean;

    @Value("${database.type}")
    private String cartDBType;

    @PostConstruct
    public void postConstruct()
    {
        this.cartDaoBean = cartDaoFactory.getCartDao("Cart"+cartDBType);
    }

    public void setCartDaoBean(CartDao cartDaoBean) {
        this.cartDaoBean = cartDaoBean;
    }

    public CartDao getCartDaoBean() {
        return cartDaoBean;
    }

    /**
     *
     * @param id        unique id of cart
     * @return          cart object
     */
    public Cart getCart(String id) {
        Cart cart = getCartDaoBean().getCart(id);
        if (cart == null) {
            cart = new Cart(id);
        } else if (cart.getCartLines() == null) {
            cart.setCartLines(new LinkedList<>());
        }
        logger.debug("Fetched cart; context={}", cart);
        return cart;
    }

    /**
     * Writes cart to DB
     * @param cart      cart object to be persisted
     * @return
     */
    public boolean saveCart(Cart cart) {
        return getCartDaoBean().saveCart(cart);
    }

    public Cart modifyCart(String id, String productId, float quantity) {
        Cart cart = getCartDaoBean().getCart(id);
        if (cart == null) {
            cart = new Cart(id);
            CartLine cartLine = new CartLine(1, productId, quantity);
            cart.addCartLine(cartLine);
            // Save to db
            saveCart(cart);
            return cart;
        }
        // Cart already exists
        List<CartLine> cartLines = cart.getCartLines();
        if (quantity > 0) {
            // Add or update cartLine
            boolean foundCartLine = false;
            for (CartLine cartLine : cartLines) {
                // Update cart line if product already present
                if (cartLine.getProductId().equals(productId)) {
                    logger.debug("Update cart line; cartId={}, productId={}, quantity={}", id, productId, quantity);
                    cartLine.setQuantity(cartLine.getQuantity()+quantity);
                    foundCartLine = true;
                    break;
                }
            }
            if (!foundCartLine) {
                // Add new cart line
                int numCartLines = cart.getCartLines().size();
                int seq = (numCartLines == 0) ? 1 : cart.getCartLines().get(numCartLines-1).getId() + 1;
                CartLine cartLine = new CartLine(seq, productId, quantity);
                cartLines.add(cartLine);
                logger.debug("Adding new cartLine; context={}", cartLine);
            }
        } else {
            // Remove cart line
            logger.debug("Remove from cart; cartId={}, productId={}, quantity={}", id, productId, quantity);
            Iterator<CartLine> iter = cartLines.iterator();
            while (iter.hasNext()) {
                CartLine cartLine = iter.next();
                if (cartLine.getProductId().equals(productId)) {
                    iter.remove();
                    break;
                }
            }
            if (cartLines.size() == 0) {
                logger.debug("Cart is now empty after cartline removal");
            }
        }
        // Save modified cart to DB
        logger.debug("Modifying cart; RetryCount={}, context={}", cart);
        if (! getCartDaoBean().updateCart(cart)) {
            logger.error("Update cart failed; context={}", cart);
            return null;
        }
        return cart;
    }

    /**
     * Remove all carts
     * @return  true is successful
     */
    public boolean removeCarts() {
        return getCartDaoBean().removeCarts();
    }

    /**
     *
     * @param id        unique id of cart to be removed
     * @return
     */
    public boolean removeCart(String id) {
        return getCartDaoBean().removeCart(id);
    }

}
