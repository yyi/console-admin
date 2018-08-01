package com.founder.config;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Visitor;
import org.apache.commons.lang3.StringUtils;


import javax.annotation.Nullable;
import java.util.function.Function;


public class WhereClauseBuilder implements Predicate, Cloneable {
    private BooleanBuilder delegate;

    public WhereClauseBuilder() {
        this.delegate = new BooleanBuilder();
    }

    public WhereClauseBuilder(Predicate pPredicate) {
        this.delegate = new BooleanBuilder(pPredicate);
    }

    public WhereClauseBuilder and(Predicate right) {
        return new WhereClauseBuilder(delegate.and(right));
    }

    public WhereClauseBuilder or(Predicate right) {
        return new WhereClauseBuilder(delegate.or(right));
    }

    public <V> WhereClauseBuilder optionalAnd(@Nullable V pValue, LazyBooleanExpression pBooleanExpression) {
        return applyIfNotEmpty(pValue, this::and, pBooleanExpression);
    }

    public <V> WhereClauseBuilder optionalOr(@Nullable V pValue, LazyBooleanExpression pBooleanExpression) {
        return applyIfNotEmpty(pValue, this::or, pBooleanExpression);
    }

    private <V> WhereClauseBuilder applyIfNotEmpty(@Nullable V pValue, Function<Predicate, WhereClauseBuilder> pFunction, LazyBooleanExpression pBooleanExpression) {
        if (pValue != null && StringUtils.isNoneBlank(pValue.toString())) {
            return new WhereClauseBuilder(pFunction.apply(pBooleanExpression.get()));
        }

        return this;
    }

    @Override
    public Predicate not() {
        return delegate.not();
    }

    @Nullable
    @Override
    public <R, C> R accept(Visitor<R, C> visitor, @Nullable C c) {
        return delegate.accept(visitor, c);
    }

    @Override
    public Class<? extends Boolean> getType() {
        return delegate.getType();
    }
}

