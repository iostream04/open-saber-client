package io.opensaber.registry.client;

import io.opensaber.registry.client.data.RequestData;
import io.opensaber.registry.client.data.ResponseData;
import io.opensaber.registry.exception.TransformationException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

public interface Client<T> {

    /**
     * This method will allow you to add a new entity of any type. The {@link io.opensaber.registry.client.data.RequestData}
     * parameter will take JSON-LD data in String format. The method also takes the HttpHeaders as Map object.
     * The return type will be {@link io.opensaber.registry.client.data.ResponseData} wherein the JSON-LD data
     * will be serialized as UTF8 String. Use {@link io.opensaber.registry.client.TransformationClient}
     * to convert JSON to JSON-LD.
     * @param requestData
     * @param headers
     * @return
     */
    ResponseData<T> addEntity(RequestData<T> requestData, Map<String, String> headers) throws TransformationException, ClientProtocolException, IOException, URISyntaxException;

    /**
     * This method will allow you to update an existing entity. The {@link io.opensaber.registry.client.data.RequestData}
     * parameter will take JSON-LD data in String format. The method also takes the HttpHeaders as Map object.
     * The updateEntity method will generally overwrite the values of all the input properties. In the case of
     * multi-valued enumerated properties, it will create the values which do not exist already. The updateEntity method
     * also expects the input JSON-LD to have the Id of the entity that is being updated.
     * The return type will be {@link io.opensaber.registry.client.data.ResponseData} wherein the JSON-LD data
     * will be serialized as UTF8 String. Use {@link io.opensaber.registry.client.TransformationClient}
     * to convert JSON to JSON-LD.
     * @param requestData
     * @return
     */
    ResponseData<T> updateEntity(RequestData<T> requestData, Map<String, String> headers) throws TransformationException, ClientProtocolException, IOException, URISyntaxException;

    /**
     * This method will allow you to create a new entity and link to an existing entity.
     * The {@link io.opensaber.registry.client.data.RequestData} parameter will take JSON-LD data in String format.
     * The method also takes the HttpHeaders as Map object. The addAndAssociateEnytity method will take the FQ
     * {@link java.net.URI} of an existing entity and FQ {@link java.net.URI} of the property predicate. The new entity
     * will be created and linked to the existing entity automatically. The return type will be
     * {@link io.opensaber.registry.client.data.ResponseData} wherein the JSON-LD data will be serialized as UTF8 String.
     * Use {@link io.opensaber.registry.client.TransformationClient} to convert JSON to JSON-LD.
     * @param existingEntity
     * @param property
     * @param requestData
     * @return
     */
    ResponseData<T> addAndAssociateEntity(URI existingEntity, URI property, RequestData<T> requestData, Map<String, String> headers) 
    		throws TransformationException, ClientProtocolException, IOException, URISyntaxException;

    /**
     * This method will take the FQ {@link java.net.URI} of an existing entity that needs to be retrieved.
     * The method also takes the HttpHeaders as Map object. The return type will be
     * {@link io.opensaber.registry.client.data.ResponseData} wherein the JSON-LD data will be serialized as UTF8 String.
     * @param entity
     * @param headers
     * @return
     */
    ResponseData<T> readEntity(URI entity, Map<String, String> headers) throws TransformationException, ClientProtocolException, IOException, URISyntaxException;

    /**
     * This method will take the FQ {@link java.net.URI} of an existing entity id that needs to be deleted.
     * The method also takes the HttpHeaders as Map object. The return type will be
     * {@link io.opensaber.registry.client.data.ResponseData} .
     * @param property
     * @param headers
     * @return
     */
    ResponseData<T> deleteEntity(URI property, Map<String, String> headers) throws ClientProtocolException, IOException, URISyntaxException ;
    
    /**
     * This method will allow you to search for entities based on property names and values provided in the JSON input
     * @param requestData - A sample requestData to search for all teachers with serialNum 12, would look like this
     * {\"teacher\":{\"serialNum\":12}}
     * @param headers
     * @return
     * @throws TransformationException
     * @throws IOException
     * @throws URISyntaxException
     */
    ResponseData<String> searchEntity(RequestData<String> requestData, Map<String, String> headers) throws TransformationException, IOException, URISyntaxException;
}
