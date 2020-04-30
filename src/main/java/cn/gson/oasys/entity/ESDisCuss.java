package cn.gson.oasys.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @ClassName ESDisCuss
 * @Author xuliang
 * @date 2020.04.30 14:14
 */
@Data
public class ESDisCuss {

    @JSONField(name = "status_id")
    private String statusId;

    @JSONField(name = "create_time")
    private Date createTime;

    @JSONField(name = "type_id")
    private Long typeId;

    @JSONField(name = "modify_time")
    private Date modifyTime;

    @JSONField(name = "attachment_id")
    private Long attachmentId;

    @JSONField(name = "discuss_user_id")
    private Long discussUserId;

    @JSONField(name = "discuss_id")
    private Long discussId;

    @JSONField(name = "vote_id")
    private Long voteId;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "visit_num")
    private Integer visitNum;

    @JSONField(name = "content")
    private String content;

}
