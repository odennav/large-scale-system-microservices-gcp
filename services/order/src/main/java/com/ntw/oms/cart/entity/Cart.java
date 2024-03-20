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

package com.ntw.oms.cart.entity;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 19/04/17.
 */

public class Cart {
    private String id;
    private List<CartLine> cartLines;

    public Cart() {
        this.cartLines = new LinkedList<>();
    }

    public Cart(String id) {
        this.id = id;
        this.cartLines = new LinkedList<>();
    }

    public Cart(Cart cartEntity) {
        this.id = cartEntity.id;
        this.cartLines = cartEntity.cartLines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CartLine> getCartLines() {
        return cartLines;
    }

    public void setCartLines(List<CartLine> cartLines) {
        this.cartLines = cartLines;
    }

    public void addCartLine(CartLine cartLine) {
        this.getCartLines().add(cartLine);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + (id == null ? "null" : "\"" + id + "\"") + ", " +
                "\"cartLines\":" + (cartLines == null ? "null" : Arrays.toString(cartLines.toArray())) +
                "}";
    }
}
