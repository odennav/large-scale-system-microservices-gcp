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

import com.ntw.common.entity.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by anurag on 24/03/17.
 */

/**
 * Filter interface implementation for authorizing user requests
 */
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = ((HandlerMethod) handler).getMethod();

        List<Role> allowedRoles = extractRoles(resourceMethod);
        logger.debug("Allowed method roles for method {} are {}", resourceMethod.getName(),
                allowedRoles.size() > 0 ? allowedRoles.toArray().toString() : "None");

        if (allowedRoles.isEmpty()) {
            // Get the resource class which matches with the requested URL
            // Extract the roles declared by it
            Class<?> resourceClass = resourceMethod.getDeclaringClass();
            allowedRoles = extractRoles(resourceClass);
            logger.debug("Allowed class roles for class " + resourceClass.getName()
                    + " are " + (allowedRoles.size() > 0 ? allowedRoles.toArray().toString() : "None"));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!allowedRoles.isEmpty() && !checkPermissions(allowedRoles)) {
            logger.info("User "+authentication.getName()+" not authorized");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("User not authorized");
            return false;
        }

        logger.debug("User "+authentication.getName()+" has been authorized "
                + "to execute "+resourceMethod.getName());

        return true;
    }

    // Extract the roles from the annotated element
    private List<Role> extractRoles(AnnotatedElement annotatedElement) {
        List<Role> roles = new ArrayList<>();
        if (annotatedElement != null) {
            Secured secured = annotatedElement.getAnnotation(Secured.class);
            if (secured != null) {
                Role[] allowedRoles = secured.value();
                roles = Arrays.asList(allowedRoles);
            }
        }
        return roles;
    }

    private boolean checkPermissions(List<Role> allowedRoles) {
        // Check if the user contains one of the allowed roles
        // Throw an Exception if the user has not permission to execute the method
        AppAuthentication authentication = (AppAuthentication) SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Allowed roles : {}", allowedRoles);
        logger.debug("Roles provided = {}", authentication.getAuthorities());
        for (Role allowedRole : allowedRoles) {
            if (authentication.getAuthorities().contains(Role.getRole(allowedRole.toString()))) {
                logger.debug("{}'s role {} is authorized", authentication.getName(), allowedRole);
                return true;
            }
        }
        logger.warn("No authorized role found for user {}", authentication.getName());
        return false;
    }

}

