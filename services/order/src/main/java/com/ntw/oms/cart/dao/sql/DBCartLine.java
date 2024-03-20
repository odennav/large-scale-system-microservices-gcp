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

package com.ntw.oms.cart.dao.sql;

import com.ntw.oms.cart.entity.Cart;
import com.ntw.oms.cart.entity.CartLine;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 19/04/17.
 */
public class DBCartLine {
    private String cartId;
    private int cartLineId;

    private String productId;
    private float quantity;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getCartLineId() {
        return cartLineId;
    }

    public void setCartLineId(int cartLineId) {
        this.cartLineId = cartLineId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":\"" + cartLineId + "\"" + ", " +
                "\"cartId\":" + (cartId == null ? "null" : "\"" + cartId + "\"") + ", " +
                "\"productId\":" + (productId == null ? "null" : "\"" + productId + "\"") + ", " +
                "\"quantity\":\"" + quantity + "\"" +
                "}";
    }

    public static List<DBCartLine> createDBCartLines(Cart cart) {
        List<DBCartLine> dbCartLines = new LinkedList<>();
        for (CartLine cartLine : cart.getCartLines()) {
            DBCartLine dbCartLine = new DBCartLine();
            dbCartLine.setCartLineId(cartLine.getId());
            dbCartLine.setCartId(cart.getId());
            dbCartLine.setProductId(cartLine.getProductId());
            dbCartLine.setQuantity(cartLine.getQuantity());
            dbCartLines.add(dbCartLine);
        }
        return dbCartLines;
    }

    public static Cart getCart(String cartId, List<DBCartLine> dbCartLines) {
        Cart cart = new Cart();
        cart.setId(cartId);
        for (DBCartLine dbCartLine : dbCartLines) {
            CartLine cartLine = new CartLine();
            cartLine.setId(dbCartLine.getCartLineId());
            cartLine.setProductId(dbCartLine.getProductId());
            cartLine.setQuantity(dbCartLine.getQuantity());
            cart.getCartLines().add(cartLine);
        }
        return cart;
    }
}
