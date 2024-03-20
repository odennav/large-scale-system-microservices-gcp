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

package com.ntw.oms.admin.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.*;
import org.springframework.data.cassandra.core.convert.CassandraConverter;
import org.springframework.data.cassandra.core.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.cql.CqlTemplate;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.SimpleUserTypeResolver;

/**
 * Created by anurag on 13/08/19.
 */
@Configuration
@PropertySource(value = { "classpath:config.properties" })
public class CQLConfig {

    @Autowired
    private Environment environment;

    protected String getKeyspaceName() {
        return environment.getProperty("database.cassandra.keySpace");
    }

    @Bean
    @ConditionalOnExpression("'${database.type}'.equals('CQL') or '${database.type}'.equals('ALL')")
    public CassandraCqlClusterFactoryBean cluster() {
        CassandraCqlClusterFactoryBean cluster = new CassandraCqlClusterFactoryBean();
        cluster.setContactPoints(environment.getProperty("database.cassandra.hosts"));
        cluster.setPort(Integer.parseInt(environment.getProperty("database.cassandra.port")));
        return cluster;
    }

    /*
     * Factory bean that creates the com.datastax.driver.core.Session instance
     */
    @Bean
    @ConditionalOnExpression("'${database.type}'.equals('CQL') or '${database.type}'.equals('ALL')")
    public CassandraCqlSessionFactoryBean session() {
        CassandraCqlSessionFactoryBean session = new CassandraCqlSessionFactoryBean();
        session.setCluster(cluster().getObject());
        session.setKeyspaceName(getKeyspaceName());
        return session;
    }

    @Bean
    @ConditionalOnExpression("'${database.type}'.equals('CQL') or '${database.type}'.equals('ALL')")
    public CqlTemplate cqlTemplate() {
        return new CqlTemplate(session().getObject());
    }

    @Bean
    @ConditionalOnExpression("'${database.type}'.equals('CQL') or '${database.type}'.equals('ALL')")
    public CassandraMappingContext mappingContext() {
        BasicCassandraMappingContext mappingContext =  new BasicCassandraMappingContext();
        mappingContext.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(),
                getKeyspaceName()));

        return mappingContext;
    }

    @Bean
    @ConditionalOnExpression("'${database.type}'.equals('CQL') or '${database.type}'.equals('ALL')")
    public CassandraConverter converter() {
        return new MappingCassandraConverter(mappingContext());
    }

}