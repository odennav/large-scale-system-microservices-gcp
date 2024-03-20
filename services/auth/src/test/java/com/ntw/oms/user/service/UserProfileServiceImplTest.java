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

import com.ntw.oms.user.config.TestConfig;
import com.ntw.oms.user.dao.UserProfileMockDao;
import com.ntw.oms.user.entity.UserProfile;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Created by anurag on 12/05/17.
 */
public class UserProfileServiceImplTest extends TestCase {

    private UserProfileServiceImpl userProfileServiceBean;

    public UserProfileServiceImplTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( UserProfileServiceImplTest.class );
    }

    public void setUp() throws Exception {
        super.setUp();
        userProfileServiceBean = new UserProfileServiceImpl();
        userProfileServiceBean.setUserProfileDaoBean(new UserProfileMockDao());
    }

    public void testDummy() {
        Assert.assertEquals(true, true);
    }

    public void testCreateUserProfile() {
        UserProfile up = TestConfig.createUserProfile(TestConfig.TEST_USER_PROFILE_ID_1);
        boolean success = userProfileServiceBean.saveUserProfile(up);
        Assert.assertEquals(true, success);
    }

    public void testFetchUserProfile() {
        // Fetch userProfile
        UserProfile up = TestConfig.createUserProfile(TestConfig.TEST_USER_PROFILE_ID_1);
        UserProfile userProfile = userProfileServiceBean.getUserProfile(TestConfig.TEST_USER_PROFILE_ID_1);
        Assert.assertNotNull(userProfile);
        Assert.assertEquals(up.getId(), userProfile.getId());
        Assert.assertEquals(up.getShippingAddresses().size(),
                            userProfile.getShippingAddresses().size());
        Assert.assertEquals(up.getShippingAddresses().get(0).getStreet(),
                userProfile.getShippingAddresses().get(0).getStreet());
        Assert.assertEquals(up.getAddress().getContact().getName(),
                userProfile.getAddress().getContact().getName());
    }

    public void testRemoveUserProfile() {
        // Remove userProfile
        boolean success = userProfileServiceBean.removeUserProfile(TestConfig.TEST_USER_PROFILE_ID_1);
        Assert.assertTrue(success);
    }

    ///////// Do NOT Uncomment ///////////
//    public void testCreateUserProfiles() {
//        boolean success = userProfileServiceBean.saveUserProfile(TestConfig.createUserProfile("anurag"));
//        Assert.assertEquals(true, success);
//        success = userProfileServiceBean.saveUserProfile(TestConfig.createUserProfile("john"));
//        Assert.assertEquals(true, success);
//        success = userProfileServiceBean.saveUserProfile(TestConfig.createUserProfile("bob"));
//        Assert.assertEquals(true, success);
//    }

}
