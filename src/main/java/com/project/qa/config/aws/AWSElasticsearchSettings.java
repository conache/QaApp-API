package com.project.qa.config.aws;

import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class AWSElasticsearchSettings {

    private final AwsCredentials credentials;

    @Autowired
    public AWSElasticsearchSettings(AwsCredentials credentials) {
        this.credentials = credentials;
    }

    @Bean
    @Qualifier("esHighLevelClient")
    public RestHighLevelClient elasticsearchClient() {
        HttpRequestInterceptor interceptor = getAwsHttpRequestInterceptor();
        return new RestHighLevelClient(RestClient
                .builder(HttpHost.create(credentials.getUrl()))
                .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor)));
    }

    @Bean
    @Qualifier("esCLient")
    public RestClient esRestClient() {
        HttpRequestInterceptor interceptor = getAwsHttpRequestInterceptor();
        return RestClient
                .builder(HttpHost.create(credentials.getUrl()))
                .setHttpClientConfigCallback(hacb -> hacb.addInterceptorLast(interceptor))
                .build();
    }

    private HttpRequestInterceptor getAwsHttpRequestInterceptor() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(credentials.getAccessKey(), credentials.getSecretKey());
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        AWS4Signer signer = new AWS4Signer();
        signer.setServiceName(credentials.getServiceName());
        signer.setRegionName(credentials.getRegion());
        return new AWSRequestSigningApacheInterceptor(credentials.getServiceName(), signer, credentialsProvider);
    }
}


