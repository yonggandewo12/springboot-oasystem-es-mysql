package cn.gson.oasys.utils;

import cn.gson.oasys.constants.KeyConstant;
import cn.gson.oasys.model.dao.user.UserDao;
import cn.gson.oasys.model.entity.ESDisCuss;
import cn.gson.oasys.model.entity.ESResponseList;
import cn.gson.oasys.model.entity.discuss.Discuss;
import cn.gson.oasys.model.entity.user.User;
import cn.gson.oasys.services.BaseServiceImpl;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @ClassName ESUtils
 * @Author xuliang
 * @date 2020.04.29 16:18
 */
@Data
@Component
@Slf4j
public class ESUtils {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private BaseServiceImpl baseService;

    @Autowired
    private UserDao userDao;
    /**
     * 单条数据插入
     * @param discuss
     * @return
     * @throws Exception
     */
    public BulkResponse save(Discuss discuss) throws Exception{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.type(KeyConstant.DISSCUSS_TYPE);
        indexRequest.index(KeyConstant.DISSCUSS_INDEX);
        indexRequest.source(baseService.discussToMap(discuss));
        bulkRequest.add(indexRequest);
        return restHighLevelClient.bulk(bulkRequest);
    }

    /**
     * 批量数据插入
     * @param discusses
     * @return
     * @throws Exception
     */
    public BulkResponse save(List<Discuss> discusses) throws Exception{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        discusses.forEach(discuss -> {
            IndexRequest indexRequest = new IndexRequest();
            indexRequest.type(KeyConstant.DISSCUSS_TYPE);
            indexRequest.index(KeyConstant.DISSCUSS_INDEX);
            indexRequest.source(baseService.discussToMap(discuss));
            bulkRequest.add(indexRequest);
        });
        return restHighLevelClient.bulk(bulkRequest);
    }


    /**
     * 根据disscussId删除帖子
     *
     * @param discussId
     */
    public DeleteResponse delete(Long discussId) throws Exception {
        SearchResponse searchResponse = get(discussId);
        if (searchResponse == null) {
            log.error(".....discuss_id:{}对应的帖子为空", discussId);
        }
        String _id = searchResponse.getHits().getAt(0).getId();
        DeleteRequest deleteRequest = new DeleteRequest();
        deleteRequest.type(KeyConstant.DISSCUSS_TYPE);
        deleteRequest.index(KeyConstant.DISSCUSS_INDEX);
        deleteRequest.id(_id);
        return restHighLevelClient.delete(deleteRequest);
    }

    /**
     * 根据disscussId获得帖子
     * @param diuscussId
     * @return
     * @throws Exception
     */
    public SearchResponse get(Long diuscussId) throws Exception{
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(KeyConstant.DISSCUSS_TYPE);
        searchRequest.indices(KeyConstant.DISSCUSS_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("discuss_id", diuscussId));
        searchRequest.source(searchSourceBuilder);
        return restHighLevelClient.search(searchRequest);
    }

    /**
     * 查找所有元素
     * @return
     * @throws Exception
     */
    public List<Discuss> findAll() throws Exception{
        List<Discuss> discussList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(KeyConstant.DISSCUSS_TYPE);
        searchRequest.indices(KeyConstant.DISSCUSS_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        searchResponse.getHits().iterator().forEachRemaining(searchHitFields -> {
            Discuss discuss = JSON.toJavaObject(JSON.parseObject(searchHitFields.getSourceAsString()), Discuss.class);
            discussList.add(discuss);
        });
        return discussList;
    }

    /**
     * 分页查找所有元素-根据userId
     *
     * @return
     * @throws Exception
     */
    public ESResponseList findAllAndPageByUser(Long userId, int page, int pageSize) throws Exception {
        ESResponseList esResponseList = new ESResponseList();
        List<Discuss> discussList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(KeyConstant.DISSCUSS_TYPE);
        searchRequest.indices(KeyConstant.DISSCUSS_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (userId != null) {
            searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("discuss_user_id", userId));
        }else{
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        }
        searchSourceBuilder.from(page * pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        log.info("........total:{}", searchResponse.getHits().getTotalHits());
        searchResponse.getHits().iterator().forEachRemaining(searchHitFields -> {
            ESDisCuss esDisCuss = JSON.toJavaObject(JSON.parseObject(searchHitFields.getSourceAsString()), ESDisCuss.class);
            Discuss discuss = (Discuss) BeanUtils.transfer(esDisCuss, new Discuss());
            User user = new User();
            user.setUserId(esDisCuss.getDiscussUserId());
            user.setUserName(userDao.getOne(esDisCuss.getDiscussUserId()).getUserName());
            discuss.setUser(user);
            discussList.add(discuss);
            log.info("......user={},discuss={}", user, discuss);
        });
        esResponseList.setDiscussList(discussList);
        esResponseList.setTotalSize(searchResponse.getHits().getTotalHits());
        return esResponseList;
    }

    /**
     * 分页查找所有元素-根据userId和key
     *
     * @return
     * @throws Exception
     */
    public ESResponseList findDiscussesAndPageByUserAndKey(Long userId, int page, int pageSize,String baseKey) throws Exception {
        ESResponseList esResponseList = new ESResponseList();
        List<Discuss> discussList = new ArrayList<>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(KeyConstant.DISSCUSS_TYPE);
        searchRequest.indices(KeyConstant.DISSCUSS_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (userId != null) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("discuss_user_id", userId));
        }else{
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", baseKey));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from(page * pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        searchResponse.getHits().iterator().forEachRemaining(searchHitFields -> {
            ESDisCuss esDisCuss = JSON.toJavaObject(JSON.parseObject(searchHitFields.getSourceAsString()), ESDisCuss.class);
            Discuss discuss = (Discuss) BeanUtils.transfer(esDisCuss, new Discuss());
            User user = new User();
            user.setUserId(esDisCuss.getDiscussUserId());
            user.setUserName(userDao.getOne(esDisCuss.getDiscussUserId()).getUserName());
            discuss.setUser(user);
            discussList.add(discuss);
            log.info("......discuss={}", discuss);
        });
        esResponseList.setDiscussList(discussList);
        esResponseList.setTotalSize(searchResponse.getHits().getTotalHits());
        return esResponseList;
    }


    /**
     * total
     *
     * @return
     * @throws Exception
     */
    public Long findDiscussesAndPageByUserAndKeyTotal(Long userId, int pageSize,String baseKey) throws Exception {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.types(KeyConstant.DISSCUSS_TYPE);
        searchRequest.indices(KeyConstant.DISSCUSS_INDEX);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (userId != null) {
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("discuss_user_id", userId));
        }else{
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", baseKey));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        return searchResponse.getHits().getTotalHits();
    }
}
