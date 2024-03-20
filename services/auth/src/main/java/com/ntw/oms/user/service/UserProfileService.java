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

import com.ntw.common.config.AppConfig;
import com.ntw.common.entity.Role;
import com.ntw.common.security.Secured;
import com.ntw.oms.user.entity.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by anurag on 30/05/17.
 */
@RestController
@RequestMapping(AppConfig.USERS_PROFILE_RESOURCE_PATH)
public class UserProfileService {
    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    @Autowired
    private UserProfileServiceImpl userProfileServiceBean;

    public UserProfileServiceImpl getUserProfileServiceBean() {
        return userProfileServiceBean;
    }

    /**
     * Get user profile of a user
     * @param id        id of the user
     * @return
     */
    @Secured({Role.ADMIN,Role.USER})
    @GetMapping(path= AppConfig.USER_PROFILE_PATH,produces = "application/json")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable("id") String id) {
        if (!id.equals(getUser())) {
            return ResponseEntity.badRequest().build();
        }
        UserProfile userProfile = getUserProfileServiceBean().getUserProfile(id);
        if (userProfile == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok().body(userProfile);
    }

    /**
     * Insert a user profile record
     * @param userProfile   user profile object to be inserted
     * @return
     */
    @Secured({Role.ADMIN})
    @PutMapping(path= AppConfig.USER_PROFILE_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<UserProfile> createUserProfile(@PathVariable("id") String id,
            @RequestBody UserProfile userProfile) {
        if (!id.equals(userProfile.getId())) {
            return ResponseEntity.badRequest().build();
        }
        boolean success = getUserProfileServiceBean().saveUserProfile(userProfile);
        if (success) {
            logger.debug("UserProfile saved; context={}", userProfile);
            try {
                return ResponseEntity.created(new URI("/userProfile")).body(userProfile);
            } catch (URISyntaxException e) {
                logger.debug("Error saving UserProfile; context={}, error={}", userProfile, e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(userProfile);
            }
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(userProfile);
    }

    /**
     * Delete user profile of a user. Usage limited to admin role.
     * @param id        id of the user whose user profile is to be deleted
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(path= AppConfig.USER_PROFILE_PATH, consumes = "application/json")
    public ResponseEntity<String> removeUserProfile(@PathVariable("id") String id) {
        if (!id.equals(getUser())) {
            return ResponseEntity.badRequest().build();
        }
        boolean success = getUserProfileServiceBean().removeUserProfile(id);
        if (success) {
            logger.debug("UserProfile removed; userId={}", id);
            return ResponseEntity.ok("USER DELETED");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("USER NOT DELETED");
    }

    /**
     * Delete user profile of all users. Usage limited to admin role.
     * @return
     */
    @Secured({Role.ADMIN})
    @DeleteMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> removeUserProfiles() {
        if (getUserProfileServiceBean().removeUserProfiles()) {
            logger.debug("UserProfile records removed");
            return ResponseEntity.ok("USERS DELETED");
        }
        return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body("USERS DELETION FAILED");
    }

    private String getUser() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
