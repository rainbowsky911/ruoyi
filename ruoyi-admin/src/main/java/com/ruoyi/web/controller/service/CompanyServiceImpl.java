package com.ruoyi.web.controller.service;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HeFeiCompanyAdvice;
import com.ruoyi.web.PageResult;
import com.ruoyi.web.RequestParams;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zdw
 * @Date: 2022/5/27 10:40
 * @Description:
 */
@Service
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private RestHighLevelClient client;
    //ES索引名称
    private final static String INDEX_NAME = "company";

    @Override
    public PageResult listCompany(RequestParams params) {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(params.getTitle())) {
            boolQuery.must(QueryBuilders.matchPhraseQuery("title", params.getTitle()));
        }else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        int pageNum = params.getPageNum();
        int pageSize = params.getPageSize();

        request.source().from((pageNum - 1) * pageSize).size(pageSize);

        if (params.getSortType() == 1) {
            request.source().sort("commentCount", SortOrder.DESC);
        }else {
            request.source().sort("createTime", SortOrder.DESC)
                    .sort("commentCount", SortOrder.DESC);
        }


        request.source().query(boolQuery);
        SearchResponse search = null;
        try {
            search = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<HeFeiCompanyAdvice> list = new ArrayList<>();
        SearchHits hits = search.getHits();
        long total = hits.getTotalHits().value;
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HeFeiCompanyAdvice company = JSON.parseObject(json, HeFeiCompanyAdvice.class);
            list.add(company);
        }
        return new PageResult(total, list, list);
    }


    @Override
    public List<String> getSuggestion(String key) {
        try {
            SearchRequest request = new SearchRequest(INDEX_NAME);

            request.source().suggest(new SuggestBuilder()
                    .addSuggestion("suggestions",
                            SuggestBuilders.completionSuggestion("suggestion")
                                    .prefix(key)
                                    .skipDuplicates(true)
                                    .size(5)));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            log.info("DSL语句为：{}",request.source().toString());

            Suggest suggestions = response.getSuggest();
            CompletionSuggestion suggestion = suggestions.getSuggestion("suggestions");
            List<String> list = new ArrayList<>();
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();

            for (CompletionSuggestion.Entry.Option option : options) {
                String str = option.getText().toString();
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
