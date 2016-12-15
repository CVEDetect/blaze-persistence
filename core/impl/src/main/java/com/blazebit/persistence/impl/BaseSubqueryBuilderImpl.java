/*
 * Copyright 2014 - 2016 Blazebit.
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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.blazebit.persistence.BaseOngoingSetOperationBuilder;
import com.blazebit.persistence.impl.expression.Expression;
import com.blazebit.persistence.impl.expression.ExpressionFactory;
import com.blazebit.persistence.spi.DbmsStatementType;
import com.blazebit.persistence.spi.SetOperationType;

/**
 *
 * @author Christian Beikov
 * @author Moritz Becker
 * @since 1.0
 */
public class BaseSubqueryBuilderImpl<T, X, Y extends BaseOngoingSetOperationBuilder<?, ?, ?>, Z extends BaseOngoingSetOperationBuilder<?, ?, ?>> extends AbstractCommonQueryBuilder<Tuple, X, Y, Z, BaseFinalSetOperationSubqueryBuilderImpl<T, ?>> implements SubqueryInternalBuilder<T> {

    protected final T result;
    protected final SubqueryBuilderListener<T> listener;
    protected final SubqueryBuilderListenerImpl<T> subListener;

    public BaseSubqueryBuilderImpl(MainQuery mainQuery, AliasManager aliasManager, JoinManager parentJoinManager, ExpressionFactory expressionFactory, T result, SubqueryBuilderListener<T> listener, BaseFinalSetOperationSubqueryBuilderImpl<T, ?> finalSetOperationBuilder) {
        super(mainQuery, false, DbmsStatementType.SELECT, Tuple.class, null, aliasManager, parentJoinManager, expressionFactory, finalSetOperationBuilder, true);
        this.result = result;
        this.listener = listener;
        this.subListener = new SubqueryBuilderListenerImpl<T>();
    }
    
    public SubqueryBuilderListenerImpl<T> getSubListener() {
        return subListener;
    }

    @Override
    public List<Expression> getSelectExpressions() {
        List<Expression> selectExpressions = new ArrayList<Expression>(selectManager.getSelectInfos().size());

        for (SelectInfo info : selectManager.getSelectInfos()) {
            selectExpressions.add(info.getExpression());
        }

        return selectExpressions;
    }

    public T getResult() {
        return result;
    }
    
    protected BaseFinalSetOperationSubqueryBuilderImpl<T, ?> createFinalSetOperationBuilder(SetOperationType operator, boolean nested, boolean isSubquery) {
        SubqueryBuilderImpl<?> newInitiator = finalSetOperationBuilder == null ? null : finalSetOperationBuilder.getInitiator();
        return createFinalSetOperationBuilder(operator, nested, isSubquery, newInitiator);
    }
    
    protected BaseFinalSetOperationSubqueryBuilderImpl<T, ?> createFinalSetOperationBuilder(SetOperationType operator, boolean nested, boolean isSubquery, SubqueryBuilderImpl<?> newInitiator) {
        // TODO: this should never be null, handle it!
        SubqueryBuilderListener<T> newListener = finalSetOperationBuilder == null ? listener : finalSetOperationBuilder.getSubListener();
        T newResult = finalSetOperationBuilder == null ? result : finalSetOperationBuilder.getResult();
        
        if (isSubquery) {
            return new OngoingFinalSetOperationSubqueryBuilderImpl<T>(mainQuery, newResult, operator, nested, newListener, newInitiator);
        } else {
            return new FinalSetOperationSubqueryBuilderImpl<T>(mainQuery, newResult, operator, nested, newListener, newInitiator);
        }
    }

    @SuppressWarnings("unchecked")
    protected LeafOngoingSetOperationSubqueryBuilderImpl<T> createLeaf(BaseFinalSetOperationSubqueryBuilderImpl<T, ?> finalSetOperationBuilder) {
        SubqueryBuilderListener<T> newListener = finalSetOperationBuilder.getSubListener();
        LeafOngoingSetOperationSubqueryBuilderImpl<T> next = new LeafOngoingSetOperationSubqueryBuilderImpl<T>(mainQuery, aliasManager, joinManager.getParent(), expressionFactory, result, newListener, (FinalSetOperationSubqueryBuilderImpl<T>) finalSetOperationBuilder);
        newListener.onBuilderStarted(next);
        return next;
    }

    @SuppressWarnings("unchecked")
    protected <W> OngoingSetOperationSubqueryBuilderImpl<T, W> createOngoing(BaseFinalSetOperationSubqueryBuilderImpl<T, ?> finalSetOperationBuilder, W endSetResult) {
        // TODO: This is such an ugly hack, but I don't know how else to fix this generics issue for now
        finalSetOperationBuilder.setEndSetResult((T) endSetResult);
        
        SubqueryBuilderListener<T> newListener = finalSetOperationBuilder.getSubListener();
        OngoingSetOperationSubqueryBuilderImpl<T, W> next = new OngoingSetOperationSubqueryBuilderImpl<T, W>(mainQuery, aliasManager, joinManager.getParent(), expressionFactory, result, newListener, (OngoingFinalSetOperationSubqueryBuilderImpl<T>) finalSetOperationBuilder, endSetResult);
        newListener.onBuilderStarted(next);
        return next;
    }
}
