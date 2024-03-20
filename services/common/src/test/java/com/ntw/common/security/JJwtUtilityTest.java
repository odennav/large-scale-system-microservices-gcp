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

package com.ntw.common.security;


import com.ntw.common.entity.UserAuth;
import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class JJwtUtilityTest
        extends TestCase
{
    public JJwtUtilityTest(String testName )
    {
        super( testName );
    }

    public static Test suite()
    {
        return new TestSuite( JJwtUtilityTest.class );
    }

    private UserAuth createUserAuth() {
        UserAuth userAuth = new UserAuth();
        userAuth.setId("anurag");
        userAuth.setPassword("password");
        userAuth.setName("Anurag Yadav");
        userAuth.getRoles().add("Admin");
        userAuth.getRoles().add("User");
        userAuth.setEmailId("anurag.yadav@newtechways.com");
        return userAuth;
    }

    private UserAuth userAuth;
    private String testToken;

    public void setUp() throws Exception {
        super.setUp();
        userAuth = createUserAuth();
        testToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBbnVyYWcgWWFkYXYiLCJpZCI6ImFudXJhZyIsIn" +
                "JvbGUiOiJBZG1pbiBVc2VyICIsImVtYWlsSWQiOiJhbnVyYWcueWFkYXZAbmV3dGVjaHdheXMuY" +
                "29tIn0.ZZJDeH3rRkOhnnu91k3bd0O3Qog9BHX1DTwf9Oboslr75UuhMj7hob0WvXJT3cMqRnnesv0bkUVExosrMigmPA";
    }

    public void testGenerateToken() {
        JJwtUtility jwt = new JJwtUtility();
        String token = jwt.generateToken(userAuth);
        System.out.println(token);
        Assert.assertEquals(testToken, token);
    }

    public void testParseToken() {
        JJwtUtility jwt = new JJwtUtility();
        UserAuth parsedUserAuth = jwt.parseToken(testToken);
        Assert.assertNotNull(parsedUserAuth);
        Assert.assertEquals(parsedUserAuth.getId(), userAuth.getId());
        Assert.assertEquals(parsedUserAuth.getName(), userAuth.getName());
        Assert.assertEquals(parsedUserAuth.getEmailId(), userAuth.getEmailId());
        Assert.assertEquals(parsedUserAuth.getRoles().get(0), userAuth.getRoles().get(0));
        Assert.assertEquals(parsedUserAuth.getRoles().get(1), userAuth.getRoles().get(1));
    }
}

