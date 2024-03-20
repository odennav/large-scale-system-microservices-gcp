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

package com.ntw.oms.gateway;

/**
 * Created by anurag on 17/08/19.
 */
import com.ntw.common.config.EnvConfig;
import com.ntw.oms.gateway.filter.AuthenticationFilter;
import com.ntw.oms.gateway.filter.RequestFilter;
import com.ntw.oms.gateway.filter.ResponseFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
@PropertySource(value = { "classpath:config.properties" })
public class WebApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebApplication.class);
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(
            @Value("${server.port:8080}") int port,
            @Value("${server.threadPool.threads.minimum:1}") int minThreadCount,
            @Value("${server.threadPool.threads.maximum:10}") int maxThreadCount,
            @Value("${server.threadPool.threads.idleTime:60000}") int idleThreadTimeout
    ) {
        JettyServletWebServerFactory factory = new JettyServletWebServerFactory();
        factory.setPort(port);
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(minThreadCount);
        threadPool.setMaxThreads(maxThreadCount);
        threadPool.setIdleTimeout(idleThreadTimeout);
        factory.setThreadPool(threadPool);
        return factory;
    }

    @Bean
    public RequestFilter requestFilter() {
        return new RequestFilter();
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter();
    }

    @Bean
    ResponseFilter responseFilter() {
        return new ResponseFilter();
    }

    @Bean
    public EnvConfig envConfig(Environment environment) {
        // Added this bean to view env vars on console/log
        EnvConfig envConfigBean = new EnvConfig();
        envConfigBean.setEnvironment(environment);
        return envConfigBean;
    }

}
