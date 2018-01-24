package com.acxiom.interview.controller;

import com.acxiom.interview.enums.OperatorType;
import com.acxiom.interview.enums.PageOrderType;
import com.acxiom.interview.exception.SearchException;
import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestFilter;
import com.acxiom.interview.request.ApiRequestOrder;
import com.acxiom.interview.request.ApiRequestPage;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public abstract class BaseRestController extends BaseController {
    protected static final String PARAM_QUERY = "query";

    private static final String PARAM_FILTER_JSON_KEY = "filter";
    private static final String PARAM_SORT_JSON_KEY = "sort";
    private static final String PARAM_OFFSET_JSON_KEY = "offset";

    protected static final String PARAM_ROWS_JSON_KEY = "rows";

    protected static final String ERROR_CODE_EMPTY_PARAM = "500001";
    protected static final String ERROR_CODE_PATTERN_ERROR = "500002";

    protected ApiRequest convertRequest(JsonNode queryNode) throws SearchException {
        ApiRequest apiRequest = ApiRequest.newInstance();

        JsonNode filterNode = queryNode.get(PARAM_FILTER_JSON_KEY);

        if (filterNode != null && filterNode.isArray()) {
            for (JsonNode jsonNode : filterNode) {
                ApiRequestFilter filter = convertFilter(jsonNode);
                if (filter.getValueList() != null && !filter.getValueList().isEmpty()) {
                    apiRequest.filter(filter.getOperatorType(), filter.getField(), filter.getValueList().toArray(new Object[0]));
                } else if (filter.getValue() != null) {
                    apiRequest.filter(filter.getOperatorType(), filter.getField(), filter.getValue());
                } else {
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }
            }
        }
        return apiRequest;
    }

    protected ApiRequestPage convertRequestPage(JsonNode queryNode) throws SearchException {
        ApiRequestPage apiRequestPage = ApiRequestPage.newInstance();
        JsonNode sortNode = queryNode.get(PARAM_SORT_JSON_KEY);
        if (sortNode != null && sortNode.isArray()) {
            for (JsonNode jsonNode : sortNode) {
                ApiRequestOrder order = convertSort(jsonNode);
                apiRequestPage.addOrder(order.getField(), order.getOrderType());
            }
        }

        //偏移量作为每页的记录数 模拟elasticsearch from操作
        //elasticsearch from : apiRequestPage.getPage() * apiRequestPage.getPageSize();
        //elasticsearch size : apiRequestPage.pageSize
        int pageSize = apiRequestPage.getPageSize();
        int page = 0;
        JsonNode offsetNode = queryNode.get(PARAM_OFFSET_JSON_KEY);
        if (offsetNode != null){
            if(!offsetNode.isValueNode()) {
                throw new SearchException(ERROR_CODE_PATTERN_ERROR, "请求参数格式错误");
            }
            int offset = offsetNode.asInt();
            if (offset > 0) {
                pageSize = offset;
                page++;
            }
        }
        apiRequestPage.paging(page, pageSize);

        return apiRequestPage;
    }

    private ApiRequestFilter convertFilter(JsonNode filterNode) throws SearchException {
        ApiRequestFilter apiRequestFilter = new ApiRequestFilter();
        Iterator<String> filterFields = filterNode.fieldNames();
        String key = filterFields.next();
        if (key == null) {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
        }
        switch (key) {
            case "OR": {
                JsonNode orNode = filterNode.get(key);
                if (orNode == null || !orNode.isArray()) { // or 操作必须多于1个条件
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR, "请求参数格式错误");
                }

                apiRequestFilter.setOperatorType(OperatorType.OR);
                List<Object> values = Lists.newArrayList();
                for (JsonNode jsonNode : orNode) {
                    values.add(convertFilter(jsonNode));
                }
                apiRequestFilter.setValueList(values);
                break;
            } case "AND": {
                JsonNode andNode = filterNode.get(key);
                if (andNode == null || !andNode.isArray()) { // and 操作必须多于1个条件
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }

                apiRequestFilter.setOperatorType(OperatorType.AND);
                List<Object> values = Lists.newArrayList();
                for (JsonNode jsonNode : andNode) {
                    values.add(convertFilter(jsonNode));
                }
                apiRequestFilter.setValueList(values);
                break;
            } case "NOT": {
                JsonNode notNode = filterNode.get(key);
                if (notNode == null) {
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }

                apiRequestFilter.setOperatorType(OperatorType.NOT);
                List<Object> values = Lists.newArrayList();
                for (JsonNode jsonNode : notNode) {
                    values.add(convertFilter(jsonNode));
                }
                apiRequestFilter.setValueList(values);
                break;
            }case "GTE": {
                JsonNode gteNode = filterNode.get(key);
                if (gteNode == null || gteNode.isArray()) { // gte 操作只能有1个条件
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }

                apiRequestFilter.setOperatorType(OperatorType.GE);
                Iterator<String> fields = gteNode.fieldNames();
                String gteKey = fields.next();
                apiRequestFilter.setField(gteKey);
                JsonNode gteNodeValue = gteNode.get(gteKey);
                if(!gteNodeValue.isValueNode()) {
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }
                if(gteNodeValue.isInt()) {
                    apiRequestFilter.setValue(gteNodeValue.asInt());
                } else if (gteNodeValue.isLong()) {
                    apiRequestFilter.setValue(gteNodeValue.asLong());
                } else {
                    apiRequestFilter.setValue(gteNodeValue.asText());
                }
                break;
            } case "LTE": {
                JsonNode lteNode = filterNode.get(key);
                if (lteNode == null || lteNode.isArray()) { // lte 操作只能有1个条件
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }

                apiRequestFilter.setOperatorType(OperatorType.LE);
                Iterator<String> fields = lteNode.fieldNames();
                String lteKey = fields.next();
                apiRequestFilter.setField(lteKey);
                JsonNode lteNodeValue = lteNode.get(lteKey);
                if(!lteNodeValue.isValueNode()) {
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }
                if(lteNodeValue.isInt()) {
                    apiRequestFilter.setValue(lteNodeValue.asInt());
                } else if (lteNodeValue.isLong()) {
                    apiRequestFilter.setValue(lteNodeValue.asLong());
                } else {
                    apiRequestFilter.setValue(lteNodeValue.asText());
                }
                break;
            } default: {
                JsonNode eqNode = filterNode.get(key);
                apiRequestFilter.setOperatorType(OperatorType.EQ);
                if(!eqNode.isValueNode()) {
                    throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
                }
                apiRequestFilter.setField(key);
                if(eqNode.isInt()) {
                    apiRequestFilter.setValue(eqNode.asInt());
                } else if (eqNode.isLong()) {
                    apiRequestFilter.setValue(eqNode.asLong());
                } else {
                    apiRequestFilter.setValue(eqNode.asText());
                }
            }
        }
        return apiRequestFilter;
    }

    private ApiRequestOrder convertSort(JsonNode sortNode) throws SearchException {
        Iterator<String> sortFields = sortNode.fieldNames();
        String key = sortFields.next();
        if (key == null) {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
        }
        JsonNode eqNode = sortNode.get(key);
        if(!eqNode.isValueNode()) {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
        }
        String order = eqNode.asText();

        ApiRequestOrder apiRequestOrder;
        if("asc".equals(order)) {
            apiRequestOrder = new ApiRequestOrder(key, PageOrderType.ASC);
        }else if("desc".equals(order)) {
            apiRequestOrder = new ApiRequestOrder(key, PageOrderType.DESC);
        } else {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR,"不支持的排序方式");
        }
        return apiRequestOrder;
    }
}
