package com.acxiom.interview.response;

import com.acxiom.interview.request.ApiRequestPage;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 * 封装API响应对象供自定义查询使用
 * 支持分页
 * @author wangzhiliang
 */
public class ApiResponse<E> implements Serializable {

    private static final long serialVersionUID = 2490364023692403771L;
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /* 回写请求时的分页设置 */
    private int page;
    private int pageSize;

    /* 满足条件的记录总数 */
    private long total = 0;

    private Collection<E> pagedData;

    public ApiResponse(int page, int pageSize, Collection<E> pagedData, long total) {
        this.page = page;
        this.pageSize = pageSize;
        this.pagedData = pagedData;
        this.total = total;
    }

    public ApiResponse(int page, int pageSize, Collection<E> pagedData) {
        this(page, pageSize, pagedData, 0);
    }

    public ApiResponse(int page, int pageSize) {
        this(page, pageSize, null);
    }

    public ApiResponse(ApiRequestPage requestPage) {
        this(requestPage.getPage(), requestPage.getPageSize());
    }

    public ApiResponse() {
    }

    public int getCount() {
        if (pagedData == null) {
            return 0;
        }

        return pagedData.size();
    }

    public int getPageTotal() {
        if (total <= 0) {
            return 0;
        }

        return (int) Math.ceil( (double) total / pageSize);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Collection<E> getPagedData() {
        if (pagedData == null) {
            pagedData = Lists.newArrayList();
        }
        return pagedData;
    }

    public void setPagedData(Collection<E> pagedData) {
        this.pagedData = pagedData;
    }
}

