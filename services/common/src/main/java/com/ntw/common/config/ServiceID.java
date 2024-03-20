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

package com.ntw.common.config;

/**
 * Created by anurag on 13/07/19.
 */
public enum ServiceID {

    AuthSvc("AuthSvc"), ProductSvc("ProductSvc"), UserProfileSvc("UserProfileSvc"),
    CartSvc("CartSvc"), OrderSvc("OrderSvc"), InventorySvc("InventorySvc"),
    GatewaySvc("GatewaySvc"), AdminSvc("AdminSvc");

    private String serviceId;

    ServiceID(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return serviceId;
    }
}
