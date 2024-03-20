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

import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import com.ntw.oms.admin.entity.OperationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by anurag on 23/05/19.
 */
@RestController
@RequestMapping(AppConfig.ADMIN_RESOURCE_PATH)
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private AdminServiceImpl adminServiceBean;

    private String getUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    private String getAuthHeader() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getDetails();
    }


    @Secured({Role.ADMIN})
    @PostMapping(path = "/dataset",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createAppData(@RequestParam("userCount") Integer userCount,
                                                 @RequestParam("productCount") Integer productCount) {
        logger.info("Create test data with userCount={} and productCount={}", userCount, productCount);
        OperationStatus operationStatus =
                adminServiceBean.createAppData(userCount, productCount, getAuthHeader());
        if (!operationStatus.isSuccess()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(operationStatus.getMessage());
        }
        logger.info("Created test data with userCount={} and productCount={}", userCount, productCount);
        return ResponseEntity.ok().body(operationStatus.getMessage());
    }


    @Secured({Role.ADMIN})
    @DeleteMapping(path = "/dataset", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteTestData() {
        logger.info("Delete data");
        OperationStatus operationStatus = adminServiceBean.deleteAppData(getAuthHeader());
        logger.info("Deleted data with status={}",operationStatus.toString());
        if (operationStatus.isSuccess())
            return ResponseEntity.ok().body(operationStatus.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(operationStatus.getMessage());
    }

}
