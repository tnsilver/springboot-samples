/*
 * File: JpaConfig.java
 * Creation Date: Jul 20, 2021
 *
 * Copyright (c) 2021 T.N.Silverman - all rights reserved
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses  this file to you under the Apache License, Version
 * 2.0 (the "License");  you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tnsilver.contacts.config;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import com.tnsilver.contacts.model.Contact;
import com.tnsilver.contacts.repository.ContactRepository;

//@lombok.Generated // skip jacoco
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = ContactRepository.class)
@EnableAutoConfiguration
@EnableTransactionManagement
public class JpaConfig {

    // @formatter:off
    @Autowired Environment env;
    @Value("${spring.jpa.show-sql}") private String showSql;
    @Value("${spring.jpa.hibernate.ddl-auto}") private String ddlAuto;
    @Value("${spring.flyway.locations}") private String locations;
    @Value("${spring.jpa.properties.hibernate.dialect}") private String hibernateDialect;
    // @formatter:on

    /**
     * Entity manager factory. (must wait for flyway to finish migration)
     *
     * @param dataSource the data source
     * @return the local container entity manager factory bean
     */
    @Bean(name = "entityManagerFactory")
    @DependsOn("flyway")
    protected LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        Assert.notNull(dataSource, "dataSource is null");
        return createEntityManagerFactoryBean(dataSource);
    }

    /**
     * Transaction manager.
     *
     * @param entityManagerFactory the entity manager factory
     * @return the jpa transaction manager
     */
    @Bean(name = "transactionManager")
    protected JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    /**
     * Persistence exception translator.
     *
     * @return the persistence exception translator
     */
    @Bean(name = "persistenceExceptionTranslator")
    protected PersistenceExceptionTranslator persistenceExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    /**
     * Creates the entity manager factory bean.
     *
     * @param dataSource       the data source
     * @param dialectClassName the dialect class name
     * @return the local container entity manager factory bean
     */
    protected LocalContainerEntityManagerFactoryBean createEntityManagerFactoryBean(DataSource dataSource) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, ddlAuto);
        properties.put(org.hibernate.cfg.Environment.SHOW_SQL, showSql);
        properties.put(org.hibernate.cfg.Environment.FORMAT_SQL, showSql);
        properties.put(org.hibernate.cfg.Environment.ENABLE_LAZY_LOAD_NO_TRANS, "true");
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.connection.characterEncoding", "utf8");
        properties.put("hibernate.connection.UseUnicode", "true");
        properties.put("hibernate.connection.CharSet", "UTF-8");
        properties.put("hibernate.dialect.storage_engine", "innodb");
        properties.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        properties.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(new String[] { Contact.class.getPackageName() });
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaPropertyMap(properties);
        return em;
    }

}
