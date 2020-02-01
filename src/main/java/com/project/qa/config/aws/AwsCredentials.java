package com.project.qa.config.aws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "amazon")
@Configuration("amazonProperties")
public class AwsCredentials {

    private String serviceName;
    private String region;
    private String url;
    private String type;
    private String id;
    private String accessKey;
    private String secretKey;
    private String awsSQSURL;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAwsSQSURL() {
        return awsSQSURL;
    }

    public void setAwsSQSURL(String awsSQSURL) {
        this.awsSQSURL = awsSQSURL;
    }
}
