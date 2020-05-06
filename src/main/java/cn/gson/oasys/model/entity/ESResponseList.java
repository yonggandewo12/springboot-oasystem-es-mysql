package cn.gson.oasys.model.entity;

import cn.gson.oasys.model.entity.discuss.Discuss;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @ClassName ESResponseList
 * @Author xuliang
 * @date 2020.05.06 09:37
 */
@Data
public class ESResponseList implements Serializable {
    private Long totalSize;
    private List<Discuss> discussList;
}
