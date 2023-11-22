package com.chupaniko.controller;

import com.chupaniko.exceptions.HttpRequestExecutionException;
import com.chupaniko.exceptions.InvalidHttpMethodException;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MyHttpClient {
    private volatile static MyHttpClient myHttpClient;
    private PoolingHttpClientConnectionManager connectionManager;
    private CloseableHttpClient httpClient;

    private MyHttpClient() {
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(10);
        connectionManager.setDefaultMaxPerRoute(5);
        httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();
        //httpClient = HttpClients.createDefault();
    }

    public static MyHttpClient getInstance() {
        if (myHttpClient == null) {
            synchronized (MyHttpClient.class) {
                if (myHttpClient == null) {
                    myHttpClient = new MyHttpClient();
                }
            }
        }
        return myHttpClient;
    }

    public void close() {
        if (myHttpClient != null) {
            synchronized (MyHttpClient.class) {
                if (myHttpClient != null) {
                    try {
                        httpClient.close();
                        connectionManager.close();
                        myHttpClient = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public JSONObject sendRequest(MyHttpClient.Method method,
                                  String path,
                                  JSONObject body,
                                  String authToken) {
        ClassicHttpRequest request;
        switch (method) {
            case GET:
                request = new HttpGet(path);
                break;
            case POST:
                request = new HttpPost(path);
                break;
            case DELETE:
                request = new HttpDelete(path);
                break;
            default:
                throw new InvalidHttpMethodException();
        }
        if (body != null) {
            request.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));
        }
        if (authToken != null) {
            request.addHeader("X-Authorization", "Bearer " + authToken);
        }

        JSONObject result = new JSONObject();

        try {
            String responseStr = httpClient.execute(request, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
            if (responseStr.startsWith("[")) {
                result.put("data", new JSONArray(responseStr));
            } else if (responseStr.startsWith("{")) {
                result = new JSONObject(responseStr);
            }
            if (result.has("status")) {
                try {
                    int status = result.getInt("status");
                    if (status < 200 || status >= 300) {
                        throw new HttpRequestExecutionException("Ошибка выполнения HTTP-запроса!\n"
                                + responseStr
                        );
                    }
                } catch (JSONException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public enum Method {
        POST,
        GET,
        DELETE
    }
}
