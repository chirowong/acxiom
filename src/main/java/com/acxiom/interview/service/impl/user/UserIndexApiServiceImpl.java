package com.acxiom.interview.service.impl.user;

import com.acxiom.interview.domain.elasticsearch.user.UserIndex;
import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestPage;
import com.acxiom.interview.response.ApiResponse;
import com.acxiom.interview.exception.user.UserIndexException;
import com.acxiom.interview.repository.elasticsearch.user.UserIndexRepository;
import com.acxiom.interview.service.impl.AbstractElasticsearchApiServiceImpl;
import com.acxiom.interview.service.user.UserIndexApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author wangzhiliang
 */
@Service("userIndexApiService")
public class UserIndexApiServiceImpl extends AbstractElasticsearchApiServiceImpl implements UserIndexApiService {
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserIndexRepository userIndexRepository;

    @Override
    public ApiResponse<UserIndex> findAll(ApiRequest request, ApiRequestPage requestPage) throws UserIndexException {
        Page<UserIndex> page = userIndexRepository.search(convertQueryBuilder(request), convertPageable(requestPage));
        return new ApiResponse<>(requestPage.getPage(), requestPage.getPageSize(), page.getContent(), page.getTotalElements());
    }
}
