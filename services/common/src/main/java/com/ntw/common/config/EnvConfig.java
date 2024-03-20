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

package com.ntw.common.config;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by anurag on 09/07/18.
 */
public class EnvConfig {

    private static final String USE_SERVICE_REGISTRY = "eureka.client.registerWithEureka";

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    private static Properties properties = new Properties();
    private static final String PROP_FILE_NAME = "config";

    private Environment environment;

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public EnvConfig() {
        initialize();
    }

    public void initialize() {

        String propFileName = PROP_FILE_NAME+".properties";

        try {
            InputStream in = EnvConfig.class.getClassLoader().getResourceAsStream(propFileName);
            if (in != null) {
                properties.load(in);
            } else {
                logger.error("Unable to read {} file",propFileName);
            }
        } catch (IOException e1) {
            logger.error("Unable to read "+propFileName+" file");
            logger.debug(e1.getStackTrace().toString());
        }
        System.getProperties().stringPropertyNames().
                forEach(key -> {if(properties.containsKey(key)) properties.setProperty(key, System.getProperty(key));});
        System.getenv().keySet().
                forEach(key -> {if(properties.containsKey(key)) properties.setProperty(key, System.getenv(key));});
        String[] otherEnv = {"server.log.level", "server.log.path"};
        for (String key : otherEnv) {
            if (System.getenv(key) != null) {
                properties.setProperty(key, System.getenv(key));
            }
        }
        Map<String, String> envLog = new HashMap<>();
        SortedSet<String> sortedProps = new TreeSet<>();
        sortedProps.addAll(properties.stringPropertyNames());
        sortedProps.forEach(x -> envLog.put(x,properties.getProperty(x)));
        logger.info("Environment & System Variables = {}",new Gson().toJson(envLog));
    }

    public boolean useServiceRegistry() {
        return Boolean.parseBoolean(properties.getProperty(USE_SERVICE_REGISTRY));
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
