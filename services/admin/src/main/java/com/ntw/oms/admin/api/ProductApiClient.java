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

package com.ntw.oms.admin.api;

import com.ntw.common.config.AppConfig;
import com.ntw.common.config.ServiceID;
import com.ntw.oms.admin.entity.Product;

/**
 * Created by anurag on 27/06/19.
 */
public class ProductApiClient extends ApiClient {

    @Override
    protected ServiceID getServiceID() {
        return ServiceID.ProductSvc;
    }

    @Override
    protected String getServiceURI() {
        return AppConfig.PRODUCTS_RESOURCE_PATH;
    }

    @Override
    protected Object createObject(String id) {
        return createSampleProduct(id);
    }

    @Override
    protected String getObjectId(int index) {
        return getProductId(index);
    }

    private Product createSampleProduct(String id) {
        Product product = new Product();
        product.setId(id);
        product.setName(getNameFromId(id));
        product.setPrice(10F);
        return product;
    }

}
