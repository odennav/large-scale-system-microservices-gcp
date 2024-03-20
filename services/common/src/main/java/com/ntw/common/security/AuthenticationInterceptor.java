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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by anurag on 24/03/17.
 */

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return false;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null) {
            logger.info("No Authorization header present in the request; requestContext={}", request.toString());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("User does not have an authentication token");
            return false;
        }

        logger.debug("Auth header : "+authHeader);

        UserAuth userAuth = getUserCred(authHeader);
        if (userAuth == null) {
            logger.info("Null access token");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().println("User does not have a valid authentication token");
            return false;
        }

        AppAuthentication appAuthentication = new AppAuthentication(userAuth,
                request.isSecure(), authHeader);
        appAuthentication.setAuthenticated(true);
        AppSecurityContext appSecurityContext = new AppSecurityContext(appAuthentication);
        request.setAttribute("AppSecurityContext",appSecurityContext);
        SecurityContextHolder.setContext(appSecurityContext);
        logger.debug("User authenticated; context={}", userAuth);

        return true;
    }

    private UserAuth getUserCred(String authHeader) {
        if(authHeader==null || !authHeader.startsWith("Bearer")) {
            logger.error("Authorization not provided; authHeader={}", authHeader);
            return null;
        }

        String authToken = authHeader.substring("Bearer".length()).trim();

        JwtUtility jwtUtility = new JJwtUtility();
        UserAuth userAuth = jwtUtility.parseToken(authToken);

        return userAuth;
    }

}

