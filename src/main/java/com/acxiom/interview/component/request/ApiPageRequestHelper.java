package com.acxiom.interview.component.request;

import com.acxiom.interview.request.ApiRequest;
import com.acxiom.interview.request.ApiRequestPage;
import com.acxiom.interview.response.ApiResponse;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author wangzhiliang
 */
public class ApiPageRequestHelper {

    public static <T> List<T> request(ApiRequest apiRequest, ApiRequestPage apiRequestPage, BiFunction<ApiRequest, ApiRequestPage, ApiResponse<T>> biFunction) throws RuntimeException {
        return request(apiRequest, apiRequestPage, biFunction, 0);
    }

    public static <T> List<T> request(ApiRequest apiRequest, ApiRequestPage apiRequestPage, BiFunction<ApiRequest, ApiRequestPage, ApiResponse<T>> biFunction, int maxPagingCount) throws RuntimeException {
        List<T> result = Lists.newArrayList();

        while (true) {
            ApiResponse<T> apiResponse = biFunction.apply(apiRequest, apiRequestPage);

            if (apiResponse == null || apiResponse.getCount() == 0) {
                break;
            }

            result.addAll(apiResponse.getPagedData());

            if (apiResponse.getCount() < apiRequestPage.getPageSize()) {
                break;
            }

            apiRequestPage.pagingNext();

            if (maxPagingCount > 0 && apiRequestPage.getPage() >= maxPagingCount) {
                // 达到最大翻页次数
                break;
            }
        }

        return result;
    }
}
