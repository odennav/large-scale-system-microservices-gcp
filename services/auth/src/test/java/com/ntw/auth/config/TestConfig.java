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

package com.ntw.auth.config;

import com.ntw.common.entity.UserAuth;

/**
 * Created by anurag on 16/05/17.
 */
public class TestConfig {
    private static UserAuth createAdminUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId("anurag");
        userAuth.setPassword("password");
        userAuth.setName("Anurag");
        userAuth.getRoles().add("Admin");
        userAuth.getRoles().add("User");
        userAuth.setEmailId("anurag.yadav@newtechways.com");
        return userAuth;
    }

    private static UserAuth createUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId("john");
        userAuth.setPassword("password");
        userAuth.setName("John");
        userAuth.getRoles().add("User");
        userAuth.setEmailId("john.doe@newtechways.com");
        return userAuth;
    }

    public static final UserAuth Test_Admin_Auth = createAdminUserAuth();
    public static final UserAuth Test_User_Auth = createUserAuth();

    public static final String DUMMY_TOKEN = "DUMMY_TOKEN";

}
