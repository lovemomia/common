package cn.momia.common.api;

import cn.momia.common.api.exception.MomiaExpiredException;
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
        HttpClient httpClient = createHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            if (!checkResponseStatus(response)) throw new RuntimeException("fail to execute request: " + request);;

            MomiaHttpResponse momiaHttpResponse = buildResponse(response);
            if (momiaHttpResponse.tokenExpired()) throw new MomiaExpiredException();
            if (!momiaHttpResponse.successful()) throw new MomiaFailedException(momiaHttpResponse.getErrmsg());

            return momiaHttpResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClient createHttpClient() {
        // TODO more configuration of http client
        return HttpClients.createDefault();
    }

    private boolean checkResponseStatus(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private MomiaHttpResponse buildResponse(HttpResponse response) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(entity);

        return JSON.toJavaObject(responseJson, MomiaHttpResponse.class);
    }
}
