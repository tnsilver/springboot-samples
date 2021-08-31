package com.tnsilver.contacts.config

import java.util
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.dao.support.PersistenceExceptionTranslator
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.hibernate5.HibernateExceptionTranslator
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.util.Assert
import com.tnsilver.contacts.model.Contact
import com.tnsilver.contacts.repository.ContactRepository
import org.hibernate.cfg.AvailableSettings

/*
 * File: JpaConfig.java
 * Creation Date: Jun 19, 2021
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


//@lombok.Generated // skip jacoco
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackageClasses = Array(classOf[ContactRepository]))
@EnableAutoConfiguration /*(exclude = { MongoAutoConfiguration.class,
                                      MongoDataAutoConfiguration.class,
                                      MongoRepositoriesAutoConfiguration.class })*/
@EnableTransactionManagement class JpaConfig {
  @Value("${spring.jpa.show-sql}") private var showSql : String = null
  @Value("${spring.jpa.hibernate.ddl-auto}") private var ddlAuto : String = null
  @Value("${spring.jpa.properties.hibernate.dialect}") private var hibernateDialect : String = null
  @Autowired private[config] var dataSource : DataSource = null

  /**
   * Entity manager factory. (must wait for flyway to finsih migration)
   *
   * @param dataSource the data source
   * @return the local container entity manager factory bean
   */
  @Bean(name = Array("entityManagerFactory"))
  @DependsOn(Array("flyway")) def entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean = {
    Assert.notNull(dataSource, "dataSource is null")
    createEntityManagerFactoryBean(dataSource)
  }

  /**
   * Transaction manager.
   *
   * @param entityManagerFactory the entity manager factory
   * @return the jpa transaction manager
   */
  @Bean(name = Array("transactionManager")) def transactionManager(entityManagerFactory: EntityManagerFactory) = new JpaTransactionManager(entityManagerFactory)

  /**
   * Persistence exception translator.
   *
   * @return the persistence exception translator
   */
  @Bean(name = Array("persistenceExceptionTranslator")) def persistenceExceptionTranslator = new HibernateExceptionTranslator

  /**
   * Creates the entity manager factory bean.
   *
   * @param dataSource       the data source
   * @param dialectClassName the dialect class name
   * @return the local container entity manager factory bean
   */
  protected def createEntityManagerFactoryBean(dataSource: DataSource): LocalContainerEntityManagerFactoryBean = {
    val properties = new util.HashMap[String, String]
    properties.put(AvailableSettings.HBM2DDL_AUTO, ddlAuto)
    properties.put(AvailableSettings.SHOW_SQL, showSql)
    properties.put(AvailableSettings.FORMAT_SQL, showSql)
    properties.put(AvailableSettings.ENABLE_LAZY_LOAD_NO_TRANS, "true")
    properties.put(AvailableSettings.DIALECT, hibernateDialect)
    properties.put("hibernate.connection.characterEncoding", "utf8")
    properties.put("hibernate.connection.UseUnicode", "true")
    properties.put("hibernate.connection.CharSet", "UTF-8")
    properties.put("hibernate.dialect.storage_engine", "innodb")
    properties.put("hibernate.physical_naming_strategy", classOf[SpringPhysicalNamingStrategy].getName)
    properties.put("hibernate.implicit_naming_strategy", classOf[SpringImplicitNamingStrategy].getName)
    val em = new LocalContainerEntityManagerFactoryBean
    em.setDataSource(dataSource)
    em.setPackagesToScan(classOf[Contact].getPackageName)
    val vendorAdapter = new HibernateJpaVendorAdapter
    em.setJpaVendorAdapter(vendorAdapter)
    em.setJpaPropertyMap(properties)
    em
  }
}
