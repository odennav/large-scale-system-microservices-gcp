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

package com.ntw.auth.core;

import com.ntw.common.entity.UserAuth;

import java.util.List;

/**
 * Created by anurag on 23/03/17.
 */
public interface AuthMgr {

    public boolean createUser(UserAuth userAuth);

    public boolean authenticate(String userId, String password);

    public UserAuth getUserAuth(String userId);

    public List<UserAuth> getAllUserAuth();

    public List<String> getUserRole(String userId);

    public boolean deleteUsers();
}