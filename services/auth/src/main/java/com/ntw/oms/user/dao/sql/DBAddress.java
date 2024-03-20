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

package com.ntw.oms.user.dao.sql;

import com.ntw.oms.user.entity.Address;
import com.ntw.oms.user.entity.Contact;

/**
 * Created by anurag on 30/05/17.
 */
public class DBAddress {

    private String userProfileId;
    private String type;
    private int id;

    // Address
    private String street;
    private String area;
    private String city;
    private String state;
    private String country;
    private String contactName;
    private String contactTelephone;
    private String contactEmail;

    public String getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(String userProfileId) {
        this.userProfileId = userProfileId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactTelephone() {
        return contactTelephone;
    }

    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    @Override
    public String toString() {
        return "{" +
                "\"userProfileId\":" + (userProfileId == null ? "null" : "\"" + userProfileId + "\"") + ", " +
                "\"type\":" + (type == null ? "null" : "\"" + type + "\"") + ", " +
                "\"id\":\"" + id + "\"" + ", " +
                "\"street\":" + (street == null ? "null" : "\"" + street + "\"") + ", " +
                "\"area\":" + (area == null ? "null" : "\"" + area + "\"") + ", " +
                "\"city\":" + (city == null ? "null" : "\"" + city + "\"") + ", " +
                "\"state\":" + (state == null ? "null" : "\"" + state + "\"") + ", " +
                "\"country\":" + (country == null ? "null" : "\"" + country + "\"") + ", " +
                "\"contactName\":" + (contactName == null ? "null" : "\"" + contactName + "\"") + ", " +
                "\"contactTelephone\":" + (contactTelephone == null ? "null" : "\"" + contactTelephone + "\"") + ", " +
                "\"contactEmail\":" + (contactEmail == null ? "null" : "\"" + contactEmail + "\"") +
                "}";
    }

    public static DBAddress createDBAddress(Address address, String userProfileId,
                                            String type, int serialNum) {
        DBAddress dbAddress = new DBAddress();
        dbAddress.setUserProfileId(userProfileId);
        dbAddress.setType(type);
        dbAddress.setId(serialNum);
        dbAddress.setStreet(address.getStreet());
        dbAddress.setCity(address.getCity());
        dbAddress.setArea(address.getArea());
        dbAddress.setState(address.getState());
        dbAddress.setCountry(address.getCountry());
        dbAddress.setContactName(address.getContact().getName());
        dbAddress.setContactEmail(address.getContact().getEmail());
        dbAddress.setContactTelephone(address.getContact().getTelephone());

        return dbAddress;
    }

    public Address getAddress() {
        Address address = new Address();
        address.setStreet(getStreet());
        address.setArea(getArea());
        address.setCity(getCity());
        address.setState(getState());
        address.setCountry(getCountry());
        Contact contact = new Contact();
        contact.setName(getContactName());
        contact.setEmail(getContactEmail());
        contact.setTelephone(getContactTelephone());
        address.setContact(contact);
        return address;
    }

}
