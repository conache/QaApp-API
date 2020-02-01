package com.project.qa.repository.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.project.qa.model.elasticserach.Answer;
import com.project.qa.model.elasticserach.ModelBase;
import com.project.qa.model.elasticserach.Question;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanQuery;
import org.javatuples.Pair;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.join.query.ParentIdQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ModelManager<T extends ModelBase> {

    private Supplier<T> supplier;

    protected RestHighLevelClient esClient;

    private Class<T> type;

    private Class<T> getMyType() {
        return this.type;
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    public ModelManager(Supplier<T> supplier, RestHighLevelClient esClient) {
        this.supplier = supplier;
        type = (Class<T>) supplier.get().getClass();
        this.esClient = esClient;
    }

    public T getByID(String id) {

        GetRequest getRequest = new GetRequest(supplier.get().getIndex().toString(), id);
        return getModelsFromGetRequest(getRequest);
    }

    public T getByID(String id, String route) {

        GetRequest getRequest = new GetRequest(supplier.get().getIndex().toString(), id).routing(route);
        return getModelsFromGetRequest(getRequest);
    }

    private T getModelsFromGetRequest(GetRequest getRequest) {
        try {

            GetResponse getResponse = esClient.get(getRequest, RequestOptions.DEFAULT);
            ObjectMapper oMapper = new ObjectMapper();
            Reader reader = new StringReader(getResponse.getSourceAsString());
            T toReturn = oMapper.readValue(reader, this.type);
            toReturn.setModelId(getResponse.getId());
            return toReturn;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public Pair<List<T>, Long> getAll(int size, int from, String groupName) {

        return getAll(size, from, groupName, supplier.get().getSortBy());
    }

    public Pair<List<T>, Long> getAll(int size, int from, String groupName, String sortBy) {
        QueryBuilder boolQueryBuilder = new BoolQueryBuilder().must(QueryBuilders.termsQuery("groupName.keyword", groupName)).must(QueryBuilders.termQuery("modelType.keyword", this.supplier.get().getModelType()));
        return getModelsFromFilterRequest(boolQueryBuilder, size, from, sortBy);
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

    public String update(T model) {
        UpdateRequest updateRequest = new UpdateRequest().
                index(supplier.get().getIndex().toString()).
                id(model.getModelId()).
                doc(writeModelAsMap(model));

        try {
            UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String update(T model, String route) {
        UpdateRequest updateRequest = new UpdateRequest().
                index(supplier.get().getIndex().toString()).
                id(model.getModelId()).
                doc(writeModelAsMap(model)).
                routing(route);

        try {
            UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

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

    public Pair<List<T>, Long> findByField(String field, Object value, int size, int from, String groupName) {
        return findByField(field, value, size, from, groupName, supplier.get().getSortBy());
    }

    public Pair<List<T>, Long> findByField(String field, Object value, int size, int from, String groupName, String sortBy) {
        field += ".keyword";
        QueryBuilder boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.termQuery(field, value.toString()))
                .must(QueryBuilders.termsQuery("groupName.keyword", groupName))
                .must(QueryBuilders.termQuery("modelType.keyword", this.supplier.get().getModelType()));
        return getModelsFromFilterRequest(boolQueryBuilder, size, from, sortBy);
    }

    public Pair<List<T>, Long> filterByField(String field, List<String> terms, int size, int from, String groupName) {

        return filterByField(field, terms, size, from, groupName, supplier.get().getSortBy());
    }

    public Pair<List<T>, Long> filterByField(String field, List<String> terms, int size, int from, String groupName, String sortBy) {

        field += ".keyword";
        QueryBuilder boolQueryBuilder = new BoolQueryBuilder()
                .filter(QueryBuilders.termsQuery(field, terms))
                .must(QueryBuilders.termsQuery("groupName.keyword", groupName))
                .must(QueryBuilders.termQuery("modelType.keyword", this.supplier.get().getModelType()));
        return getModelsFromFilterRequest(boolQueryBuilder, size, from, sortBy);

    }

    public Pair<List<T>, Long> matchLikeThis(String field, String value, int size, int from, String groupName) {
        QueryBuilder boolQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(field, value.toString())).must(QueryBuilders.termsQuery("groupName.keyword", groupName)).must(QueryBuilders.termQuery("modelType.keyword", this.supplier.get().getModelType()));
        return getModelsFromFilterRequest(boolQueryBuilder, size, from, "_score");
    }

    public void delete(T model) {
        delete(model.getModelId());
    }

    public void delete(String id) {
        DeleteRequest deleteRequest = new DeleteRequest().id(id).index(this.supplier.get().getIndex().toString());
        try {
            DeleteResponse deleteResponse = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(deleteResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void delete(String id, String route) {
        DeleteRequest deleteRequest = new DeleteRequest().id(id).index(this.supplier.get().getIndex().toString()).routing(route);
        try {
            DeleteResponse deleteResponse = esClient.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println(deleteResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAnswers(Question model) {

        ParentIdQueryBuilder queryBuilder = new ParentIdQueryBuilder("answer", model.getModelId());
        Answer answer = new Answer.AnswerBuilder().build();
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder).sort(answer.getSortBy(), SortOrder.DESC)).indices(model.getIndex().toString());
        try {
            model.setQuestionsAnswers(GetAnswers(searchRequest));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<List<Answer>, Long> getAnswersForQuestion(String id, int size, int from, String sortBy) {

        Question model = new Question.QuestionBuilder().build();
        Answer answer = new Answer.AnswerBuilder().build();
        ParentIdQueryBuilder queryBuilder = new ParentIdQueryBuilder("answer", id);


        CountRequest countRequest = new CountRequest().source(SearchSourceBuilder.
                searchSource().
                query(queryBuilder)).
                indices(model.getIndex().toString());


        CountResponse countResponse;
        try {
            countResponse = esClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        }

        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder).size(size).from(from).sort(sortBy, SortOrder.DESC)).indices(model.getIndex().toString());

        try {
            return new Pair<>(GetAnswers(searchRequest), countResponse.getCount());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Answer> GetAnswers(SearchRequest searchRequest) throws IOException {

        SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
        ObjectMapper oMapper = new ObjectMapper();
        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<Answer> answers = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Reader reader = new StringReader(json);
            Answer toAppend = oMapper.readValue(reader, Answer.class);
            toAppend.setModelId(hit.getId());
            answers.add(toAppend);
        }
        return answers;
    }

    private Pair<List<T>, Long> getModelsFromFilterRequest(QueryBuilder boolQueryBuilder, int size, int from, String sortBy) {
        T model = supplier.get();


        CountRequest countRequest = new CountRequest().source(SearchSourceBuilder.
                searchSource().
                query(boolQueryBuilder)).
                indices(model.getIndex().toString());


        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.
                searchSource().
                query((boolQueryBuilder)).
                size(size).
                from(from).
                sort(sortBy, SortOrder.DESC)).
                indices(model.getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            List<T> models = getModelsFromHits(searchResponse.getHits().getHits());

            CountResponse countResponse = esClient.count(countRequest, RequestOptions.DEFAULT);

            return new Pair<>(models, countResponse.getCount());

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
            toAppend.setModelId(hit.getId());
            toRerun.add(toAppend);
        }
        return toRerun;
    }

    public Map<String, Object> writeModelAsMap(T t) {
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        return objectMapper.convertValue(t, new TypeReference<HashMap<String, Object>>() {
        });

    }
}

