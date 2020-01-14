package com.project.qa.repository.elasticsearch;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;

import com.project.qa.amazonaws.http.AWSElasticsearchSettings;
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

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

public class ModelManager<Model extends ModelBase> {

    private Supplier<Model> supplier;

    protected RestHighLevelClient esClient;

    private Class<Model> type;

    private Class<Model> getMyType() {
        return this.type;
    }

    public ModelManager(Supplier<Model> supplier) {
        this.supplier = supplier;
        type = (Class<Model>) supplier.get().getClass();
        esClient = AWSElasticsearchSettings.esClient();
    }

    public Model getByID(String id) {
        GetRequest getRequest = new GetRequest(supplier.get().getIndex().toString(), id);
        try {
            GetResponse getResponse = esClient.get(getRequest, RequestOptions.DEFAULT);
            ObjectMapper oMapper = new ObjectMapper();
            Reader reader = new StringReader(getResponse.getSourceAsString());
            Model toReturn = oMapper.readValue(reader, this.type);
            toReturn.setId(getResponse.getId());
            return toReturn;

        } catch (IOException e) {
            e.printStackTrace();
            //To be done
            return null;
        }
    }

    public List<Model> getAll() {

        SearchRequest searchRequest = new SearchRequest().
                searchType(SearchType.DFS_QUERY_THEN_FETCH).
                source(SearchSourceBuilder.searchSource().
                        query(QueryBuilders.matchAllQuery())).
                indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String index(Model model) {

        Map<String, Object> modelMap = writeModelAsMap(model);
        IndexRequest indexRequest = new IndexRequest().
                index(model.getIndex().toString()).
                source(modelMap);
        if (model instanceof Answer) {
            Answer answerModel = (Answer) model;
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

    public String update(Model model) {
        UpdateRequest updateRequest = new UpdateRequest().
                index(supplier.get().getIndex().toString()).
                id(model.getId()).
                doc(writeModelAsMap(model));
        try {
            UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
            return updateResponse.getId();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public List<Model> moreLikeThis(String[] fieldsToSearch, String text ){

        return  moreLikeThis(fieldsToSearch, text, 1, 2, 0.5);
    }

    public List<Model> moreLikeThis(String[] fieldsToSearch, String text, int minTermFreq, int minDocFreq, double minScore ) {

        QueryBuilder queryBuilder = new MoreLikeThisQueryBuilder(fieldsToSearch, new String[]{text,},null).minDocFreq(minDocFreq).minTermFreq(minTermFreq);
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder)).indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Model> findByField(String field, Object value)  {
        field += ".keyword";
        QueryBuilder  boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.termQuery(field,value.toString()));
        return getModelsFromFilterRequest(boolQueryBuilder);
    }

    public List<Model> filterByField(String field, List<String> terms) {

        field += ".keyword";
        QueryBuilder  boolQueryBuilder = new BoolQueryBuilder().filter(QueryBuilders.termsQuery(field,terms));
        return getModelsFromFilterRequest(boolQueryBuilder);

    }

    public List<Model> matchLikeThis(String field, String value)
    {
        QueryBuilder  boolQueryBuilder = QueryBuilders.matchQuery(field,value.toString());
        return getModelsFromFilterRequest(boolQueryBuilder);
    }

    public void delete(Model model){
        delete(model.getId());
    }

    public void delete(String id) {
        DeleteRequest deleteRequest = new DeleteRequest().id(id).index(this.supplier.get().getIndex().toString());
        try {
            DeleteResponse deleteResponse = esClient.delete(deleteRequest,RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAnswers(Question model) {

        ParentIdQueryBuilder queryBuilder = new ParentIdQueryBuilder("answer",model.getId());
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query(queryBuilder)).indices(model.getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            ObjectMapper oMapper = new ObjectMapper();
            SearchHit[] hits = searchResponse.getHits().getHits();
            ArrayList<Answer> answers = new ArrayList<>();
            for (SearchHit hit : hits) {
                String json = hit.getSourceAsString();
                Reader reader = new StringReader(json);
                Answer toAppend = oMapper.readValue(reader, Answer.class);
                toAppend.setId(hit.getId());
                answers.add(toAppend);
            }
            model.setQuestionsAnswers(answers);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private List<Model> getModelsFromFilterRequest(QueryBuilder boolQueryBuilder) {
        SearchRequest searchRequest = new SearchRequest().source(SearchSourceBuilder.searchSource().query((boolQueryBuilder))).indices(supplier.get().getIndex().toString());
        try {
            SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
            return getModelsFromHits(searchResponse.getHits().getHits());
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private List<Model> getModelsFromHits(SearchHit[] hits) throws IOException {
        return getModelsFromHits(hits,0);
    }

    private List<Model> getModelsFromHits(SearchHit[] hits, double minScore) throws IOException {

        ObjectMapper oMapper = new ObjectMapper();
        ArrayList<Model> toRerun = new ArrayList<Model>();
        for (SearchHit hit : hits) {

            if(hit.getScore() < minScore)
            {
                continue;
            }

            String json = hit.getSourceAsString();
            Reader reader = new StringReader(json);
            Model toAppend = oMapper.readValue(reader, this.type);
            toAppend.setId(hit.getId());
            toRerun.add(toAppend);
        }
        return toRerun;
    }

    public Map<String, Object> writeModelAsMap(Model model) {
        ObjectMapper oMapper = new ObjectMapper();
        oMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        Map<String, Object> map = oMapper.convertValue(model, Map.class);
        return map;
    }
}

