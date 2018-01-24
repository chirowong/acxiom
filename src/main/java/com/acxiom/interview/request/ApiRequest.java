package com.acxiom.interview.request;

import com.acxiom.interview.enums.OperatorType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzhiliang
 */
public class ApiRequest implements Serializable {

    private static final long serialVersionUID = -3250470842829091873L;

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<ApiRequestFilter> filterList;

    private boolean distinct;

    private ApiRequest() {

    }

    public static ApiRequest newInstance() {
        return new ApiRequest();
    }

    public ApiRequest filter(OperatorType operator, String field, Object... values) {
        if (filterList == null) {
            filterList = new ArrayList<>();
        }
        try {
            if (values == null) {
                values = new Object[0];
            }
            if (OperatorType.isLogical(operator)) {
                if (StringUtils.isNotBlank(field)) {
                    throw new IllegalArgumentException("逻辑操作不允许指定 field");
                }

                for (Object value : values) {
                    if (!(value instanceof ApiRequestFilter)) {
                        throw new IllegalArgumentException("逻辑运算参数不为 filter 表达式");
                    }
                }

                filterList.add(new ApiRequestFilter(operator, null, Lists.newArrayList(values)));
            } else if (OperatorType.isZero(operator)) {
                // 支持不需要参数的运算符
                if (values.length > 0) {
                    throw new IllegalArgumentException("参数个数不为0");
                }
                filterList.add(new ApiRequestFilter(operator, field));
            } else if (OperatorType.isUnary(operator)) {
                // 单目运算符
                if (values.length > 1) {
                    throw new IllegalArgumentException("单目运算符参数个数超过1个");
                }
                filterList.add(new ApiRequestFilter(operator, field, values[0]));
            } else if (OperatorType.isBinary(operator)) {
                if (values.length != 2) {
                    throw new IllegalArgumentException("双目运算符参数个数不为1个");
                }
                filterList.add(new ApiRequestFilter(operator, field, Lists.newArrayList(values)));
            } else if (OperatorType.isCollection(operator)) {
                filterList.add(new ApiRequestFilter(operator, field, Lists.newArrayList(values)));
            } else {
                throw new IllegalArgumentException("暂未支持的运算符操作");
            }
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return this;
    }

    public ApiRequest filterEqual(String field, Object value) {
        return this.filter(OperatorType.EQ, field, value);
    }

    public ApiRequest filterLessEqual(String field, Object value) {
        return this.filter(OperatorType.LE, field, value);
    }

    public ApiRequest filterGreaterEqual(String field, Object value) {
        return this.filter(OperatorType.GE, field, value);
    }

    public ApiRequest filterLessThan(String field, Object value) {
        return this.filter(OperatorType.LT, field, value);
    }

    public ApiRequest filterGreaterThan(String field, Object value) {
        return this.filter(OperatorType.GT, field, value);
    }

    public ApiRequest filterBetween(String field, Object low, Object high) {
        if (low != null && high != null) {
            return this.filter(OperatorType.BETWEEN, field, low, high);
        }
        if (low == null && high == null) {
            return this;
        } else if (low == null) {
            return this.filterLessEqual(field, high);
        } else {
            return this.filterGreaterEqual(field, low);
        }
    }

    public ApiRequest filterIn(String field, Iterable<? extends Object> valueList) {
        return this.filter(OperatorType.IN, field, Iterables.toArray(valueList, Object.class));
    }

    public ApiRequest filterLike(String field, Object value) {
        return this.filter(OperatorType.LIKE, field, value);
    }

    public ApiRequest filterNotNull(String field) {
        return this.filter(OperatorType.NOT_NULL, field);
    }

    public ApiRequest filterNotEqual(String field, Object value) {
        return this.filter(OperatorType.NOT_EQUAL, field, value);
    }

    public ApiRequest filterOr(ApiRequestFilter... filters) {
        return this.filter(OperatorType.OR, null, (Object[]) filters);
    }

    public ApiRequest distinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public List<ApiRequestFilter> getFilterList() {
        if (filterList == null) {
            return null;
        }
        return ImmutableList.copyOf(filterList);
    }

    public boolean isDistinct() {
        return distinct;
    }
}

