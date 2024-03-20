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

package com.ntw.auth.db.sql;

import com.ntw.common.entity.UserAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 15/05/17.
 */
@Component("SQL")
public class SQLAuthMgr extends com.ntw.auth.core.DBAuthMgr {

    private static Logger logger = LoggerFactory.getLogger(SQLAuthMgr.class);

    @Autowired(required = false)
    JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static final String GET_USER_ROLE_SQL = "select role from UserRole where id = ?";

    @Override
    public List<String> getUserRole(String userId) {
        List<String> roles;
        try {
            roles = jdbcTemplate.queryForList(GET_USER_ROLE_SQL, new Object[]{userId}, String.class);
        } catch (Exception e) {
            logger.error("Exception fetching user role for id {}", userId);
            logger.error("Exception message: ", e);
            return null;
        }
        if (roles.isEmpty()) {
            logger.info("No roles found for user id {}", userId);
            return null;
        }
        logger.debug("Fetched user roles; userid={}, context={}", userId, roles.toArray());
        return roles;
    }

    private static final String GET_USER_AUTH_SQL = "select * from UserAuth where id = ?";

    @Override
    public UserAuth getUserAuth(String userId) {
        UserAuth userAuth;
        try {
            userAuth = jdbcTemplate.queryForObject(GET_USER_AUTH_SQL, new Object[]{userId},
                    new BeanPropertyRowMapper<UserAuth>(UserAuth.class));
        } catch (Exception e) {
            logger.error("Exception fetching user auth for user id {}", userId);
            logger.error("Exception message: ", e);
            return null;
        }
        if (userAuth == null) {
            logger.error("No user auth record found for user id {}", userAuth);
        }
        logger.debug("Found user; context={}", userAuth.toString());
        List<String> roles = getUserRole(userId);
        if (roles != null) {
            userAuth.setRoles(roles);
        }
        return userAuth;
    }

    private static final String GET_USERS_ROLE_SQL = "select * from UserRole";

    private List<UserRole> getAllUsersRole() {
        List<UserRole> userRoles;
        try {
            userRoles = jdbcTemplate.query(GET_USERS_ROLE_SQL,
                    new BeanPropertyRowMapper<>(UserRole.class));
        } catch (Exception e) {
            logger.error("Exception fetching users role records", e);
            return null;
        }
        if (userRoles.isEmpty()) {
            logger.info("No users role records found");
            return null;
        }
        logger.debug("Fetched {} userRoleList", userRoles.size());
        logger.debug("Fetched userAuthList; context={}", userRoles);
        return userRoles;
    }

    private static final String GET_USERS_AUTH_SQL = "select * from UserAuth";

    @Override
    public List<UserAuth> getAllUserAuth() {
        List<UserAuth> userAuthList;
        try {
            userAuthList = jdbcTemplate.query(GET_USERS_AUTH_SQL,
                    new BeanPropertyRowMapper<>(UserAuth.class));
        } catch (Exception e) {
            logger.error("Exception fetching users auth records", e);
            return null;
        }
        if (userAuthList.isEmpty()) {
            logger.info("No user auth records found");
            return null;
        }
        logger.debug("Fetched {} userAuthList", userAuthList.size());
        logger.debug("Fetched userAuthList; context={}", userAuthList);

        List<UserRole> userRoles = getAllUsersRole();
        Map<String, UserAuth> userAuthMap = new HashMap<>(1000);
        for (UserAuth userAuth : userAuthList) {
            userAuthMap.put(userAuth.getId(), userAuth);
        }
        for (UserRole userRole : userRoles) {
            UserAuth userAuth = userAuthMap.get(userRole.getId());
            if (userAuth == null) {
                logger.error("No user auth found for user {} present in user role table", userRole);
                continue;
            }
            userAuth.getRoles().add(userRole.getRole());
        }
        return userAuthList;
    }

    private static final String INSERT_USER_AUTH_SQL =
            "insert into UserAuth (id, emailId, name, password) values (?,?,?,?)";

    @Override
    public UserAuth createUser(UserAuth userAuth, String hashedPassword) {
        int updateStatus;
        try {
            updateStatus = jdbcTemplate.update(INSERT_USER_AUTH_SQL, new Object[]{
                    userAuth.getId(), userAuth.getEmailId(), userAuth.getName(), hashedPassword
            });
        } catch (Exception e) {
            logger.error("Exception creating user auth; context={}", userAuth);
            logger.error("Exception message: ", e);
            return null;
        }
        if (updateStatus <= 0) {
            logger.error("Insert failed : context={}", userAuth);
        }
        List<String> roles = userAuth.getRoles();
        String sqlUserRole = "insert into UserRole (id, role) values (?,?)";
        int [] updateStatusArr;
        try {
            updateStatusArr = jdbcTemplate.batchUpdate(sqlUserRole, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    ps.setString(1, userAuth.getId());
                    ps.setString(2, roles.get(i));
                }

                @Override
                public int getBatchSize() {
                    return roles.size();
                }
            });
        } catch (Exception e) {
            logger.error("Exception creating user auth; context={}", userAuth);
            logger.error("Exception message: ", e);
            return userAuth;
        }
        logger.debug("Insert user role status: userAuth={}, roles={}, updateStatus={}",
                userAuth, roles.toArray(), updateStatusArr);
        return userAuth;
    }

    private static final String DELETE_USERS_AUTH_SQL = "delete from UserAuth";
    private static final String DELETE_USERS_ROLE_SQL = "delete from UserRole";

    @Override
    public boolean deleteUsers() {
        boolean success = true;
        try {
            getJdbcTemplate().execute(DELETE_USERS_ROLE_SQL);
        } catch (Exception e) {
            logger.error("exception deleting user roles", e);
            success = false;
        }
        try {
            getJdbcTemplate().execute(DELETE_USERS_AUTH_SQL);
        } catch (Exception e) {
            logger.error("exception deleting user roles", e);
            success = false;
        }
        return success;
    }

}
