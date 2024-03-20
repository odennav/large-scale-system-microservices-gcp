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

import com.ntw.auth.crypt.PasswordCrypt;
import com.ntw.auth.crypt.PasswordCryptFactory;
import com.ntw.common.entity.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 15/05/17.
 */
public abstract class DBAuthMgr implements AuthMgr {

    private static Logger logger = LoggerFactory.getLogger(DBAuthMgr.class);

    @Value("${auth.password.plain:false}")
    private Boolean usePlainTextPassword;

    @Autowired
    private PasswordCryptFactory passwordCryptFactory;

    private PasswordCrypt passwordCrypt;

    public PasswordCrypt getPasswordCrypt() {
        return passwordCrypt;
    }

    public void setPasswordCrypt(PasswordCrypt passwordCrypt) {
        this.passwordCrypt = passwordCrypt;
    }

    @PostConstruct
    public void postConstruct()
    {
        passwordCrypt = passwordCryptFactory.getPasswordCrypt(usePlainTextPassword);
    }

    @Override
    public boolean createUser(UserAuth userAuth) {
        String hashedPassword = getPasswordCrypt().hashPassword(userAuth.getPassword());
        Object userAuthRet = createUser(userAuth, hashedPassword);
        return userAuthRet == null ? false : true;
    }

    @Override
    public boolean authenticate(String userId, String passwordParam) {
        UserAuth userAuth = getUserAuth(userId);
        if (userAuth == null) {
            logger.warn("User {} does not exist", userId);
            return false;
        }
        String dbPassword = userAuth.getPassword();
        if (dbPassword != null) {
            if (getPasswordCrypt().checkPassword(passwordParam, dbPassword)) {
                return true;
            }
            logger.warn("Incorrect password {} for user id {}", passwordParam, userId);
        }
        return false;
    }

    @Override
    public List<String> getUserRole(String userId) {
        UserAuth userAuth = getUserAuth(userId);
        return (userAuth == null) ? new LinkedList<>() : userAuth.getRoles();
    }

    @Override
    public abstract boolean deleteUsers();

    @Override
    public abstract UserAuth getUserAuth(String userId);

    @Override
    public abstract List<UserAuth> getAllUserAuth();

    protected abstract UserAuth createUser(UserAuth userAuth, String hashedPassword);
}
