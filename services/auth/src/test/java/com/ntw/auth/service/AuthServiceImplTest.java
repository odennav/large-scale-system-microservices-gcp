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

package com.ntw.auth.service;

/**
 * Created by anurag on 28/03/17.
 */
import com.ntw.auth.config.TestConfig;
import com.ntw.auth.core.AuthMgr;
import com.ntw.auth.entity.OAuthToken;
import com.ntw.auth.mock.MockAuth;
import com.ntw.auth.mock.MockJwtUtility;
import com.ntw.common.entity.UserAuth;
import com.ntw.common.security.JwtUtility;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AuthServiceImplTest extends TestCase
{
    private AuthMgr authBean;
    private JwtUtility jwtUtilBean;
    private AuthServiceImpl authService;

    public void setUp() throws Exception {
        super.setUp();
        authBean = new MockAuth();
        jwtUtilBean = new MockJwtUtility();
        authService = new AuthServiceImpl();
        authService.setAuthBean(authBean);
        authService.setJwtUtilBean(jwtUtilBean);
    }

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AuthServiceImplTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AuthServiceImplTest.class );
    }

    public void testDummy() {
        Assert.assertEquals(true, true);
    }

    /**
     * Test User Authentication
     */
    public void testGetAuthToken() {
        authService.createUserAuth(TestConfig.Test_Admin_Auth);
        OAuthToken authToken = authService.getOAuthToken(TestConfig.Test_Admin_Auth.getId(),
                TestConfig.Test_Admin_Auth.getPassword());
        Assert.assertNotNull(authToken);
        Assert.assertEquals(authToken.getAccess_token(),TestConfig.DUMMY_TOKEN);
    }

    public void testGetUserAuth() {
        UserAuth userAuth = authService.getUserAuth(TestConfig.DUMMY_TOKEN);
        Assert.assertNotNull(userAuth);
        Assert.assertEquals(userAuth.getId(), TestConfig.Test_Admin_Auth.getId());
    }

}

