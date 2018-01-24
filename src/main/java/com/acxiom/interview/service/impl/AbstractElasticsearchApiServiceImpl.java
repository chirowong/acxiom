package com.acxiom.interview.service.impl;

import com.acxiom.interview.enums.OperatorType;
import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestFilter;
import com.acxiom.interview.request.ApiRequestOrder;
import com.acxiom.interview.request.ApiRequestPage;
import com.acxiom.interview.enums.PageOrderType;
import com.acxiom.interview.service.BaseApiService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Elastic搜索服务
 * @author wangzhiliang
 */
public abstract class  AbstractElasticsearchApiServiceImpl implements BaseApiService {
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Sort convertSort(ApiRequestPage requestPage) {
        if (requestPage.getOrderList() != null && !requestPage.getOrderList().isEmpty()) {
            List<Sort.Order> orderList = new ArrayList<>();
            for (ApiRequestOrder requestOrder : requestPage.getOrderList()) {
                orderList.add(this.convertSortOrder(requestOrder));
            }
            return new Sort(orderList);
        }
        return null;
    }

    private Sort.Order convertSortOrder(ApiRequestOrder requestOrder) {
        Sort.Direction direction;
        if (requestOrder.getOrderType().equals(PageOrderType.DESC)) {
            direction = Sort.Direction.DESC;
        } else {
            direction = Sort.Direction.ASC;
        }
        return new Sort.Order(direction, requestOrder.getField());
    }

    protected Pageable convertPageable(ApiRequestPage requestPage) {
        return new PageRequest(requestPage.getPage(), requestPage.getPageSize(), this.convertSort(requestPage));
    }

    protected QueryBuilder convertQueryBuilder(ApiRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (ApiRequestFilter filter : request.getFilterList()) {
            boolQueryBuilder.filter(convertFilterBuilder(filter));
        }
        return boolQueryBuilder;
    }

    protected QueryBuilder convertFilterBuilder(ApiRequestFilter filter) {
        switch (filter.getOperatorType()) {
            case EQ:
                if (filter.getValue() instanceof String) { //String 精确匹配
                    return QueryBuilders.matchPhraseQuery(filter.getField(), filter.getValue());
                } else {
                    return QueryBuilders.termQuery(filter.getField(), filter.getValue());
                }
            case GE:
                if (filter.getValue() instanceof Comparable) {
                    return QueryBuilders.rangeQuery(filter.getField()).gte(filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
            case LE:
                if (filter.getValue() instanceof Comparable) {
                    return QueryBuilders.rangeQuery(filter.getField()).lte(filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
            case GT:
                if (filter.getValue() instanceof Comparable) {
                    return QueryBuilders.rangeQuery(filter.getField()).gt(filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
            case LT:
                if (filter.getValue() instanceof Comparable) {
                    return QueryBuilders.rangeQuery(filter.getField()).lt(filter.getValue());
                } else {
                    logger.error("字段({})不是可比较对象, value={}", filter.getField(), filter.getValue());
                }
            case BETWEEN:
                Object val1 = filter.getValueList().get(0);
                Object val2 = filter.getValueList().get(1);
                if (val1 instanceof Comparable && val2 instanceof Comparable) {
                    return QueryBuilders.rangeQuery(filter.getField()).from(val1).to(val2);
                } else {
                    logger.error("字段({})不是可比较对象, value1={}, value2={}", filter.getField(), val1, val2);
                }
            case IN:
                return QueryBuilders.termQuery(filter.getField(), filter.getValueList());
            case LIKE:
                return QueryBuilders.termQuery(filter.getField(), filter.getValue());
            case AND:
            case OR:
                if (filter.getValueList() == null || filter.getValueList().isEmpty()) {
                    return null;
                }
                if (StringUtils.isNotBlank(filter.getField())) {
                    logger.error("OR 和 AND 操作不允许指定 field: {}", filter.getField());
                    return null;
                }
                List<QueryBuilder> queryBuilderList = Lists.newArrayList();
                for (Object o : filter.getValueList()) {
                    ApiRequestFilter f = (ApiRequestFilter) o;
                    // 递归调用
                    QueryBuilder queryBuilder = convertFilterBuilder(f);
                    if (queryBuilder != null) {
                        queryBuilderList.add(queryBuilder);
                    }
                }
                if (!queryBuilderList.isEmpty()) {
                    if (filter.getOperatorType() == OperatorType.AND) {
                        return QueryBuilders.andQuery(queryBuilderList.toArray(new QueryBuilder[queryBuilderList.size()]));
                    }
                    if (filter.getOperatorType() == OperatorType.OR) {
                        return QueryBuilders.orQuery(queryBuilderList.toArray(new QueryBuilder[queryBuilderList.size()]));
                    }
                }
                return null;
            case NOT:
                //not 操作只支持1个
                ApiRequestFilter apiRequestFilter = (ApiRequestFilter) filter.getValueList().get(0);
                return QueryBuilders.notQuery(QueryBuilders.termQuery(apiRequestFilter.getField(), apiRequestFilter.getValue()));
            default:
                logger.error("不支持的运算符, op={}", filter.getOperatorType());
                return null;
        }
    }
}
