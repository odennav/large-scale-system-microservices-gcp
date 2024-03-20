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
import com.ntw.oms.admin.entity.Address;
import com.ntw.oms.admin.entity.Contact;
import com.ntw.oms.admin.entity.OperationStatus;
import com.ntw.oms.admin.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anurag on 27/06/19.
 */

@Component
public class UserProfileApiClient extends ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileApiClient.class);

    @Override
    protected ServiceID getServiceID() {
        return ServiceID.UserProfileSvc;
    }

    @Override
    protected ServiceID getEndpointServiceID() {
        return ServiceID.AuthSvc;
    }

    @Override
    protected String getServiceURI() {
        return AppConfig.USERS_PROFILE_RESOURCE_PATH;
    }

    @Override
    protected Object createObject(String id) {
        return createUserProfile(id);
    }

    @Override
    protected String getObjectId(int index) {
        return getUserId(index);
    }

    public OperationStatus insertBootstrapData() {
        String users[] = {"admin", "john"};
        OperationStatus operationStatus = null;
        for (String userId : users) {
            operationStatus = insertData(userId, createUserProfile(userId));
            if (!operationStatus.isSuccess()) {
                logger.error("Error creating bootstrap user profile data");
                return operationStatus;
            }
        }
        logger.info("Created bootstrap user auth data");
        operationStatus.setMessage("Inserted User profile bootstrap data");
        return operationStatus;
    }

    private Contact createContact(String id) {
        Contact contact = new Contact();
        contact.setName(id.substring(0, 1).toUpperCase() + id.substring(1));
        contact.setEmail(id+"@test.com");
        contact.setTelephone("0001234567");
        return contact;
    }

    private Address createAddress(String id) {
        Address address = new Address();
        address.setStreet(id+", 12th cross, 2nd main");
        address.setArea("Indira Nagar");
        address.setCity("Bangalore");
        address.setState("KA");
        address.setCountry("IN");
        address.setContact(createContact(id));
        return address;
    }

    private UserProfile createUserProfile(String id) {
        UserProfile userProfile = new UserProfile();
        userProfile.setId(id);
        userProfile.setName(id.substring(0, 1).toUpperCase() + id.substring(1));
        userProfile.setEmail(id+"@test.com");
        userProfile.setContact(createContact(id));
        userProfile.setAddress(createAddress("1"));
        List<Address> shipAddresses = new ArrayList<>();
        shipAddresses.add(createAddress("2"));
        shipAddresses.add(createAddress("3"));
        userProfile.setShippingAddresses(shipAddresses);
        return userProfile;
    }

}
