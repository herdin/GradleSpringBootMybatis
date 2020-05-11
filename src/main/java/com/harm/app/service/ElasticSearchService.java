package com.harm.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harm.app.dto.model.TransModel;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticSearchService {
    private Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);
    private ObjectMapper objectMapper;
    private RestHighLevelClient restHighLevelClient;

    public ElasticSearchService(
            ObjectMapper objectMapper
            , RestHighLevelClient restHighLevelClient) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
    }

    private void getTransactionByCardNo(String cardNo) throws IOException {
        logger.debug("get transaction by card no -> {}", cardNo);
        SearchRequest searchRequest = new SearchRequest("sjb");
        /**
         *      * @deprecated Types are in the process of being removed. Instead of using a type, prefer to
         *      * filter on a field on the document.
         */
        searchRequest.types("trans");

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("card_no", cardNo)
                .lenient(false)
                .fuzziness(Fuzziness.ZERO) //연관도라고한다..
                .prefixLength(3)
                .maxExpansions(10);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);

        logger.debug("search request -> {}", searchRequest);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        logger.debug("status {}, took {} terminatedEarly {}, timeout {}", searchResponse.status(), searchResponse.getTook(), searchResponse.isTerminatedEarly(), searchResponse.isTimedOut());
        logger.debug("search response -> {}", searchResponse);

        SearchHits hits = searchResponse.getHits();
        ArrayList<TransModel> transModels = new ArrayList<>();
        for(SearchHit hit : hits) {
            String hitSource = hit.getSourceAsString();
            TransModel transModel = objectMapper.readValue(hitSource, TransModel.class);
            transModels.add(transModel);
            logger.debug("hit source -> {}", hitSource);
            logger.debug("hit -> {}", transModel);
        }
    }
}
