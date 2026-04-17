package org.example.utils;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class HttpClient {



    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS) //connect timeout
            .writeTimeout(50, TimeUnit.SECONDS) //send timeout
            .readTimeout(60, TimeUnit.SECONDS)  //receive timeout
            .callTimeout(90, TimeUnit.SECONDS) //call timeout
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC) )
            .eventListenerFactory(MetricsEventListener.FACTORY)
            .build();


    public static String get(String url, Map<String,String> headers) {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            log.error("get url {} 请求异常", url,e);
        }
        return null;
    }



    public static String post(String url, Map<String,String> headers, String body,String mediaType) {
        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(RequestBody.create(body,MediaType.parse(mediaType)))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            log.error("post url {} 请求异常", url,e);
        }
        return null;

    }

    public static String postFormBody(String url, Map<String,String> headers, Map<String,String> formBody) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        formBody.forEach((key,value)->formBodyBuilder.add(key,value)); //这里添加表单参数，没有编码的值

        Request request = new Request.Builder()
                .url(url)
                .headers(Headers.of(headers))
                .post(formBodyBuilder.build())
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            return response.body().string();
        } catch (IOException e) {
            log.error("postFormBody url {} 请求异常", url,e);
        }
        return null;

    }

    public static String postMultipartBody(String url, Map<String,String> headers,Map<String,String> multipartHeaders, byte[] body, String mediaType) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM).addPart(Headers.of(multipartHeaders),
                        RequestBody.create(body,MediaType.parse(mediaType))).build();

        Request request = new Request.Builder()
                .headers(Headers.of(headers))
                .url(url)
                .post(requestBody)
                .build();



        try (Response response = client.newBuilder().writeTimeout(60, TimeUnit.SECONDS).build().newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        } catch (IOException e) {
            log.error("postMultipartBody url {} 请求异常", url,e);
        }
        return null;

    }

}
