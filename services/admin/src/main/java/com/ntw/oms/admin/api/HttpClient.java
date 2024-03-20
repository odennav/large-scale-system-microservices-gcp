//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.admin.api;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by anurag on 09/07/18.
 */

public class HttpClient {

    private PoolingHttpClientConnectionManager cm;

    public HttpClient(int connPoolSize) {
        cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(connPoolSize);
        cm.setDefaultMaxPerRoute(connPoolSize);
    }

    private static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    private String formatUrlParams(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder("");
        if (params != null) {
            int paramsSize = params.keySet().size();
            if (paramsSize > 0) {
                int counter = 0;
                for (String paramKey : params.keySet()) {
                    String value = params.get(paramKey);
                    if (value == null) {
                        logger.warn("Key value is null for param {}", paramKey);
                        continue;
                    }
                    try {
                        value = URLEncoder.encode(value, StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        logger.error("Unable to encode url param; key={}; value={};", paramKey, value);
                        throw e;
                    }
                    urlBuilder.append(paramKey).append("=").append(value);
                    if (++counter < paramsSize) {
                        urlBuilder.append("&");
                    }
                }
            }
        }
        return urlBuilder.toString();
    }

    private URI buildUrl(String host, int port, String path, List<NameValuePair> params) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder()
                .setScheme("http")
                .setHost(host)
                .setPort(port)
                .setPath(path);
        if (params != null && params.size() > 0) {
            uriBuilder.addParameters(params);
        }
        return uriBuilder.build();
    }

    public HttpClientResponse sendPost(String host, int port, String path, String authHeader,
                                       String contentType, Map<String, String> params) throws IOException {
        String body = formatUrlParams(params);
        logger.debug("Sending 'POST' request to host:{} path:{} ", host, path);
        logger.debug("Post parameters : {}", body);
        return sendPost(host, port, path, authHeader, contentType, body);
    }

    public HttpClientResponse sendPost(String host, int port, String path, String authHeader,
                                       String contentType, String body) throws IOException {
        return sendPostOrPut(host, port, path, authHeader, contentType, body, true);
    }

    public HttpClientResponse sendPut(String host, int port, String path, String authHeader,
                                       String contentType, String body) throws IOException {
        return sendPostOrPut(host, port, path, authHeader, contentType, body, false);
    }

    private HttpClientResponse sendPostOrPut(String host, int port, String path, String authHeader,
                                       String contentType, String body, boolean isPost) throws IOException {
        URI uri;
        try {
            uri = buildUrl(host, port, path, null);
        } catch (URISyntaxException e) {
            logger.error("Error building uri for host={}; path={};", host, path);
            throw new IOException(e);
        }
        HttpEntityEnclosingRequestBase httpRequest = isPost ? new HttpPost(uri) : new HttpPut(uri);
        if (authHeader != null) {
            httpRequest.setHeader("Authorization", authHeader);
        }
        httpRequest.setHeader("Accept-Language", "en-US,en;q=0.5");
        httpRequest.setHeader("Content-Type", contentType);
        //httpRequest.setHeader("Content-Length", String.valueOf(body.getBytes().length));

        StringEntity userEntity = new StringEntity(body);
        httpRequest.setEntity(userEntity);

        logger.debug("Sending POST request to URL {} ", uri.toString());
        CloseableHttpResponse response;
        HttpClientResponse responseObject = new HttpClientResponse();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            response = httpClient.execute(httpRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("Status code for request uri {} is {}", uri.toString(), statusCode);
            HttpEntity httpEntity = response.getEntity();
            String responseString = EntityUtils.toString(httpEntity);
            logger.info("Got response: {}", responseString.substring(0, Math.min(responseString.length(), 100)));
            responseObject.setStatus(statusCode);
            responseObject.setBody(responseString);
        } catch (ClientProtocolException cpe) {
            logger.error("Error calling uri={}; exception={}", uri.toString(), cpe.getMessage());
            throw new IOException(cpe);
        } catch (IOException ioe) {
            logger.error("Error calling uri={}; exception={}", uri.toString(), ioe.getMessage());
            throw ioe;
        }

        if (responseObject.getStatus() != 200 && responseObject.getStatus() != 201
                && responseObject.getStatus() != 401 && responseObject.getStatus() != 403) {
            logger.error("Error calling uri={}; statusCode={}", uri.toString(), responseObject.getStatus());
            throw new IOException("Error calling uri "+uri.toString());
        }
        return responseObject;
    }

    public HttpClientResponse sendGet(String host, int port, String path, String authHeader,
                                      String queryString) throws IOException {
        List<NameValuePair> params = null;
        if (queryString != null && queryString.length() > 0) {
            try {
                params = URLEncodedUtils.parse(new URI("http://dummy.com?" + queryString), Charset.forName("UTF-8"));
            } catch (URISyntaxException e) {
                logger.error("Error parsing query string for host={}; path={}; context={}", host, path, queryString);
                logger.error(e.getMessage(), e);
                throw new IOException(e);
            }
        }
        return sendGet(host, port, path, authHeader, params);
    }

    public HttpClientResponse sendGet(String host, int port, String path, String authHeader,
                                      Map<String, String> params) throws IOException {
        List<NameValuePair> paramsList = new ArrayList<>();
        if (params != null) {
            for (String paramKey : params.keySet()) {
                String value = params.get(paramKey);
                if (value == null) {
                    logger.warn("Key value is null for param {}", paramKey);
                    continue;
                }
                paramsList.add(new BasicNameValuePair(paramKey, value));
            }
        }
        return sendGet(host, port, path, authHeader, paramsList);
    }

    private HttpClientResponse sendGet(String host, int port, String path, String authHeader,
                                      List<NameValuePair> params) throws IOException {
        if (params == null) {
            params = new ArrayList<>();
        }
        URI uri;
        try {
            uri = buildUrl(host, port, path, params);
        } catch (URISyntaxException e) {
            logger.error("Error building uri for host={}; path={}; context={}", host, path, params);
            throw new IOException(e);
        }

        HttpGet httpGet = new HttpGet(uri);
        if (authHeader != null) {
            httpGet.setHeader("Authorization", authHeader);
        }
        logger.debug("Sending GET request to URL : " + uri.toString());

        CloseableHttpResponse response;
        HttpClientResponse responseObject = new HttpClientResponse();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();
            responseObject.setStatus(statusCode);
            if (statusCode >= 200 && statusCode <= 206) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String responseString = EntityUtils.toString(httpEntity);
                    logger.info("Got response: {}", responseString.substring(0, Math.min(responseString.length(), 100)));
                    responseObject.setBody(responseString);
                } else {
                    logger.warn("No response body received from service");
                }
            } else {
                if (responseObject.getStatus() == 401 ||  responseObject.getStatus() == 403) {
                    logger.warn("User forbidden or unauthorized. uri={}; statusCode={}",
                            uri.toString(), responseObject.getStatus());
                } else {
                    logger.error("Error calling uri={}; statusCode={}", uri.toString(), responseObject.getStatus());
                    throw new IOException("Error calling uri "+uri.toString());
                }
            }
        } catch(IOException ioe) {
            logger.error("Error calling uri={}; exception={}", uri.toString(), ioe.getMessage());
            throw ioe;
        }
        return responseObject;
    }

    public HttpClientResponse sendDelete(String host, int port, String path, String authHeader) throws IOException {
        URI uri;
        try {
            uri = buildUrl(host, port, path, null);
        } catch (URISyntaxException e) {
            logger.error("Error building uri for host={}; path={};", host, path);
            throw new IOException(e);
        }

        HttpDelete httpDelete = new HttpDelete(uri);
        if (authHeader != null) {
            httpDelete.setHeader("Authorization", authHeader);
        }
        logger.debug("Sending DELETE request to URL : " + uri.toString());

        CloseableHttpResponse response;
        HttpClientResponse responseObject = new HttpClientResponse();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm).build();
        try {
            response = httpClient.execute(httpDelete);
            int statusCode = response.getStatusLine().getStatusCode();
            responseObject.setStatus(statusCode);
            if (statusCode >= 200 && statusCode <= 206) {
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    String responseString = EntityUtils.toString(httpEntity);
                    logger.info("Got response: {}", responseString.substring(0, Math.min(responseString.length(), 100)));
                    responseObject.setBody(responseString);
                } else {
                    logger.warn("No response body received from service");
                }
            } else {
                if (responseObject.getStatus() == 401 ||  responseObject.getStatus() == 403) {
                    logger.warn("User forbidden or unauthorized. uri={}; statusCode={}",
                            uri.toString(), responseObject.getStatus());
                } else {
                    logger.error("Error calling uri={}; statusCode={}", uri.toString(), responseObject.getStatus());
                    throw new IOException("Error calling uri "+uri.toString());
                }
            }
        } catch(IOException ioe) {
            logger.error("Error calling uri={}; exception={}", uri.toString(), ioe.getMessage());
            throw ioe;
        }
        return responseObject;
    }

}
