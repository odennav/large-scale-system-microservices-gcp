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

package com.ntw.oms.user.service;

import com.ntw.oms.user.dao.UserProfileDao;
import com.ntw.oms.user.dao.UserProfileDaoFactory;
import com.ntw.oms.user.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by anurag on 30/05/17.
 */
@Component
public class UserProfileServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserProfileDaoFactory userProfileDaoFactory;

    private UserProfileDao userProfileDaoBean;

    @Value("${database.type}")
    private String userProfileDBType;

    @PostConstruct
    public void postConstruct()
    {
        this.userProfileDaoBean = userProfileDaoFactory.getUserProfileDao("UserProfile"+userProfileDBType);
    }

    public void setUserProfileDaoBean(UserProfileDao userProfileDaoBean) {
        this.userProfileDaoBean = userProfileDaoBean;
    }

    public UserProfileDao getUserProfileDaoBean() {
        return userProfileDaoBean;
    }

    public UserProfile getUserProfile(String id) {
        return userProfileDaoBean.getUserProfile(id);
    }

    public boolean saveUserProfile(UserProfile userProfile) {
        return userProfileDaoBean.createUserProfile(userProfile);
    }

    public boolean removeUserProfile(String id) {
        return userProfileDaoBean.removeUserProfile(id);
    }

    public boolean removeUserProfiles() {
        return getUserProfileDaoBean().removeUserProfiles();
    }
}
