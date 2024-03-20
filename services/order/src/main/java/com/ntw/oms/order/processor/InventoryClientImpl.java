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

package com.ntw.oms.order.processor;

import com.google.gson.Gson;
import com.ntw.common.config.AppConfig;
import com.ntw.common.config.ServiceID;
import com.ntw.oms.order.entity.InventoryReservation;
import com.ntw.oms.order.service.OrderServiceImpl;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by anurag on 22/05/19.
 */

@Component
@RibbonClient(name = "InventoryClient")
public class InventoryClientImpl implements InventoryClient {

    // Unused with OkHttp connection
    @Value("${InventorySvc.client.cp.size:10}")
    private int httpClientPoolSize;

    @Autowired
    private LoadBalancerClient loadBalancer;

    // tracer bean is created by the spring jaeger cloud library itself
    @Autowired
    private Tracer tracer;

    //private static HttpClient client;
    private static OkHttpClient client;

    @PostConstruct
    public void postConstruct() {
        // Each client has its own connection pool. Need one instance per class.
        client = new OkHttpClient();
    }

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public LoadBalancerClient getLoadBalancer() {
        return loadBalancer;
    }

    public void setLoadBalancer(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    @Override
    public boolean reserveInventory(InventoryReservation inventoryReservation) throws IOException {
        ServiceInstance instance = getLoadBalancer().choose(ServiceID.InventorySvc.toString());
        if (instance == null) {
            logger.error("Unable to reserve inventory, as inventory service is not available");
            throw new IOException("Inventory service is not available");
        }
        StringBuilder url = new StringBuilder()
                .append("http://").append(instance.getHost())
                .append(":").append(instance.getPort())
                .append(AppConfig.INVENTORY_RESOURCE_PATH)
                .append(AppConfig.INVENTORY_RESERVATION_PATH);
        String invResJson = (new Gson()).toJson(inventoryReservation);
        RequestBody body = RequestBody.create(invResJson, okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request.Builder requestBuilder = new Request.Builder()
                .url(url.toString())
                .post(body);
        String authHeader = OrderServiceImpl.getThreadLocal().get();
        requestBuilder.addHeader("Authorization", authHeader);
        Span invRemoteCallSpan = tracer.buildSpan("reserveInventoryRemote").asChildOf(tracer.activeSpan()).start();
        tracer.inject(invRemoteCallSpan.context(),
                    Format.Builtin.HTTP_HEADERS,
                    new RequestBuilderCarrier(requestBuilder));
        Request request = requestBuilder.build();
        try (Response response = client.newCall(request).execute()) {
            if (response.code() == 200) {
                logger.debug("Reserved inventory successfully; context={}", inventoryReservation);
                return true;
            }
            logger.debug("Unable to reserve inventory, got error; errorCode={}, message={} context={}",
                        response.code(), response.message(), inventoryReservation);
            return false;
        } catch (IOException e) {
            logger.error("Error calling InventorySvc while reserving inventory; context={}", inventoryReservation);
            logger.error(e.getMessage(), e);
            throw e;
        }
        finally {
            invRemoteCallSpan.finish();
        }
    }
}

class RequestBuilderCarrier implements io.opentracing.propagation.TextMap {
    private final Request.Builder builder;

    RequestBuilderCarrier(Request.Builder builder) {
        this.builder = builder;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {
        builder.addHeader(key, value);
    }
}

