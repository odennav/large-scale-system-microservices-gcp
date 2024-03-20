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

package com.ntw.auth;

import com.ntw.common.config.AppConfig;
import com.ntw.common.security.AuthenticationInterceptor;
import com.ntw.common.security.AuthorizationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by anurag on 21/09/20.
 */
@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor())
                .order(1)
                .addPathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH +"/**")
                .excludePathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH
                        +AppConfig.AUTH_TOKEN_PATH)
                .excludePathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH
                        +AppConfig.AUTH_TOKEN_USER_PATH)
                .addPathPatterns(AppConfig.USERS_PROFILE_RESOURCE_PATH)
                .addPathPatterns(AppConfig.USERS_PROFILE_RESOURCE_PATH +"/**");

        registry.addInterceptor(new AuthorizationInterceptor())
                .order(2)
                .addPathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH +"/**")
                .excludePathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH
                        +AppConfig.AUTH_TOKEN_PATH)
                .excludePathPatterns(AppConfig.AUTHORIZATION_RESOURCE_PATH
                        +AppConfig.AUTH_TOKEN_USER_PATH)
                .addPathPatterns(AppConfig.USERS_PROFILE_RESOURCE_PATH +"/**");
    }
}
