package cn.momia.common.api.dto;

import java.util.ArrayList;
import java.util.List;

public class PagedList<T> {
    private long totalCount;
    private Integer nextIndex;
    private List<T> list = new ArrayList<T>();

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getNextIndex() {
        return nextIndex;
    }

    public void setNextIndex(Integer nextIndex) {
        this.nextIndex = nextIndex;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public PagedList() {}

    public PagedList(long totalCount, int start, int count) {
        this.totalCount = totalCount;
        if (start + count < totalCount) this.nextIndex = start + count;
    }
}
