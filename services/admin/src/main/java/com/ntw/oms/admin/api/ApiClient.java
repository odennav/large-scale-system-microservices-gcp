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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.ntw.common.config.AppConfig;
import com.ntw.common.config.ServiceID;
import com.ntw.common.status.ServiceStatus;
import com.ntw.oms.admin.entity.OperationStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.MediaType;

/**
 * Created by anurag on 26/07/20.
 */

public abstract class ApiClient {

    protected String authHeader;
    protected LoadBalancerClient loadBalancer;
    protected ObjectMapper mapper;
    protected HttpClient client;

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    public void setAuthHeader(String authHeader) {
        this.authHeader = authHeader;
    }

    public void setLoadBalancer(LoadBalancerClient loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void setClient(HttpClient client) {
        this.client = client;
    }

    protected abstract ServiceID getServiceID();

    protected ServiceID getEndpointServiceID() {
        return getServiceID();
    }

    protected abstract String getServiceURI();

    protected abstract Object createObject(String id);

    protected abstract String getObjectId(int index);

    public OperationStatus insertBootstrapData() {
        throw new UnsupportedOperationException();
    }

    public OperationStatus insertData(int index) {
        String id = getObjectId(index);
        Object object = createObject(id);
        return insertData(id, object);
    }

    public OperationStatus insertData(String id, Object object) {
        String uri = new StringBuilder().append(getServiceURI())
                .append("/").append(id).toString();
        OperationStatus operationStatus = new OperationStatus(false);
        operationStatus.setMessage("Unable to convert object to json: "+object.toString());
        String jsonObject = null;
        try {
            jsonObject = mapper.writeValueAsString(object);
        } catch (JsonProcessingException jpe) {
            logger.error("Unable to insert data for object {} due to json processing", object);
            logger.error(jpe.getMessage(), jpe);
            return operationStatus;
        }
        if (jsonObject == null) {
            logger.error("Unable to process insert data for object {}", object.toString());
            return operationStatus;
        }
        operationStatus = insertData(uri, jsonObject);
        return operationStatus;
    }

    protected OperationStatus insertData(String uri, String data) {
        OperationStatus operationStatus = new OperationStatus();
        operationStatus.setSuccess(false);
        ServiceInstance instance = loadBalancer.choose(getEndpointServiceID().toString());
        if (instance == null) {
            operationStatus.setMessage("Unknown host for: "+getEndpointServiceID().toString());
            logger.error("Service not available: "+getEndpointServiceID().toString());
            return operationStatus;
        }
        String host = instance.getHost();
        int port = instance.getPort();
        try {
            HttpClientResponse response = client.sendPut(host, port, uri,
                    authHeader, MediaType.APPLICATION_JSON_VALUE, data);
            operationStatus.setSuccess(true);
            operationStatus.setMessage("Inserted data: "+data);
            logger.debug("Data creation response code: {} and body: {}",
                    response.getStatus(), response.getBody());
        } catch (Exception e) {
            operationStatus.setMessage("Error inserting data: "+e.getMessage());
            logger.error("Error inserting data: "+data);
            logger.error(e.getMessage(),e);
        }
        return operationStatus;
    }

    public OperationStatus deleteData() {
        OperationStatus operationStatus = new OperationStatus();
        operationStatus.setSuccess(false);
        ServiceInstance instance = loadBalancer.choose(getEndpointServiceID().toString());
        if (instance == null) {
            operationStatus.setMessage("Unknown host for: "+getEndpointServiceID().toString());
            logger.error("Service not available: "+getEndpointServiceID().toString());
            return operationStatus;
        }
        String host = instance.getHost();
        int port = instance.getPort();
        try {
            String uri = new StringBuilder().append(getServiceURI()).toString();
            HttpClientResponse response = client.sendDelete(host, port, uri, authHeader);
            operationStatus.setSuccess(true);
            operationStatus.setMessage("Deleted data");
            logger.info("Data deletion response code: {} and body: {}",
                    response.getStatus(), response.getBody());
        } catch (Exception e) {
            operationStatus.setSuccess(false);
            operationStatus.setMessage("Error deleting data: "+e.getMessage());
            logger.error("Error deleting data");
            logger.error(e.getMessage(),e);
        }
        return operationStatus;
    }

    public ServiceStatus getStatus() {
        ServiceInstance instance = loadBalancer.choose(getEndpointServiceID().toString());
        ServiceStatus serviceStatus = new ServiceStatus(getEndpointServiceID().toString());
        if (instance == null) {
            serviceStatus.setServiceHost("Unknown");
            serviceStatus.setServiceTime("Service not available");
            return serviceStatus;
        }
        String host = instance.getHost();
        int port = instance.getPort();
        try {
            String uri = new StringBuilder().append(AppConfig.STATUS_PATH).toString();
            HttpClientResponse response = client.sendGet(host, port, uri, authHeader, "");
            logger.info("Status code: {} and body: {}",
                    response.getStatus(), response.getBody());
            String status = response.getBody();
            return (new Gson()).fromJson(status, ServiceStatus.class);
        } catch (Exception e) {
            logger.error("Error fetching server status: {}", e.getMessage());
            serviceStatus.setServiceHost(host+":"+port);
            serviceStatus.setServiceTime("Not Reachable: "+e.getMessage());
            return serviceStatus;
        }
    }

    private static final String PRODUCT_ID_PREFIX = "test-product-";

    protected String getProductId(int index) {
        return new StringBuilder(PRODUCT_ID_PREFIX).
                append(String.format("%05d", index)).toString();
    }

    private static final String USER_ID_PREFIX = "test-user-";

    protected String getUserId(int index) {
        return new StringBuilder(USER_ID_PREFIX).
                append(String.format("%05d", index)).toString();
    }

    protected static String getNameFromId(String id) {
        StringBuilder buffer = new StringBuilder(id);
        for (int i=0; i<buffer.length(); i++) {
            if (buffer.charAt(i) == '-') {
                buffer.setCharAt(i,' ');
            }
        }
        return StringUtils.capitalize(buffer.toString());
    }

}
