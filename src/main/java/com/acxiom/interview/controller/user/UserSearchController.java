package com.acxiom.interview.controller.user;

import com.acxiom.interview.component.request.ApiPageRequestHelper;
import com.acxiom.interview.controller.BaseRestController;
import com.acxiom.interview.domain.elasticsearch.user.UserIndex;
import com.acxiom.interview.exception.SearchException;
import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestPage;
import com.acxiom.interview.response.ApiResponse;
import com.acxiom.interview.service.user.UserIndexApiService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangzhiliang
 */
@RestController
@RequestMapping("/user/search")
public class UserSearchController extends BaseRestController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserIndexApiService userIndexApiService;

    @ResponseBody
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String userSearch(HttpServletRequest request) {
        String queryStr = request.getParameter(PARAM_QUERY);

        if (StringUtils.isEmpty(queryStr)) {
            throw new SearchException(ERROR_CODE_EMPTY_PARAM, "请求参数为空");
        }

        JsonNode queryNode;
        try {
            queryNode = objectMapper.readTree(queryStr);
        } catch (IOException e) {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR,"请求参数格式错误");
        }

        if(queryNode == null) {
            throw new SearchException(ERROR_CODE_PATTERN_ERROR, "请求参数格式错误");
        }

        ApiRequest apiRequest = convertRequest(queryNode);
        ApiRequestPage apiRequestPage = convertRequestPage(queryNode);

        //需要获取的记录数
        int rows = 0;
        JsonNode rowsNode = queryNode.get(PARAM_ROWS_JSON_KEY);
        if (rowsNode != null){
            if(!rowsNode.isValueNode()) {
                throw new SearchException(ERROR_CODE_PATTERN_ERROR, "请求参数格式错误");
            }
            rows = rowsNode.asInt();
            if (rows < 0) {
                throw new SearchException(ERROR_CODE_PATTERN_ERROR, "请求参数格式错误");
            }
        }

        if (rows == 0) {
            rows = apiRequestPage.getPageSize();
        }

        int offsetPage = 0;
        if (apiRequestPage.getPageSize() >= rows) { //每次获取的记录数大于需要获取的记录数
            offsetPage = 1;
        } else {
            offsetPage = rows / apiRequestPage.getPageSize() + 1;
        }

        int maxPage = apiRequestPage.getPage() + offsetPage;

        List<UserIndex> response = ApiPageRequestHelper.request(apiRequest, apiRequestPage, userIndexApiService::findAll, maxPage);
        if (!response.isEmpty() && response.size() > rows) {
            response = response.subList(0, rows);
        }

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new SearchException("转换查询结果错误");
        }
    }
}
