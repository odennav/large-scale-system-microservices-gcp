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

package com.ntw.auth.db.cassandra;

import com.ntw.auth.core.DBAuthMgr;
import com.ntw.common.entity.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 15/05/17.
 */
@Component("CQL")
public class CassandraAuthMgr extends DBAuthMgr {

    private static Logger logger = LoggerFactory.getLogger(CassandraAuthMgr.class);

    @Autowired(required = false)
    private CassandraOperations cassandraOperations;

    @Autowired(required = false)
    private CqlTemplate cqlTemplate;

    public CassandraOperations getCassandraOperations() {
        return cassandraOperations;
    }

    public void setCassandraOperations(CassandraOperations cassandraOperations) {
        this.cassandraOperations = cassandraOperations;
    }

    public CqlTemplate getCqlTemplate() {
        return cqlTemplate;
    }

    public void setCqlTemplate(CqlTemplate cqlTemplate) {
        this.cqlTemplate = cqlTemplate;
    }

    @Override
    public List<String> getUserRole(String userId) {
        UserAuth userAuth = getUserAuth(userId);
        return (userAuth == null) ? new LinkedList<>() : userAuth.getRoles();
    }

    @Override
    public UserAuth getUserAuth(String userId) {
        String cqlOne = "select * from UserAuth where id = '"+userId+"'";
        CassandraUserAuth dbUserAuth;
        try {
            dbUserAuth = getCassandraOperations().selectOne(cqlOne, CassandraUserAuth.class);
        } catch (Exception e) {
            logger.error("Exception fetching user auth for id={}", userId);
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbUserAuth == null) {
            logger.info("User auth with id {} not found", userId);
            return null;
        }
        logger.info("Found user; context={}", dbUserAuth.toString());
        return dbUserAuth.getUserAuth();
    }

    @Override
    public List<UserAuth> getAllUserAuth() {
        String selectCql = "select * from UserAuth";
        List<UserAuth> userAuthList;
        try {
            userAuthList = getCassandraOperations().select(selectCql, UserAuth.class);
        } catch (Exception e) {
            logger.error("Exception getting all users auth");
            return null;
        }
        if (userAuthList.isEmpty()) {
            logger.debug("No userAuth found; query={}", selectCql);
            return null;
        }
        logger.debug("Fetched userAuth; number of records={}", userAuthList.size());
        return userAuthList;
    }

    @Override
    public UserAuth createUser(UserAuth userAuth, String hashedPassword) {
        CassandraUserAuth dbUserAuth = new CassandraUserAuth(userAuth);
        dbUserAuth.setPassword(hashedPassword);
        CassandraUserAuth userAuthRet;
        try {
            userAuthRet = getCassandraOperations().insert(dbUserAuth);
        } catch (Exception e) {
            logger.error("Exception creating user; context={}", userAuth);
            logger.error("Exception message: ", e);
            return null;
        }
        if (userAuthRet != null && userAuthRet.getId().equals(userAuth.getId())) {
            logger.debug("Created user auth; context={}", userAuthRet);
            return userAuthRet.getUserAuth();
        }
        logger.error("Unable to new user; context={}", userAuth);
        return null;
    }

    @Override
    public boolean deleteUsers() {
        String cql = "truncate UserAuth";
        try {
            if (getCqlTemplate().execute(cql)) {
                return true;
            }
        } catch (Exception e) {
            logger.error("Exception deleting users auth", e);
        }
        return false;
    }

}
