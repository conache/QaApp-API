package com.project.qa.amazonaws.http;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class AWSElasticsearchSettings {

    private static String serviceName = "es";
    private static String region = "us-east-2";
    private static String aesEndpoint = "http://search-notstackoverflowsearch-ana2xbx6kdnqfewfowo6k4k4em.us-east-2.es.amazonaws.com"; // e.g. https://search-mydomain.us-west-1.es.amazonaws.com
    private static String type = "_doc";
    private static String id = "visualization:37cc8650-b882-11e8-a6d9-e546fe2bba5f";

    public static RestHighLevelClient esClient() {

        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA2ZCDFQHXSPGJ2N6C", "4sUuIKEqzk0GLLlx1/y9BILWtC+Hp1R6/zE4e2yL");
        AWSStaticCredentialsProvider credentialsProvider =  new  AWSStaticCredentialsProvider(awsCreds);
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return new RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }

    public static RestClient esRestClient() {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials("AKIA2ZCDFQHXSPGJ2N6C", "4sUuIKEqzk0GLLlx1/y9BILWtC+Hp1R6/zE4e2yL");
        AWSStaticCredentialsProvider credentialsProvider =  new  AWSStaticCredentialsProvider(awsCreds);
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(serviceName);
        signer.setRegionName(region);
        HttpRequestInterceptor interceptor = new AWSRequestSigningApacheInterceptor(serviceName, signer, credentialsProvider);
        return RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)).build();
    }
}


