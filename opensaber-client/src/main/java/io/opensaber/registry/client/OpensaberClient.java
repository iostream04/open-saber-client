package io.opensaber.registry.client;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.opensaber.pojos.Request;
import io.opensaber.pojos.RequestParams;
import io.opensaber.pojos.Response;
import io.opensaber.pojos.ResponseSerializer;
import io.opensaber.registry.client.data.RequestData;
import io.opensaber.registry.client.data.ResponseData;
import io.opensaber.registry.config.Configuration;
import io.opensaber.registry.constants.Constants.ApiEndPoints;
import io.opensaber.registry.exception.TransformationException;
import io.opensaber.registry.transform.ITransformer;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpensaberClient implements Client<String> {

    private static Logger logger = LoggerFactory.getLogger(OpensaberClient.class);
    public static Builder builder() {
        return new Builder();
    }

    private ITransformer<String> requestTransformer;
    private ITransformer<String> responseTransformer;

    private HttpClient httpClient;
    private Gson gson;
    // private ObjectMapper mapper = new ObjectMapper();
    private static Type mapType = new TypeToken<Map<String, Object>>(){}.getType();



    private OpensaberClient(Builder clientBuilder) {
        Preconditions.checkNotNull(clientBuilder.requestTransformer);
        Preconditions.checkNotNull(clientBuilder.responseTransformer);
        this.requestTransformer = clientBuilder.requestTransformer;
        this.responseTransformer = clientBuilder.responseTransformer;

        this.httpClient = HttpClient.getInstance();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ResponseSerializer.class, new ResponseSerializer());
        gson = gsonBuilder.create();
    }



    public static final class Builder {
        private ITransformer<String> requestTransformer;
        private ITransformer<String> responseTransformer;

        public Builder requestTransformer(ITransformer<String> requestTransformer) {
            this.requestTransformer = requestTransformer;
            return this;
        }

        public Builder responseTransformer(ITransformer<String> responseTransformer) {
            this.responseTransformer = responseTransformer;
            return this;
        }

        public OpensaberClient build() {
            return new OpensaberClient(this);
        }
    }


    @Override
    public ResponseData<String> addEntity(RequestData<String> requestData, Map<String, String> headers)
            throws TransformationException, ClientProtocolException, IOException, URISyntaxException {
        ResponseData<String> transformedReqData = requestTransformer.transform(requestData);
        logger.debug("AddEntity Transformed Request Data: " + transformedReqData.getResponseData());
        String response =
                httpClient.post(Configuration.BASE_URL + ApiEndPoints.ADD, createHttpHeaders(headers),
                        gson.toJson(createRequestEntity(transformedReqData.getResponseData())));
        //String result = gson.toJson(response.getBody());
        return new ResponseData<>(response);
    }

    @Override
    public ResponseData<String> updateEntity(RequestData<String> requestData, Map<String, String> headers)
            throws TransformationException, ClientProtocolException, IOException, URISyntaxException {
        ResponseData<String> transformedReqData = requestTransformer.transform(requestData);
        logger.debug("UpdateEntity Transformed Request Data: " + transformedReqData.getResponseData());
        String response =
                httpClient.post(Configuration.BASE_URL + ApiEndPoints.UPDATE,
                        createHttpHeaders(headers), gson.toJson(createRequestEntity(transformedReqData.getResponseData())));
        //String result = gson.toJson(response.getBody());
        return new ResponseData<>(response);
    }

    @Override
    public ResponseData<String> addAndAssociateEntity(URI existingEntity, URI property,
                                                      RequestData<String> requestData, Map<String, String> headers)
            throws TransformationException, ClientProtocolException, IOException, URISyntaxException {
        ResponseData<String> transformedReqData = requestTransformer.transform(requestData);
        logger.debug("AddAndAssociateEntity Transformed Request Data: " + transformedReqData.getResponseData());
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("id", existingEntity.toString());
        queryParams.put("prop", property.toString());
        String response =
                httpClient.post(Configuration.BASE_URL + ApiEndPoints.ADD,
                        createHttpHeaders(headers), queryParams,
                        gson.toJson(createRequestEntity(transformedReqData.getResponseData())));
        //String result = gson.toJson(response.getBody());
        return new ResponseData<>(response);
    }

    @Override
    public ResponseData<String> readEntity(URI entity, Map<String, String> headers)
            throws TransformationException, ClientProtocolException, IOException, URISyntaxException {
    	String entityId = extractEntityId(entity);
        String response = httpClient.get(Configuration.BASE_URL + ApiEndPoints.READ +"/"+entityId, createHttpHeaders(headers));
        //JsonObject responseJson = gson.toJsonTree(response).getAsJsonObject();
        JsonObject responseJson = gson.fromJson(response, JsonObject.class);
        String resultNode = gson.toJson(gson.fromJson(response, Response.class).getResult(), mapType);
        String transformedJson = responseTransformer.transform(new RequestData<>(resultNode)).getResponseData();
        logger.debug("Transformed Response Data: " + transformedJson);
        responseJson.add("result", gson.fromJson(transformedJson, JsonObject.class));
        return new ResponseData<>(responseJson.toString());
    }


    public ResponseData<String> deleteEntity(URI entity, Map<String, String> headers)
    		throws ClientProtocolException, IOException, URISyntaxException{
        System.out.println("entity = " + entity);
        String entityId = extractEntityId(entity);
        System.out.println("entityId = " + entityId);
        String response = httpClient.delete(Configuration.BASE_URL + ApiEndPoints.DELETE +"/"+entityId,
                createHttpHeaders(headers),null);
        System.out.println("response = " + response);
        //String result = gson.toJson(response.getBody());
        return new ResponseData<>(response);
    }

    private Request createRequestEntity(String requestData) {
        return new Request(new RequestParams(), gson.fromJson(requestData, mapType));
    }

    /*private HttpHeaders createHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach((headerName, headerValue) -> httpHeaders.set(headerName, headerValue));
        return httpHeaders;
    }*/
    
    private Header[] createHttpHeaders(Map<String, String> headers) {
        List<Header> headerList = new ArrayList<Header>();
        headers.forEach((headerName, headerValue) -> headerList.add(new BasicHeader(headerName, headerValue)));
        return headerList.toArray(new Header[headerList.size()]);
    }

    private String extractEntityId(URI entity){
        String entityId = entity.toString();
        return entityId.substring(entityId.lastIndexOf("/")+1);
        /*Pattern pattern = Pattern.compile("^" + Pattern.quote(Configuration.BASE_URL) + "(.*?)$");
        Matcher matcher = pattern.matcher(entityId);
        if(matcher.find()) {
            entityId = matcher.group(1);
        }
        return entityId;*/
    }

}
