package cn.momia.common.api;

import cn.momia.common.api.dto.PagedList;
import cn.momia.common.api.exception.MomiaLoginException;
import cn.momia.common.api.exception.MomiaErrorException;
import cn.momia.common.api.http.MomiaHttpResponse;
import cn.momia.common.api.util.CastUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public abstract class ServiceApi {
    private String service;

    public void setService(String service) {
        this.service = service;
    }

    protected String url(String path, Object... args) {
        path = String.format(path, args);

        StringBuilder urlBuilder = new StringBuilder().append(service);
        if (!path.startsWith("/")) urlBuilder.append("/");
        urlBuilder.append(path);

        return urlBuilder.toString();
    }

    protected Object execute(HttpUriRequest request) {
        try {
            HttpClient httpClient = createHttpClient();

            HttpResponse response = httpClient.execute(request);
            if (!isSuccessful(response)) throw new RuntimeException("fail to execute request: " + request);;

            MomiaHttpResponse momiaHttpResponse = buildResponse(response);
            if (momiaHttpResponse.isTokenExpired()) throw new MomiaLoginException();
            if (!momiaHttpResponse.isSuccessful()) throw new MomiaErrorException(momiaHttpResponse.getErrmsg());

            return momiaHttpResponse.getData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> T executeReturnObject(HttpUriRequest request, Class<T> clazz) {
        Object object = execute(request);
        if (object instanceof JSON) return CastUtil.toObject((JSON) object, clazz);
        return clazz.cast(object);
    }

    protected <T> List<T> executeReturnList(HttpUriRequest request, Class<T> clazz) {
        Object object = execute(request);
        return CastUtil.toList((JSON) object, clazz);
    }

    protected <T> PagedList<T> executeReturnPagedList(HttpUriRequest request, Class<T> clazz) {
        Object object = execute(request);
        return CastUtil.toPagedList((JSON) object, clazz);
    }

    private HttpClient createHttpClient() {
        // TODO more configuration of http client
        return HttpClients.createDefault();
    }

    private boolean isSuccessful(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
    }

    private MomiaHttpResponse buildResponse(HttpResponse response) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(entity);

        return CastUtil.toObject(responseJson, MomiaHttpResponse.class);
    }
}
