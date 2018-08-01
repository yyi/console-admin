package com.founder.config;

import com.querydsl.core.types.dsl.BooleanExpression;

@FunctionalInterface
public interface LazyBooleanExpression
{
    BooleanExpression get();
}