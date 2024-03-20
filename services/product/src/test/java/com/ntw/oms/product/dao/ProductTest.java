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

package com.ntw.oms.product.dao;

/**
 * Created by anurag on 11/05/17.
 */

import com.ntw.oms.product.config.TestConfig;
import com.ntw.oms.product.entity.Product;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ProductTest extends TestCase {

    private ProductDao productDaoBean;

    public ProductTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( ProductTest.class );
    }

    public void setUp() {
        productDaoBean = new ProductMockDao();
    }

    public void testDummy() {
        Assert.assertEquals(true, true);
    }

    public void testAddProduct() {
        Product product1 = TestConfig.createProduct(TestConfig.productTestId_1);
        boolean success = productDaoBean.addProduct(product1);
        Assert.assertTrue(success);
    }

    public void testGetProduct() {
        Product product1 = TestConfig.createProduct(TestConfig.productTestId_1);
        Product fetchedProduct = productDaoBean.getProduct(product1.getId());

        Assert.assertNotNull(fetchedProduct);
        Assert.assertEquals(product1.getId(), fetchedProduct.getId());
        Assert.assertEquals(product1.getName(), fetchedProduct.getName());
        Assert.assertEquals(product1.getPrice(), fetchedProduct.getPrice());

        Product fetchedProductBad = productDaoBean.getProduct(TestConfig.productTestId_Bad);
        Assert.assertNull(fetchedProductBad);
    }

    public void testModifyProduct() {
        Product product1 = TestConfig.createProduct(TestConfig.productTestId_1);
        product1.setPrice(TestConfig.productTestPrice_2);
        Product retProduct = productDaoBean.modifyProduct(product1);
        Assert.assertNotNull(retProduct);
        Assert.assertEquals(product1.getId(), retProduct.getId());
        Assert.assertEquals(product1.getPrice(), retProduct.getPrice());
    }

    public void testRemoveProduct() {
        Product product1 = TestConfig.createProduct(TestConfig.productTestId_1);
        boolean result = productDaoBean.removeProduct(product1.getId());
        Assert.assertTrue(result);
    }

}
