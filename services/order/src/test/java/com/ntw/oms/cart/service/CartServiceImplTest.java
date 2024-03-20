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

import com.ntw.oms.cart.config.TestConfig;
import com.ntw.oms.cart.dao.CartMockDao;
import com.ntw.oms.cart.entity.Cart;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by anurag on 12/05/17.
 */
public class CartServiceImplTest extends TestCase {

    private CartServiceImpl cartService;

    public CartServiceImplTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( CartServiceImplTest.class );
    }

    public void setUp() throws Exception {
        super.setUp();
        cartService = new CartServiceImpl();
        cartService.setCartDaoBean(new CartMockDao());
    }

    public void testDummy() {
        Assert.assertEquals(true, true);
    }

    public void testCreateCart() {
        Cart cart = TestConfig.createCart(TestConfig.TEST_USER_ID);
        boolean success = cartService.saveCart(cart);
        Assert.assertEquals(true, success);
    }

    public void testFetchCart() {
        // Fetch cart
        Cart cart = cartService.getCart(TestConfig.TEST_USER_ID);
        Assert.assertNotNull(cart);
        Assert.assertEquals(TestConfig.TEST_USER_ID, cart.getId());
        Assert.assertEquals(cart.getCartLines().size(), cart.getCartLines().size());
        Assert.assertEquals(cart.getCartLines().get(0).getProductId(),
                cart.getCartLines().get(0).getProductId());
        Assert.assertEquals(cart.getCartLines().get(1).getProductId(),
                cart.getCartLines().get(1).getProductId());
        Assert.assertEquals(cart.getCartLines().get(0).getQuantity(),
                cart.getCartLines().get(0).getQuantity());
        Assert.assertEquals(cart.getCartLines().get(1).getQuantity(),
                cart.getCartLines().get(1).getQuantity());

        // Fetch cart
        Cart cart1 = cartService.getCart("Non-Existent-Id");
        Assert.assertNotNull(cart1);
        Assert.assertEquals(0, cart1.getCartLines().size());
    }

    public void testAddToCart() {
        // Modify cart
        Cart cart = cartService.getCart(TestConfig.TEST_USER_ID);
        float testQuantity = 1F;
        Cart modifiedCart = cartService.modifyCart(cart.getId(), TestConfig.TEST_PRODUCT_ID_3, testQuantity);
        Assert.assertNotNull(modifiedCart);
        Assert.assertEquals(3, modifiedCart.getCartLines().size());
        Assert.assertEquals(TestConfig.TEST_PRODUCT_ID_3, modifiedCart.getCartLines().get(2).getProductId());
        Assert.assertEquals(testQuantity, modifiedCart.getCartLines().get(2).getQuantity());
    }

    public void testRemoveFromCart() {
        //Remove from cart
        float testQuantity = 0F;
        Cart cart = cartService.getCart(TestConfig.TEST_USER_ID);
        Cart modifiedCart = cartService.modifyCart(cart.getId(), TestConfig.TEST_PRODUCT_ID_2, testQuantity);
        Assert.assertNotNull(modifiedCart);
        Assert.assertEquals(cart.getCartLines().size()-1, modifiedCart.getCartLines().size());
    }

    public void testRemoveCart() {
        // Remove cart
        boolean success = cartService.removeCart(TestConfig.TEST_USER_ID);
        Assert.assertEquals(true, success);
    }

}
