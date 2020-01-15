package com.project.qa.repository.elasticsearch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ModelBase;
import com.project.qa.model.elasticserach.Question;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.ParentIdQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Service
public class ModelManager<T extends ModelBase> {

    private Supplier<T> supplier;

    protected RestHighLevelClient esClient;

    private Class<T> type;

    private Class<T> getMyType() {
        return this.type;
    }

    private ObjectMapper objectMapper;

    @Autowired
    public ModelManager(@Qualifier("esHighLevelClient") RestHighLevelClient esClient, ObjectMapper objectMapper) {
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }

    public ModelManager(Supplier<T> supplier) {
        this.supplier = supplier;
        type = (Class<T>) supplier.get().getClass();
        // esClient = new AWSElasticsearchSettings();.elasticsearchClient();
    }

    public T getByID(String id) {
        GetRequest getRequest = new GetRequest(supplier.get().getIndex().toString(), id);
        try {
            GetResponse getResponse = esClient.get(getRequest, RequestOptions.DEFAULT);
            Reader reader = new StringReader(getResponse.getSourceAsString());
            T toReturn = objectMapper.readValue(reader, this.type);
            toReturn.setId(getResponse.getId());
            return toReturn;

        } catch (IOException e) {
            e.printStackTrace();
            //To be done
            return null;
        }
    }

    public List<T> getAll() {
        SearchRequest searchRequest = new SearchRequest()
                .searchType(SearchType.DFS_QUERY_THEN_FETCH)
                .source(SearchSourceBuilder.searchSource()
                        .query(QueryBuilders.matchAllQuery()))
                .indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String index(T t) {

        Map<String, Object> modelMap = writeModelAsMap(t);
        IndexRequest indexRequest = new IndexRequest().
                index(t.getIndex().toString()).
                source(modelMap);
        if (t instanceof Answer) {
            Answer answerModel = (Answer) t;
            indexRequest.routing(answerModel.getParentId());
        }
        try {
            IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println(indexResponse.toString());
            return indexResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String update(T t) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest()
                .index(supplier.get().getIndex().toString())
                .id(t.getId())
                .doc(writeModelAsMap(t));

        UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
        return updateResponse.getId();
    }

    public List<T> moreLikeThis(String[] fieldsToSearch, String text) {
        return moreLikeThis(fieldsToSearch, text, 1, 2, 0.5);
    }

    public List<T> moreLikeThis(String[] fieldsToSearch, String text, int minTermFreq, int minDocFreq, double minScore) {

        QueryBuilder queryBuilder = new MoreLikeThisQueryBuilder(fieldsToSearch, new String[]{text,}, null).minDocFreq(minDocFreq).minTermFreq(minTermFreq);
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder)).indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<T> findByField(String field, Object value) {
        field += ".keyword";
        QueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.termQuery(field, value.toString()));
        return getModelsFromFilterRequest(boolQueryBuilder);
    }

    public List<T> filterByField(String field, List<String> terms) {
        field += ".keyword";
        QueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.termsQuery(field, terms));
        return getModelsFromFilterRequest(boolQueryBuilder);

    }

    public List<T> matchLikeThis(String field, String value) {
        QueryBuilder boolQueryBuilder = QueryBuilders.matchQuery(field, value);
        return getModelsFromFilterRequest(boolQueryBuilder);
    }

    public void delete(T t) throws IOException {
        delete(t.getId());
    }

    public void delete(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest().id(id).index(this.supplier.get().getIndex().toString());
        DeleteResponse deleteResponse = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    public void loadAnswers(Question model) {
        ParentIdQueryBuilder queryBuilder = new ParentIdQueryBuilder("answer", model.getId());
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder)).indices(model.getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHit[] hits = searchResponse.getHits().getHits();
            ArrayList<Answer> answers = new ArrayList<>();
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                Reader reader = new StringReader(json);
                Answer toAppend = objectMapper.readValue(reader, Answer.class);
                toAppend.setId(hit.getId());
                answers.add(toAppend);
            }
            model.setQuestionsAnswers(answers);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<T> getModelsFromFilterRequest(QueryBuilder boolQueryBuilder) {
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query((boolQueryBuilder))).indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<T> getModelsFromHits(SearchHit[] hits) throws IOException {
        return getModelsFromHits(hits, 0);
    }

    private List<T> getModelsFromHits(SearchHit[] hits, double minScore) throws IOException {

        ArrayList<T> toRerun = new ArrayList<>();
        for (SearchHit hit : hits) {
            if (hit.getScore() < minScore) {
                continue;
            }

            String json = hit.getSourceAsString();
            Reader reader = new StringReader(json);
            T toAppend = objectMapper.readValue(reader, this.type);
            toAppend.setId(hit.getId());
            toRerun.add(toAppend);
        }
        return toRerun;
    }

    public Map<String, Object> writeModelAsMap(T t) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        return objectMapper.convertValue(t, new TypeReference<HashMap<String, Object>>() {});
    }
}

