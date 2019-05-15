package org.nds.dhp.manager;

import ca.uhn.fhir.rest.client.api.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpClientManager {

    private static Logger logger = LoggerFactory.getLogger(HttpClientManager.class);

    private HttpClient httpClient;

    public HttpClientManager() {
        httpClient = HttpClientBuilder.create().build();
    }

    public HttpClientManager(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public HttpResponse post(String url, List<Header> headers) throws Exception {

        return post(url, headers, null);
    }

    public HttpResponse post(String url, List<Header> headers, HttpEntity httpEntity) throws Exception {
        HttpPost httpPost;
        HttpResponse httpResponse;

        try {
            httpPost = createHttpPost(url, headers, httpEntity);

            httpResponse = post(httpPost);
        } catch (Exception exception) {
            logger.error("Unable to complete http post", exception);
            throw exception;
        }

        return httpResponse;
    }

    public HttpPost createHttpPost(String url, List<Header> headers, HttpEntity httpEntity) throws Exception {
        HttpPost httpPost;

        try {
            logger.info("Create httpPost {}", url, headers, httpEntity);

            httpPost = new HttpPost(url);

            for(Header header: headers) {
                logger.info("Header {} ()", header.getName(), header.getValue());
                httpPost.setHeader(header.getName(), header.getValue());
            }

            httpPost.setEntity(httpEntity);

            logger.info("Created httpPost {}", url, headers, httpEntity);

        } catch (Exception exception) {
            logger.error("Unable to create http post", exception);
            throw exception;
        }

        return httpPost;
    }

    public HttpResponse post(HttpPost httpPost) throws Exception {
        HttpResponse httpResponse;

        try {
            logger.info("Post Request {}", httpPost);

            httpResponse = httpClient.execute(httpPost);

            logger.info("Received response {}", httpResponse);
        } catch (Exception exception) {
            logger.error("Unable to complete http post", exception);
            throw exception;
        }

        return httpResponse;
    }

}
