/*
 * Copyright 2014 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blazebit.persistence.spi;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.EntityType;

import com.blazebit.persistence.ReturningResult;

/**
 * Interface implemented by the criteria provider.
 *
 * It is invoked to do some extended functionality like retrieving sql and
 *
 * @author Christian Beikov
 * @since 1.1.0
 */
public interface ExtendedQuerySupport {

    public String getSql(EntityManager em, Query query);
    
    public String[] getColumnNames(EntityManager em, EntityType<?> entityType, String attributeName);
    
    public Connection getConnection(EntityManager em);
    
    @SuppressWarnings("rawtypes")
    public List getResultList(EntityManager em, List<Query> participatingQueries, Query query, String sqlOverride);
    
    public Object getSingleResult(EntityManager em, List<Query> participatingQueries, Query query, String sqlOverride);
    
    public int executeUpdate(EntityManager em, List<Query> participatingQueries, Query query, String sqlOverride);
    
    public ReturningResult<Object[]> executeReturning(EntityManager em, List<Query> participatingQueries, Query exampleQuery, String sqlOverride);
}
