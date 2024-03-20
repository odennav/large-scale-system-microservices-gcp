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

package com.ntw.oms.cart.dao;

import com.ntw.oms.cart.config.TestConfig;
import com.ntw.oms.cart.entity.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by anurag on 24/03/17.
 */
public class CartMockDao implements CartDao {

    private static final Logger logger = LoggerFactory.getLogger(CartMockDao.class);
    @Override
    public Cart getCart(String id) {
        if (id.equals(TestConfig.TEST_USER_ID)) {
            return TestConfig.createCart(TestConfig.TEST_USER_ID);
        }
        return null;
    }

    @Override
    public boolean saveCart(Cart cart) {
        return true;
    }

    @Override
    public boolean updateCart(Cart cart) {
        return true;
    }

    @Override
    public boolean removeCart(String userId) {
        return true;
    }

    @Override
    public boolean removeCarts() {
        return false;
    }

}
