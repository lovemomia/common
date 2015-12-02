package cn.momia.common.api.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MomiaHttpRequestBuilder {
    public static HttpUriRequest GET(String uri) {
        return GET(uri, null);
    }

    public static HttpUriRequest GET(String uri, Map<String, String> params) {
        return new HttpGet(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
    }

    private static String toUrlParams(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            int i = 0;
            int paramCount = params.size();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.append(entry.getKey()).append(("=")).append(entry.getValue());
                i++;
                if (i < paramCount) builder.append("&");
            }
        }

        return builder.toString();
    }

    public static HttpUriRequest POST(String uri) {
        return POST(uri, (Map) null);
    }

    public static HttpUriRequest POST(String uri, Map<String, String> params) {
        HttpPost httpPost = new HttpPost(uri);
        parseEntity(httpPost, params);

        return httpPost;
    }

    private static void parseEntity(HttpEntityEnclosingRequestBase httpMethod, Map<String, String> params) {
        if (params == null || params.isEmpty()) return;

        try {
            HttpEntity entity = new UrlEncodedFormEntity(toNameValuePairs(params), "UTF-8");
            httpMethod.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<NameValuePair> toNameValuePairs(Map<String, String> params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return nameValuePairs;
    }

    public static HttpUriRequest POST(String uri, String content) {
        return POST(uri, content, "application/json");
    }

    public static HttpUriRequest POST(String uri, String content, String contentType) {
        HttpPost httpPost = new HttpPost(uri);
        parseEntity(httpPost, content, contentType);

        return httpPost;
    }

    private static void parseEntity(HttpEntityEnclosingRequestBase httpMethod, String content, String contentType) {
        StringEntity entity = new StringEntity(content, "UTF-8");
        entity.setContentType(contentType);
        entity.setContentEncoding("UTF-8");

        httpMethod.addHeader(HTTP.CONTENT_TYPE, contentType);
        httpMethod.setEntity(entity);
    }

    public static HttpUriRequest PUT(String uri) {
        return PUT(uri, (Map) null);
    }

    public static HttpUriRequest PUT(String uri, Map<String, String> params) {
        HttpPut httpPut = new HttpPut(uri);
        parseEntity(httpPut, params);

        return httpPut;
    }

    public static HttpUriRequest PUT(String uri, String content) {
        return PUT(uri, content, "application/json");
    }

    public static HttpUriRequest PUT(String uri, String content, String contentType) {
        HttpPut httpPut = new HttpPut(uri);
        parseEntity(httpPut, content, contentType);

        return httpPut;
    }

    public static HttpUriRequest DELETE(String uri) {
        return DELETE(uri, null);
    }

    public static HttpUriRequest DELETE(String uri, Map<String, String> params) {
        return new HttpDelete(new StringBuilder().append(uri).append("?").append(toUrlParams(params)).toString());
    }
}
