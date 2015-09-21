package cn.momia.common.api;

import cn.momia.common.api.exception.MomiaLoginException;
import cn.momia.common.api.exception.MomiaFailedException;
import cn.momia.common.api.http.MomiaHttpRequest;
import cn.momia.common.api.http.MomiaHttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public abstract class AbstractServiceApi implements ServiceApi {
    protected String service;

    public void setService(String service) {
        this.service = service;
    }

    protected String url(Object... paths) {
        StringBuilder urlBuilder = new StringBuilder().append(service);
        for (Object path : paths) urlBuilder.append("/").append(path);

        return urlBuilder.toString();
    }

    protected Object executeRequest(MomiaHttpRequest request) {
        try {
            HttpClient httpClient = createHttpClient();

            HttpResponse response = httpClient.execute(request);
            if (!isSuccessfulResponse(response)) throw new MomiaFailedException("fail to execute request: " + request);;

            MomiaHttpResponse momiaHttpResponse = buildResponse(response);
            if (momiaHttpResponse.isTokenExpired()) throw new MomiaLoginException();
            if (!momiaHttpResponse.isSuccessful()) throw new MomiaFailedException(momiaHttpResponse.getErrmsg());

            return momiaHttpResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClient createHttpClient() {
        // TODO more configuration of http client
        return HttpClients.createDefault();
    }

    private boolean isSuccessfulResponse(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private MomiaHttpResponse buildResponse(HttpResponse response) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(entity);

        return JSON.toJavaObject(responseJson, MomiaHttpResponse.class);
    }
}
