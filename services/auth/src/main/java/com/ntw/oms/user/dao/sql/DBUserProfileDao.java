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

import com.ntw.oms.user.dao.UserProfileDao;
import com.ntw.oms.user.entity.Address;
import com.ntw.oms.user.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 30/05/17.
 */
@Component("UserProfileSQL")
public class DBUserProfileDao implements UserProfileDao {

    private static final Logger logger = LoggerFactory.getLogger(DBUserProfileDao.class);

    @Autowired(required = false)
    JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    private static final String USER_PROFILE_INSERT_SQL = "insert into UserProfile (id, name, email) values (?,?,?)";
    private static final String ADDRESS_INSERT_SQL = "insert into UserAddress (userprofileid, type, id, street, area, " +
            "city, state, country, contactname, contactemail, contacttelephone) values (?,?,?,?,?,?,?,?,?,?,?)";

    @Override
    public boolean createUserProfile(UserProfile userProfile) {
        int retUserProfile;
        try {
            retUserProfile = jdbcTemplate.update(USER_PROFILE_INSERT_SQL, new Object[]{
                    userProfile.getId(), userProfile.getName(), userProfile.getEmail()
            });
        } catch (Exception e) {
            logger.error("Unable to create user profile record; context={}", userProfile);
            logger.error("Exception message: ", e);
            return false;
        }
        if (retUserProfile <= 0) {
            logger.error("Unable to add DBUserProfile; context={}", userProfile);
            return false;
        }
        logger.debug("Added UserProfile; context={}", userProfile);

        List<DBAddress> addresses = createDBAddresses(userProfile);
        int [] updateStatusArr;
        try {
            updateStatusArr = jdbcTemplate.batchUpdate(ADDRESS_INSERT_SQL, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i)
                        throws SQLException {
                    ps.setString(1, addresses.get(i).getUserProfileId());
                    ps.setString(2, addresses.get(i).getType());
                    ps.setInt(3, addresses.get(i).getId());
                    ps.setString(4, addresses.get(i).getStreet());
                    ps.setString(5, addresses.get(i).getArea());
                    ps.setString(6, addresses.get(i).getCity());
                    ps.setString(7, addresses.get(i).getState());
                    ps.setString(8, addresses.get(i).getCountry());
                    ps.setString(9, addresses.get(i).getContactName());
                    ps.setString(10, addresses.get(i).getContactEmail());
                    ps.setString(11, addresses.get(i).getContactTelephone());
                }

                @Override
                public int getBatchSize() {
                    return addresses.size();
                }
            });
        } catch (Exception e) {
            logger.error("Exception creating user addresses; context={}", userProfile);
            logger.error("Exception message: ", e);
            return false;
        }
        if (updateStatusArr.length == 0) {
            logger.error("Unable to add DBAddress List; sql={}, status={}, context={}",
                    ADDRESS_INSERT_SQL, updateStatusArr, userProfile);
            return false;
        }
        logger.debug("Added DBAddress List; sql={}, status={}, context={}",
                ADDRESS_INSERT_SQL, updateStatusArr, addresses.toArray());

        return true;
    }

    private static final String GET_USER_ADDRESS_SQL = "select * from UserAddress where userProfileId=?";
    private static final String GET_USER_PROFILE_SQL = "select * from UserProfile where id=?";

    @Override
    public UserProfile getUserProfile(String id) {
        List<DBAddress> dbAddresses;
        try {
            dbAddresses = jdbcTemplate.query(GET_USER_ADDRESS_SQL,
                    new Object[]{id}, new BeanPropertyRowMapper<>(DBAddress.class));
        } catch (Exception e) {
            logger.error("Exception while fetching user address record");
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbAddresses.isEmpty()) {
            logger.debug("No Addresses found; userId={}", id);
        }

        UserProfile userProfile;
        try {
            userProfile = jdbcTemplate.queryForObject(GET_USER_PROFILE_SQL, new Object[]{id},
                    new BeanPropertyRowMapper<>(UserProfile.class));
        } catch (Exception e) {
            logger.warn("No UserProfile found; userId={}", id);
            logger.warn("Exception message: ", e);
            return null;
        }
        userProfile = setUserProfileAddresses(userProfile, dbAddresses);
        logger.debug("Fetched UserProfile; context={}", userProfile);
        return userProfile;
    }

    @Override
    public boolean modifyUserProfile(UserProfile userProfile) {
        if (!removeUserProfile(userProfile.getId())) {
            return false;
        }
        if (!createUserProfile(userProfile)) {
            return false;
        }
        logger.debug("Modified UserProfile; context={}", userProfile);
        return true;
    }

    private static final String DELETE_USER_ADDRESS_SQL = "delete from UserAddress where userProfileId=?";
    private static final String DELETE_USER_PROFILE_SQL = "delete from UserProfile where id=?";

    @Override
    public boolean removeUserProfile(String id) {
        try {
            jdbcTemplate.update(DELETE_USER_ADDRESS_SQL, new Object[]{id});
        } catch (Exception e) {
            logger.error("Unable to delete user address for user {}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Executed delete addresses; userId={}", id);

        try {
            jdbcTemplate.update(DELETE_USER_PROFILE_SQL, new Object[]{id});
        } catch (Exception e) {
            logger.error("Unable to delete user profile for user {}", id);
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Executed delete userprofile; userId={}", id);

        return true;
    }

    private static final String DELETE_USERS_ADDRESS_SQL = "delete from UserAddress";
    private static final String DELETE_USERS_PROFILE_SQL = "delete from UserProfile";

    @Override
    public boolean removeUserProfiles() {
        try {
            jdbcTemplate.update(DELETE_USERS_ADDRESS_SQL);
        } catch (Exception e) {
            logger.error("Unable to delete user address records");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Executed delete addresses for all users");

        try {
            jdbcTemplate.update(DELETE_USERS_PROFILE_SQL);
        } catch (Exception e) {
            logger.error("Unable to delete user profile for all users");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Executed delete userprofile for all users");

        return true;
    }

    private static List<DBAddress> createDBAddresses(UserProfile userProfile) {
        List<DBAddress> dbAddresses = new LinkedList<>();
        int serialNum = 1;
        dbAddresses.add(DBAddress.createDBAddress(userProfile.getAddress(), userProfile.getId(), "bill", serialNum++));
        for (com.ntw.oms.user.entity.Address address : userProfile.getShippingAddresses()) {
            DBAddress dbShipAddress = DBAddress.createDBAddress(address, userProfile.getId(), "ship", serialNum++);
            dbAddresses.add(dbShipAddress);
        }
        return dbAddresses;
    }

    private UserProfile setUserProfileAddresses(UserProfile userProfile, List<DBAddress> dbAddresses) {
        Address contactAddress = null;
        List<com.ntw.oms.user.entity.Address> shippingAddresses = new LinkedList<>();

        for (DBAddress dbAddress : dbAddresses) {
            if (dbAddress.getType().equals("bill")) {
                contactAddress = dbAddress.getAddress();
            } else if (dbAddress.getType().equals("ship")) {
                shippingAddresses.add(dbAddress.getAddress());
            }
        }
        userProfile.setContact(contactAddress.getContact());
        userProfile.setAddress(contactAddress);
        userProfile.setShippingAddresses(shippingAddresses);
        return userProfile;
    }

}
