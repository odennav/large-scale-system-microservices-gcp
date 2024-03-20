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


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntw.common.config.ServiceID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.stereotype.Component;

/**
 * Created by anurag on 26/07/20.
 */

@Component
@RibbonClient(name = "ApiClient")
public class ApiClientFactory {

    @Autowired
    private LoadBalancerClient loadBalancer;

    @Value("${GatewaySvc.client.cp.size:100}")
    private int httpClientPoolSize;

    public ApiClient createApiClient(ServiceID serviceID,
                                        String authHeader) {
        ApiClient apiClient = null;
        switch(serviceID) {
            case AuthSvc: {
                apiClient = new UserAuthApiClient();
                break;
            }
            case UserProfileSvc: {
                apiClient = new UserProfileApiClient();
                break;
            }
            case ProductSvc: {
                apiClient = new ProductApiClient();
                break;
            }
            case InventorySvc: {
                apiClient = new InventoryApiClient();
                break;
            }
            case CartSvc:{
                apiClient = new CartApiClient();
                break;
            }
            case OrderSvc: {
                apiClient = new OrderApiClient();
                break;
            }
            case GatewaySvc: {
                apiClient = new GatewayApiClient();
                break;
            }
            default: {
                return null;
            }
        }
        apiClient.setAuthHeader(authHeader);
        apiClient.setLoadBalancer(loadBalancer);
        apiClient.setMapper(new ObjectMapper());
        apiClient.setClient(new HttpClient(httpClientPoolSize));
        return apiClient;
    }

}
