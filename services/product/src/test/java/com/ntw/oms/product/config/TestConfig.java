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

package com.ntw.oms.product.config;

import com.ntw.oms.product.entity.Product;

/**
 * Created by anurag on 14/12/17.
 */
public class TestConfig {
    public static String productTestId_1 = "junit-test-prod-1";
    public static String productTestName_1 = "junit-test-prod-name-1";
    public static Float productTestPrice_1 = 10.0F;
    public static Float productTestPrice_2 = 100.0F;

    public static String productTestId_Bad = "junit-test-prod-2";

    public static Product createProduct(String id) {
        Product product = new Product();
        product.setId(id);
        product.setName(TestConfig.productTestName_1);
        product.setPrice(TestConfig.productTestPrice_1);
        return product;
    }

}
