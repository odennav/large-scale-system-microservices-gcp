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

package com.ntw.oms.user.dao.cassandra;

import com.ntw.oms.user.dao.UserProfileDao;
import com.ntw.oms.user.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by anurag on 30/05/17.
 */
@Component("UserProfileCQL")
public class DBUserProfileDao implements UserProfileDao {

    private static final Logger logger = LoggerFactory.getLogger(DBUserProfileDao.class);

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
    public boolean createUserProfile(UserProfile userProfile) {
        DBUserProfile dbUserProfile = createDBUserProfile(userProfile);
        List<DBAddress> dbAddresses = createDBAddresses(userProfile);

        try {
            CassandraBatchOperations batchOpsProfile = getCassandraOperations().batchOps();
            batchOpsProfile.insert(dbUserProfile);
            logger.debug("Adding DBUserProfile; context={}", dbUserProfile);
            batchOpsProfile.execute();
        } catch(Exception e) {
            logger.error("Unable to add user profile data; context={}", dbUserProfile);
            logger.error("Exception while adding user profile data", e);
            return false;
        }

        CassandraBatchOperations batchOpsAddress = getCassandraOperations().batchOps();
        try {
            for (DBAddress dbAddress : dbAddresses) {
                batchOpsAddress.insert(dbAddress);
            }
            logger.debug("Adding DBAddress List; context={}", dbAddresses.toArray().toString());
            batchOpsAddress.execute();
        } catch(Exception e) {
            logger.error("Unable to add user address data; context={}", dbAddresses.toArray().toString());
            logger.error("Exception while adding user address data", e);
            return false;
        }

        logger.debug("Added UserProfile; context={}", userProfile);
        return true;
    }

    @Override
    public UserProfile getUserProfile(String id) {
        String addressCql = "select * from UserAddress where userProfileId='"+id+"'";
        List<DBAddress> dbAddresses;
        try {
            dbAddresses = getCassandraOperations().select(addressCql, DBAddress.class);
        } catch (Exception e) {
            logger.error("Exception fetching user address for user id={}", id);
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbAddresses.isEmpty()) {
            logger.error("No Addresses found for userId={}", id);
            return null;
        }

        String userProfileCql = "select * from UserProfile where id='"+id+"'";
        DBUserProfile dbUserProfile;
        try {
            dbUserProfile = getCassandraOperations().selectOne(userProfileCql, DBUserProfile.class);
        } catch (Exception e) {
            logger.error("Exception fetching user profile for user id={}", id);
            logger.error("Exception message: ", e);
            return null;
        }
        if (dbUserProfile == null) {
            logger.debug("No UserProfile found; userId={}", id);
            return null;
        }
        UserProfile userProfile = dbUserProfile.getUserProfile(dbAddresses);
        logger.debug("Fetched UserProfile; context={}", userProfile);
        return userProfile;
    }

    @Override
    public boolean modifyUserProfile(UserProfile userProfile) {
        createUserProfile(userProfile);
        logger.debug("Modified UserProfile; context={}", userProfile);
        return true;
    }

    @Override
    public boolean removeUserProfile(String id) {
        String deleteAddressesCql = "delete from UserAddress where userProfileId='"+id+"'";
        String deleteUserProfileCql = "delete from UserProfile where id='"+id+"'";
        try {
            getCassandraOperations().delete(deleteAddressesCql);
            logger.debug("Executed delete addresses; context={}", deleteAddressesCql);
        } catch (Exception e) {
            logger.error("Unable to delete user address records");
            logger.error("Exception message: ", e);
            return false;
        }
        try {
            getCassandraOperations().delete(deleteUserProfileCql);
            logger.debug("Executed delete userprofile; context={}", deleteUserProfileCql);
        } catch (Exception e) {
            logger.error("Unable to delete user profile records");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Deleted user profile for user {}", id);
        return true;
    }

    @Override
    public boolean removeUserProfiles() {
        String deleteUserProfilesCql = "truncate UserProfile";
        try {
            if (cqlTemplate.execute(deleteUserProfilesCql)) {
                logger.debug("UserProfile records deleted");
                return true;
            }
        } catch (Exception e) {
            logger.error("Unable to delete user profile records");
            logger.error("Exception message: ", e);
            return false;
        }
        String deleteAddressesCql = "truncate UserAddress";
        try {
            if (cqlTemplate.execute(deleteAddressesCql)) {
                logger.debug("Address records deleted");
                return true;
            }
        } catch (Exception e) {
            logger.error("Unable to delete address records");
            logger.error("Exception message: ", e);
            return false;
        }
        logger.debug("Deleted all user profile records");
        return true;
    }

    private static DBUserProfile createDBUserProfile(com.ntw.oms.user.entity.UserProfile userProfile) {
        DBUserProfile dbUserProfile = DBUserProfile.createDBUserProfile(userProfile);
        return dbUserProfile;
    }

    private static List<DBAddress> createDBAddresses(com.ntw.oms.user.entity.UserProfile userProfile) {
        List<DBAddress> dbAddresses = new LinkedList<>();
        int serialNum = 1;
        dbAddresses.add(DBAddress.createDBAddress(userProfile.getAddress(), userProfile.getId(), "bill", serialNum++));
        for (com.ntw.oms.user.entity.Address address : userProfile.getShippingAddresses()) {
            DBAddress dbShipAddress = DBAddress.createDBAddress(address, userProfile.getId(), "ship", serialNum++);
            dbAddresses.add(dbShipAddress);
        }
        return dbAddresses;
    }

    private com.ntw.oms.user.entity.UserProfile getUserProfile(DBUserProfile dbUserProfile, List<DBAddress> dbAddresses) {
        return dbUserProfile.getUserProfile(dbAddresses);
    }

}
