/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sola.services.common.repository;

import java.io.Reader;
import java.util.ResourceBundle;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.sola.common.SOLAException;
import org.sola.common.messaging.ServiceMessage;

/**
 * Provides common functionality for configuring and managing the Mybatis database connection.
 * The CommonRepositoryImpl creates a DatabaseConnectionManager and configures it with the settings
 * from the mybatisConnectionConfig.xml that is included with each EJB in the default resources
 * package. 
 * <p>
 * The DatabaseConnectionManager supports two data sources, a sharedDataSource and a 
 * specificDataSource. The configuration for each data source is included in the 
 * mybatisConnectionConfig.xml. The sharedDataSource is intended for development and testing
 * where one JNDI data source is used by all EJB's to connect to the database. The specificDataSource
 * is intended for production environments where it may be desirable for each EJB to have its own
 * JDNI data source configured to connect to the database to avoid one database user with access
 * to all database tables.
 * </p>
 * <p> 
 * The databaseConnection property file can be configured to indicate whether the sharedDataSource
 * should be used for all EJB's or not. Note that this property file is configured using Maven 
 * filtering. Refer to the POM file for the Services Common (sola-services-common) project for 
 * details. 
 *</p>
 * <p>
 * Mybatis provides detailed logging of all SQL commands it executes as well the ability to log the
 * results of each SQL statement. To direct this logging output to the Glassfish Server Log use
 * Log Levels tab of the Logger Settings node in the Glassfish Admin Console to set the java.sql
 * and java.sql.Connection loggers to the FINE level. If you wish to log the results of each
 * SQL query, set java.sql.ResultSet to FINE as well, but be aware that logging all results may
 * negatively impact performance of the application. 
 * </p>
 * @author soladev
 */
public class DatabaseConnectionManager {

    private SqlSessionFactory sqlSessionFactory;
    private Class<? extends CommonMapper> mapperClass;
    private static final String SHARED_ENV = "sharedDataSource";
    private static final String SPECIFIC_ENV = "specificDataSource";
    private static final String PROPERTY_FILENAME = "databaseConnection";
    private static final String TRUE = "true";
    private static final String SHARED_CONNECTION_PROP = "SHARED_CONNECTION";

    /**
     * This constructor is provided to simplify mocking of the DatabaseConnectionManager. Refer to
     * the MockDatabaseConnectionManager in the Services Test Common (sola-test-common) project 
     * for an example.  
     */
    protected DatabaseConnectionManager() {
    }

    /**
     * Initializes the DatabaseConnectionManager using the URL for the Mybatis configuration file
     * and a list of Mybatis mapper classes that identify the SqlSession methods that may be 
     * executed. 
     * @param configFileUrl URL to the Mybatis configuration file.
     * @param mappers A list of one or more Mybatis mapper classes
     * @see AddressConnectionFactory
     * @see PartyConnectionFactory
     */
    public DatabaseConnectionManager(String configFileUrl, Class<? extends CommonMapper> mapperClass) {
        // Ensure Mybatis uses the Glassfish JDK Logging provider in preference to any other 
        // logging provider. 
        LogFactory.useJdkLogging();
        try {
            // Determine which data source to use - shared or specific
            String environment = SPECIFIC_ENV;
            ResourceBundle bundle = ResourceBundle.getBundle(PROPERTY_FILENAME);
            if (bundle != null && TRUE.equalsIgnoreCase(
                    bundle.getString(SHARED_CONNECTION_PROP))) {
                environment = SHARED_ENV;
            }
            // Load the Mybatis configuration file and the mapper classes into the SqlSessionFactory. 
            Reader reader = Resources.getUrlAsReader(configFileUrl);
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, environment);

                sqlSessionFactory.getConfiguration().addMapper(mapperClass);
                this.mapperClass = mapperClass;

            }
        } catch (Exception ex) {
            throw new SOLAException(ServiceMessage.GENERAL_UNEXPECTED,
                    new Object[]{configFileUrl, ex});
        }
    }

    /**
     * @return The Mybatis SqlSessionFactory for the database connection
     */
    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }

    /**
     * @return A newly opened Mybatis SqlSession that can be used to query or update the database. 
     * Note that the SqlSession must be closed once all work for the transaction is complete. 
     */
    public SqlSession getSqlSession() {
        return getSqlSessionFactory().openSession();
    }

    public Class<? extends CommonMapper> getMapperClass() {
        return mapperClass;
    }
}
