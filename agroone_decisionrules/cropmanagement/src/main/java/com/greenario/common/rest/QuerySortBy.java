package com.greenario.common.rest;

import com.greenario.common.api.BaseQuery;
import com.greenario.common.api.BaseQuery.SortDirection;

public interface QuerySortBy<Q extends BaseQuery<?, ?>> {

  void applySortByForQuery(Q query, SortDirection sortDirection);
}
