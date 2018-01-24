package com.acxiom.interview.service.user;

import com.acxiom.interview.domain.elasticsearch.user.UserIndex;
import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestPage;
import com.acxiom.interview.response.ApiResponse;
import com.acxiom.interview.exception.user.UserIndexException;
import com.acxiom.interview.service.BaseApiService;

/**
 * 用户搜索操作接口
 * @author wangzhiliang
 */
public interface UserIndexApiService extends BaseApiService {
    /**
     * 商品索引信息查询
     * @param request 自定义组合查询条件
     * @param requestPage 分页和排序条件
     * @return 分页组织的商品信息查询列表
     * @throws UserIndexException
     */
    ApiResponse<UserIndex> findAll(ApiRequest request, ApiRequestPage requestPage) throws UserIndexException;
}
