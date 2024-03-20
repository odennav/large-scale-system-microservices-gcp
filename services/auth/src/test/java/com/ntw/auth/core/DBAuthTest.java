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

/**
 * Created by anurag on 28/03/17.
 */
import com.ntw.auth.config.TestConfig;
import com.ntw.auth.mock.MockAuth;
import com.ntw.common.entity.Role;
import com.ntw.common.entity.UserAuth;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.List;

/**
 * Unit test for simple App.
 */
public class DBAuthTest extends TestCase
{
    private AuthMgr authBean;

    public void setUp() throws Exception {
        super.setUp();
        authBean = new MockAuth();
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DBAuthTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DBAuthTest.class );
    }

    /**
     * Test User Authentication
     */
    public void testUserCreation() {
        authBean.createUser(TestConfig.Test_User_Auth);
        authBean.createUser(TestConfig.Test_Admin_Auth);
    }

    /**
     * Test User Authentication
     */
    public void testUserAuthentication()
    {
        boolean pass = authBean.authenticate(TestConfig.Test_Admin_Auth.getId(),
                                            TestConfig.Test_Admin_Auth.getPassword());
        Assert.assertEquals(true, pass);
        pass = authBean.authenticate("WrongId",TestConfig.Test_Admin_Auth.getPassword());
        Assert.assertEquals(false, pass);
        pass = authBean.authenticate(TestConfig.Test_Admin_Auth.getId(),"WrongPassword");
        Assert.assertEquals(false, pass);
    }

    /**
     * Test User Details
     */
    public void testUserDetails()
    {
        UserAuth userAuth = authBean.getUserAuth(TestConfig.Test_Admin_Auth.getId());
        Assert.assertNotNull(userAuth);
        Assert.assertEquals(TestConfig.Test_Admin_Auth.getId(), userAuth.getId());
        Assert.assertEquals(TestConfig.Test_Admin_Auth.getName(), userAuth.getName());
        System.out.println("Stored password for "+userAuth.getId()+" is "+userAuth.getPassword());
        Assert.assertEquals(TestConfig.Test_Admin_Auth.getEmailId(), userAuth.getEmailId());
    }

    /**
     * Test User Role
     */
    public void testUserRole()
    {
        List<String> userRoles = authBean.getUserRole(TestConfig.Test_Admin_Auth.getId());
        Assert.assertEquals(2, userRoles.size());
        System.out.println(userRoles.get(0));
        Assert.assertEquals(Role.ADMIN.toString(), userRoles.get(0));
        Assert.assertEquals(Role.USER.toString(), userRoles.get(1));

        userRoles = authBean.getUserRole(TestConfig.Test_User_Auth.getId());
        Assert.assertEquals(1, userRoles.size());
        Assert.assertEquals(Role.USER.toString(), userRoles.get(0));
    }

}

