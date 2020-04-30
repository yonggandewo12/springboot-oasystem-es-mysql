package cn.gson.oasys.services;

import cn.gson.oasys.model.entity.discuss.Discuss;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @ClassName BaseServiceImpl
 * @Author xuliang
 * @date 2020.04.29 15:47
 */
@Service
public class BaseServiceImpl {

    public Map discussToMap(Discuss discuss) {
        Map map = new HashMap();
        if (discuss != null) {
            map.put("discuss_id",discuss.getDiscussId());
            map.put("type_id",discuss.getTypeId());
            map.put("status_id",discuss.getStatusId());
            map.put("create_time",discuss.getCreateTime());
            map.put("modify_time",discuss.getModifyTime());
            map.put("visit_num",discuss.getVisitNum());
            map.put("attachment_id",discuss.getAttachmentId());
            map.put("title",discuss.getTitle());
            map.put("content",discuss.getContent());
            map.put("discuss_user_id",discuss.getUser().getUserId());
            if (discuss.getVoteList()!= null) {
                map.put("vote_id", discuss.getVoteList().getVoteId());
            }
        }
        return map;
    }
}
