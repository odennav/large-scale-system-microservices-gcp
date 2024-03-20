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

package com.ntw.oms.admin.service;

import com.ntw.common.config.ServiceID;
import com.ntw.common.status.ServiceStatus;
import com.ntw.oms.admin.api.ApiClient;
import com.ntw.oms.admin.api.ApiClientFactory;
import com.ntw.oms.admin.entity.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag on 26/07/20.
 */
@Component
public class APIDataManager {

    private static final Logger logger = LoggerFactory.getLogger(APIDataManager.class);

    @Autowired
    private ApiClientFactory apiClientFactory;

    @Value("${GatewaySvc.client.threads.size:10}")
    private int apiThreadPoolSize;

    public OperationStatus createAppData(int userCount, int productCount, String authHeader) {
        List<DataInsertTask> insertTasks = new LinkedList<>();
        for (ServiceID serviceID : ServiceID.values()) {
            if (serviceID == ServiceID.AdminSvc || serviceID == ServiceID.GatewaySvc)
                continue;
            ApiClient apiClient = apiClientFactory.createApiClient(serviceID, authHeader);
            int size;
            if (serviceID == ServiceID.AuthSvc || serviceID == ServiceID.UserProfileSvc) {
                size = userCount;
            } else if (serviceID == ServiceID.InventorySvc || serviceID == ServiceID.ProductSvc) {
                size = productCount;
            } else {
                size = 0;
            }
            ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(apiThreadPoolSize);
            for (int i = 1; i <= size; i++) {
                DataInsertTask task = new DataInsertTask(apiClient, i);
                insertTasks.add(task);
                executor.execute(task);
            }
            executor.shutdown();
            try {
                executor.awaitTermination(300, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (DataInsertTask task : insertTasks) {
            logger.info(task.getOperationStatus().toString());
            if (!task.getOperationStatus().isSuccess()) {
                return task.getOperationStatus();
            }
        }
        OperationStatus operationStatus = new OperationStatus(true);
        operationStatus.setMessage("Inserted all data");
        return operationStatus;
    }

    public OperationStatus createBootstrapData(String authHeader) {
        ApiClient apiClient = apiClientFactory.
                createApiClient(ServiceID.AuthSvc, authHeader);
        OperationStatus operationStatus = null;
        operationStatus = apiClient.insertBootstrapData();
        if (!operationStatus.isSuccess()) {
            logger.error("Unable to create bootstrap user auth data");
            return operationStatus;
        }
        apiClient = apiClientFactory.
                createApiClient(ServiceID.UserProfileSvc, authHeader);
        operationStatus = apiClient.insertBootstrapData();
        if (!operationStatus.isSuccess()) {
            logger.error("Unable to create bootstrap user profile data");
            return operationStatus;
        }
        logger.info("Created bootstrap data");
        if (operationStatus == null) {
            logger.error("Unexpected no operation status");
            operationStatus = new OperationStatus();
        }
        operationStatus.setSuccess(true);
        operationStatus.setMessage("Deleted data");
        return operationStatus;
    }

    public OperationStatus deleteAppData(String authHeader) {
        OperationStatus operationStatus = null;
        for (ServiceID serviceID : ServiceID.values()) {
            if (serviceID == ServiceID.AdminSvc || serviceID == ServiceID.GatewaySvc)
                continue;
            ApiClient apiClient = apiClientFactory.createApiClient(serviceID, authHeader);
            operationStatus = apiClient.deleteData();
            if (!operationStatus.isSuccess()) {
                return operationStatus;
            }
        }
        if (operationStatus == null) {
            logger.error("Unexpected no operation status");
            operationStatus = new OperationStatus();
        }
        operationStatus.setSuccess(true);
        operationStatus.setMessage("Deleted data");
        return operationStatus;
    }

    public List<ServiceStatus> getServicesStatus() {
        List<ServiceStatus> serviceStatusList = new LinkedList<>();
        for (ServiceID serviceID : ServiceID.values()) {
            ApiClient apiClient = apiClientFactory.createApiClient(serviceID, "");
            if (serviceID == ServiceID.AdminSvc || serviceID == ServiceID.CartSvc
                    || serviceID == ServiceID.UserProfileSvc)
                continue;
            ServiceStatus serviceStatus = apiClient.getStatus();
            serviceStatusList.add(serviceStatus);
        }
        return serviceStatusList;
    }

}

class DataInsertTask implements Runnable {

    private ApiClient apiClient;
    private int index;
    private OperationStatus operationStatus;

    public DataInsertTask(ApiClient apiClient, int index) {
        this.apiClient = apiClient;
        this.index = index;
    }

    public void run() {
        operationStatus = apiClient.insertData(index);
        setOperationStatus(operationStatus);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public int getIndex() {
        return index;
    }

    private void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }
}
