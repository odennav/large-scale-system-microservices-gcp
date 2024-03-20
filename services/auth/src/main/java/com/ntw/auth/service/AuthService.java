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

import com.ntw.auth.entity.OAuthToken;
import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.entity.UserAuth;

import com.ntw.common.security.Secured;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by anurag on 17/03/17.
 */

/**
 * AuthService provides rest interface for authentication and authorization
 * This class can be configured to use LDAP or Database as the user auth storage
 * It only supports OAuth2 grant type of 'Password Credentials'
 */
@RestController
@RequestMapping(AppConfig.AUTHORIZATION_RESOURCE_PATH)
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthServiceImpl authServiceBean;

    public AuthServiceImpl getAuthServiceBean() {
        return authServiceBean;
    }

    /**
     * This method is used for generating auth token by posting user cred as json
     * This method is open to all roles
     * @param userAuth      json representation of UserAuth object
     * @return              auth token in http response body
     */
    @PostMapping(path= AppConfig.AUTH_TOKEN_PATH, consumes=MediaType.APPLICATION_JSON_VALUE,
                                                    produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postUserCred(HttpServletRequest httpServletRequest,
                                       @RequestBody UserAuth userAuth) {
        logger.info("Received user authentication; context={}", userAuth);

        if (!authenticateClient(httpServletRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Client Credentials");
        }

        String userId = userAuth.getId();
        String password = userAuth.getPassword();

        OAuthToken authToken = getAuthServiceBean().getOAuthToken(userId, password);

        return createTokenResponse(userAuth.getId(), authToken);
    }

    /**
     * This method is used for generating auth token by posting user cred as form parameters
     * This method is open to all roles
     * @param userId        user id as form parameter
     * @param password      user password as form parameter
     * @return              access token as http response
     */
    @PostMapping(path= AppConfig.AUTH_TOKEN_PATH, consumes= MediaType.APPLICATION_FORM_URLENCODED_VALUE,
                                                    produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity postUserAuth(HttpServletRequest httpServletRequest,
                                                   @RequestParam("username") String userId,
                                                   @RequestParam("password") String password) {
        logger.info("Received user authentication request; userId={}" + userId);

        if (!authenticateClient(httpServletRequest)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Client Credentials");
        }

        OAuthToken authToken = getAuthServiceBean().getOAuthToken(userId, password);

        return createTokenResponse(userId, authToken);
    }

    /**
     * This method decrypts and returns user auth information provided in user auth token
     * This method is open to all roles
     * @param accessToken       user access token
     * @return                  user auth object with user authentication details
     */
    @GetMapping(path= AppConfig.AUTH_TOKEN_USER_PATH, produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserAuth> getUserCred(@RequestParam("access_token") String accessToken) {
        if (accessToken == null || accessToken.equals("")) {
            logger.warn("No access token provided");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        logger.info("Received user profile request;  token={}", accessToken);

        UserAuth userAuth = getAuthServiceBean().getUserAuth(accessToken);

        logger.info("Fetched user profile for user; context={}", userAuth);
        return ResponseEntity.ok().
                header("Pragma","no-cache").
                body(userAuth);
    }

    /**
     * This method can only be called by admin to show all user auth records.
     * @return      list of user auth records
     */
    @Secured({Role.ADMIN})
    @GetMapping(path= AppConfig.USERS_AUTH_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserAuth>> getUserAuthList() {
        List<UserAuth> userAuthList = getAuthServiceBean().getUserAuthList();
        if (userAuthList == null) {
            logger.info("Could not get userAuthList");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        logger.info("Get userAuthList; size={}", userAuthList.size());
        return ResponseEntity.ok().body(userAuthList);
    }


    /**
     * This method can only be called by admin to insert a new user auth record
     * @param userAuth       user id as form parameter
     * @return               user auth object with user authentication details
     */
    @Secured({Role.ADMIN})
    @PutMapping(path= AppConfig.USER_AUTH_PATH, consumes= MediaType.APPLICATION_JSON_VALUE,
                                                    produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserAuth> createUserAuth(@PathVariable("id") String id,
                                                   @RequestBody UserAuth userAuth) {
        if (!id.equals(userAuth.getId())) {
            logger.warn("User id mismatch: id={} UserAuth={}", id, userAuth);
            return ResponseEntity.badRequest().build();
        }
        logger.info("Received user auth post request;  userAuth={}", userAuth);
        if (getAuthServiceBean().createUserAuth(userAuth)) {
            return ResponseEntity.ok().header("Pragma", "no-cache").body(userAuth);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


    /**
     * Delete all users. Usage is limited to admin role.
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(path= AppConfig.USERS_AUTH_PATH, produces=MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteUsersAuth() {
        if (getAuthServiceBean().deleteUsersAuth()) {
            return ResponseEntity.ok("USERS AUTH DELETED");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("USERS AUTH NOT DELETED");
    }

    private ResponseEntity<OAuthToken> createTokenResponse(String userId, OAuthToken authToken) {
        if (authToken == null) {
            logger.info("User log in FAILED; userId={}; context={}", userId, authToken);
            return new ResponseEntity(null, HttpStatus.FORBIDDEN);
        }

        logger.info("User log in SUCCESSFUL; userId={}; context={}", userId, authToken);
        CacheControl cc = CacheControl.maxAge(3600, TimeUnit.SECONDS);

        return ResponseEntity.ok().cacheControl(cc).header("Pragma","no-cache").body(authToken);
    }

    private boolean authenticateClient(HttpServletRequest httpServletRequest) {
        String authHeader = httpServletRequest.getHeader("Authorization");
        logger.debug("Client authorization header: {}", authHeader);
        if (authHeader != null && authHeader.toLowerCase().startsWith("basic")) {
            return getAuthServiceBean().authenticateClient(authHeader);
        }
        logger.warn("No authorization header present for basic authentication of the client");
        return false;
    }

}
