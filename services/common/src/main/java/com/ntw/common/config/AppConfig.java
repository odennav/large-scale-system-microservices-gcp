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
 * Created by anurag on 09/07/18.
 */
public class AppConfig {

    public static final String STATUS_PATH = "/status";
    public static final String SERVICE_STATUS_PATH = "/status/services";
    public static final String DB_STATUS_PATH = "/status/databases";
    public static final String SYS_PROPERTY_PATH = "/property/{name}";

    public static final String ADMIN_RESOURCE_PATH = "/admin";

    public static final String AUTHORIZATION_RESOURCE_PATH = "/auth";
    public static final String AUTH_TOKEN_PATH = "/token";
    public static final String AUTH_TOKEN_USER_PATH = "/token/user";
    public static final String USERS_AUTH_PATH = "/users";
    public static final String USER_AUTH_PATH = "/users/{id}";

    public static final String PRODUCTS_RESOURCE_PATH = "/products";
    public static final String PRODUCT_PATH = "/{id}";

    public static final String CARTS_RESOURCE_PATH = "/carts";
    public static final String CART_PATH = "/{id}";

    public static final String ORDERS_RESOURCE_PATH = "/orders";
    public static final String ORDER_PATH = "/{id}";
    public static final String ORDER_CART_PATH = "/order/carts/{cartId}";

    public static final String USERS_PROFILE_RESOURCE_PATH = "/users-profile";
    public static final String USER_PROFILE_PATH = "/{id}";

    public static final String INVENTORY_RESOURCE_PATH = "/inventory";
    public static final String INVENTORY_PATH = "/{productId}";
    public static final String INVENTORY_RESERVATION_PATH = "/reservation";

}
