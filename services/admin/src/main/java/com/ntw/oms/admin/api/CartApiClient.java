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

/**
 * Created by anurag on 15/08/20.
 */
public class CartApiClient extends ApiClient {

    @Override
    protected ServiceID getServiceID() {
        return ServiceID.CartSvc;
    }

    @Override
    protected ServiceID getEndpointServiceID() {
        return ServiceID.OrderSvc;
    }

    @Override
    protected String getServiceURI() {
        return AppConfig.CARTS_RESOURCE_PATH;
    }

    @Override
    protected Object createObject(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getObjectId(int index) {
        throw new UnsupportedOperationException();
    }
}
