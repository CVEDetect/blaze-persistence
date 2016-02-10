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
package com.blazebit.persistence.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.blazebit.persistence.impl.expression.Expression;
import com.blazebit.persistence.impl.expression.Expression.Visitor;

/**
 *
 * @author Christian Beikov
 * @author Moritz Becker
 * @since 1.0
 */
public class GroupByManager extends AbstractManager {

    /**
     * We use an ArrayList since a HashSet causes problems when the path reference in the expression is changed
     * after it was inserted into the set (e.g. when implicit joining is performed).
     */
    private final List<NodeInfo> groupByInfos;

    GroupByManager(ResolvingQueryGenerator queryGenerator, ParameterManager parameterManager) {
        super(queryGenerator, parameterManager);
        groupByInfos = new ArrayList<NodeInfo>();
    }
    
    void groupBy(Expression expr) {
        groupByInfos.add(new NodeInfo(expr));
        registerParameterExpressions(expr);
    }
    
    boolean existsGroupBy(Expression expr) {
        return groupByInfos.contains(new NodeInfo(expr));
    }

    void buildGroupByClauses(Set<String> clauses) {
        if (groupByInfos.isEmpty()) {
            return;
        }

        boolean conditionalContext = queryGenerator.setConditionalContext(false);
        StringBuilder sb = new StringBuilder();

        for (NodeInfo info : groupByInfos) {
            sb.setLength(0);
            queryGenerator.setQueryBuffer(sb);
            info.getExpression().accept(queryGenerator);
            clauses.add(sb.toString());
        }
        queryGenerator.setConditionalContext(conditionalContext);
    }

    void buildGroupBy(StringBuilder sb, Set<String> clauses) {
        if (!clauses.isEmpty()) {
            sb.append(" GROUP BY ");
            build(sb, clauses);
        }
    }

    void applyTransformer(ExpressionTransformer transformer) {
        for (NodeInfo groupBy : groupByInfos) {
            groupBy.setExpression(transformer.transform(groupBy.getExpression(), ClauseType.GROUP_BY, true));
        }
    }

    void acceptVisitor(Visitor v) {
        for (NodeInfo groupBy : groupByInfos) {
            groupBy.getExpression().accept(v);
        }
    }

    boolean hasGroupBys() {
        return groupByInfos.size() > 0;
    }

    boolean isEmpty() {
        return groupByInfos.isEmpty();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.groupByInfos != null ? this.groupByInfos.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GroupByManager other = (GroupByManager) obj;
        if (this.groupByInfos != other.groupByInfos && (this.groupByInfos == null || !this.groupByInfos.equals(other.groupByInfos))) {
            return false;
        }
        return true;
    }

}
