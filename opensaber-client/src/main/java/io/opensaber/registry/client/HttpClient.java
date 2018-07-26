package io.opensaber.registry.client;
import io.opensaber.registry.config.Configuration;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.client.ResponseHandler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private static HttpClient ourInstance = new HttpClient();

    public static HttpClient getInstance() {
        return ourInstance;
    }

    //private RestTemplate restTemplate;
    
    private CloseableHttpClient client;

    private HttpClient() {
        /*HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Configuration.HTTP_CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(Configuration.HTTP_READ_TIMEOUT);
        requestFactory.setConnectionRequestTimeout(Configuration.HTTP_CONNECTION_REQUEST_TIMEOUT);
        restTemplate = new RestTemplate(requestFactory);*/
    	RequestConfig config = RequestConfig.custom().setConnectTimeout(Configuration.HTTP_CONNECT_TIMEOUT)
    			  .setConnectionRequestTimeout(Configuration.HTTP_CONNECTION_REQUEST_TIMEOUT).build();
    	client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }
    
   /* public ResponseEntity<Response> post(String url, HttpHeaders headers, String payload) {
        return post(url, headers, new HashMap<>(), payload);
    }

    public ResponseEntity<Response> delete(String url, HttpHeaders headers, String payload) {
        return delete(url, headers, new HashMap<>(), payload);
    }

    public ResponseEntity<Response> delete(String url,HttpHeaders headers, Map<String, String> queryParams, String payload){
        HttpEntity<String> entity =  new HttpEntity<>(payload, headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach((param, paramValue) -> uriBuilder.queryParam(param, paramValue));
        ResponseEntity<Response> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.DELETE, entity, Response.class);
        return response;
    }

    public ResponseEntity<Response> post(String url, HttpHeaders headers, Map<String, String> queryParams, String payload) {
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach((param, paramValue) -> uriBuilder.queryParam(param, paramValue));
        ResponseEntity<Response> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, Response.class);
        return response;
    }

    public ResponseEntity<Response> get(String url, HttpHeaders headers) {
        return get(url, headers, new HashMap<>());
    }

    public ResponseEntity<Response> get(String url, HttpHeaders headers, Map<String, String> queryParams) {
        HttpEntity<String> entity = new HttpEntity<>(headers);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
        queryParams.forEach((param, paramValue) -> uriBuilder.queryParam(param, paramValue));
        ResponseEntity<Response> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.GET, entity, Response.class);
        return response;
    }*/

    public String post(String url, Header[] headers, String payload) throws ClientProtocolException, IOException, URISyntaxException {
        return post(url, headers, new HashMap<>(), payload);
    }

    public String delete(String url, Header[] headers, String payload) throws ClientProtocolException, IOException, URISyntaxException {
        return delete(url, headers, new HashMap<>(), StringUtils.EMPTY);
    }

    public String delete(String url,Header[] headers, Map<String, String> queryParams, String payload) throws ClientProtocolException, IOException, URISyntaxException{
        URIBuilder uriBuilder = new URIBuilder(url);
        queryParams.forEach((param, paramValue) -> uriBuilder.addParameter(param, paramValue));
        HttpDelete httpDelete = new HttpDelete(uriBuilder.build().toString());
        httpDelete.setHeaders(headers);
        CloseableHttpResponse response = client.execute(httpDelete);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public String post(String url, Header[] headers, Map<String, String> queryParams, String payload) throws ClientProtocolException, IOException, URISyntaxException {
    	StringEntity entity = new StringEntity(payload, "UTF-8");
    	URIBuilder uriBuilder = new URIBuilder(url);
        queryParams.forEach((param, paramValue) -> uriBuilder.addParameter(param, paramValue));
        HttpPost httpPost = new HttpPost(uriBuilder.build().toString());
        httpPost.setHeaders(headers);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = client.execute(httpPost);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }

    public String get(String url, Header[] headers) throws ClientProtocolException, IOException, URISyntaxException {
        return get(url, headers, new HashMap<>());
    }

    public String get(String url, Header[] headers, Map<String, String> queryParams) throws ClientProtocolException, IOException, URISyntaxException  {
    	URIBuilder uriBuilder = new URIBuilder(url);
	ResponseHandler<String> responseHandler = new BasicResponseHandler();
        queryParams.forEach((param, paramValue) -> uriBuilder.addParameter(param, paramValue));
        HttpGet httpGet = new HttpGet(uriBuilder.build().toString());
        httpGet.setHeaders(headers);
        CloseableHttpResponse response = client.execute(httpGet);
        return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
}
