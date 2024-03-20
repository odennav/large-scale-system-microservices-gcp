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

package com.ntw.oms.user.config;

import com.ntw.oms.user.entity.Address;
import com.ntw.oms.user.entity.Contact;
import com.ntw.oms.user.entity.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anurag on 16/05/17.
 */
public class TestConfig {

    static Contact createContact(String id) {
        Contact contact = new Contact();
        contact.setName(id.substring(0, 1).toUpperCase() + id.substring(1));
        contact.setEmail(id+"@test.com");
        contact.setTelephone("0001234567");
        return contact;
    }

    static Address createAddress(String id) {
        Address address = new Address();
        address.setStreet(id+", 12th cross, 2nd main");
        address.setArea("Indira Nagar");
        address.setCity("Bangalore");
        address.setState("KA");
        address.setCountry("IN");
        address.setContact(createContact(id));
        return address;
    }

    public static UserProfile createUserProfile(String id) {
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

    public static final String TEST_USER_PROFILE_ID_1 = "ID_1";
    public static final String TEST_USER_PROFILE_ID_2 = "ID_2";

}
