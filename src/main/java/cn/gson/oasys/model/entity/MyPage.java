package cn.gson.oasys.model.entity;

import cn.gson.oasys.model.entity.discuss.Discuss;
import lombok.Data;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;

/**
 * @Description
 * @ClassName MyPage
 * @Author xuliang
 * @date 2020.04.30 12:13
 */
@Data
public class MyPage implements Page<Discuss> {

    private List<Discuss> discussList;
    private Integer thisPage;
    private Integer totalPages;
    private Long total;

    public MyPage() {

    }

    public MyPage(List<Discuss> discussList, int thisPage, int totalPages, long total) {
        this.total = total;
        this.totalPages = totalPages;
        this.thisPage = thisPage;
        this.discussList = discussList;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public long getTotalElements() {
        return total;
    }

    @Override
    public int getNumber() {
        return thisPage;
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public int getNumberOfElements() {
        return 0;
    }

    @Override
    public List<Discuss> getContent() {
        return discussList;
    }

    @Override
    public boolean hasContent() {
        return discussList != null && discussList.size() != 0;
    }

    @Override
    public Sort getSort() {
        return null;
    }

    @Override
    public boolean isFirst() {
        return thisPage == 0;
    }

    @Override
    public boolean isLast() {
        return thisPage == (totalPages - 1);
    }

    @Override
    public boolean hasNext() {
        if (isLast()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean hasPrevious() {
        if (isFirst()) {
            return false;
        }
        return true;
    }

    @Override
    public Pageable nextPageable() {
        if (hasNext()) {
            Pageable pegeable = new PageRequest(thisPage + 1, 10);
            return pegeable;
        }
        return null;
    }

    @Override
    public Pageable previousPageable() {
        Pageable pegeable = new PageRequest(thisPage - 1, 10);
        return pegeable;
    }

    @Override
    public <S> Page<S> map(Converter<? super Discuss, ? extends S> converter) {
        return null;
    }

    @Override
    public Iterator<Discuss> iterator() {
        return discussList.iterator();
    }
}
