/*
 * Copyright 2014 - 2022 Blazebit.
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

package com.blazebit.persistence.impl;

import com.blazebit.persistence.UpdateCriteriaBuilder;
import com.blazebit.persistence.parser.expression.ExpressionCopyContext;

import java.util.Map;

/**
 *
 * @param <T> The query result type
 * @author Christian Beikov
 * @since 1.2.0
 */
public class UpdateCollectionCriteriaBuilderImpl<T> extends AbstractUpdateCollectionCriteriaBuilder<T, UpdateCriteriaBuilder<T>, Void> implements UpdateCriteriaBuilder<T> {

    public UpdateCollectionCriteriaBuilderImpl(MainQuery mainQuery, Class<T> updateOwnerClass, String alias, String collectionName) {
        super(mainQuery, null, true, updateOwnerClass, alias, null, null, null, null, collectionName);
    }

    public UpdateCollectionCriteriaBuilderImpl(AbstractUpdateCollectionCriteriaBuilder<T, UpdateCriteriaBuilder<T>, Void> builder, MainQuery mainQuery, QueryContext queryContext, Map<JoinManager, JoinManager> joinManagerMapping, ExpressionCopyContext copyContext) {
        super(builder, mainQuery, queryContext, joinManagerMapping, copyContext);
    }

    @Override
    AbstractCommonQueryBuilder<T, UpdateCriteriaBuilder<T>, AbstractCommonQueryBuilder<?, ?, ?, ?, ?>, AbstractCommonQueryBuilder<?, ?, ?, ?, ?>, BaseFinalSetOperationBuilderImpl<T, ?, ?>> copy(QueryContext queryContext, Map<JoinManager, JoinManager> joinManagerMapping, ExpressionCopyContext copyContext) {
        return new UpdateCollectionCriteriaBuilderImpl<>(this, queryContext.getParent().mainQuery, queryContext, joinManagerMapping, copyContext);
    }
}
