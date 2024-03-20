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

package com.ntw.auth.mock;

import com.ntw.auth.config.TestConfig;
import com.ntw.auth.core.AuthMgr;
import com.ntw.common.entity.UserAuth;

import java.util.List;

/**
 * Created by anurag on 16/05/17.
 */
public class MockAuth implements AuthMgr {
    @Override
    public boolean createUser(UserAuth userAuth) {
        if (userAuth.getId().equals(TestConfig.Test_Admin_Auth.getId())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean authenticate(String userId, String password) {
        if (userId.equals(TestConfig.Test_Admin_Auth.getId())
                && password.equals(TestConfig.Test_Admin_Auth.getPassword())) {
            return true;
        }
        return false;
    }

    @Override
    public UserAuth getUserAuth(String userId) {
        return TestConfig.Test_Admin_Auth;
    }

    @Override
    public List<String> getUserRole(String userId) {
        if (userId.equals(TestConfig.Test_Admin_Auth.getId())) {
            return TestConfig.Test_Admin_Auth.getRoles();
        }
        return TestConfig.Test_User_Auth.getRoles();
    }

    @Override
    public boolean deleteUsers() {
        return false;
    }

    @Override
    public List<UserAuth> getAllUserAuth() {
        return null;
    }
}
