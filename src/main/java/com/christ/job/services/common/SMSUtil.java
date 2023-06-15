package com.christ.job.services.common;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class SMSUtil {

    public static<T> List<T> sendMessageList(List<T> messageList) {
        List<T> result = new ArrayList<>();
        if(!Utils.isNullOrEmpty(messageList)){
            messageList.forEach(t -> {

            });
        }
        return result;
    }

    public static StringBuffer formatRequest(String uri, String to, String messageBody, String templateId) throws Throwable {
        StringBuffer str = new StringBuffer();
        str.append(uri);
        str.append(getNVP("to", to, false));
        str.append(getNVP("message", messageBody, true));
        str.append(getNVP("template_id", templateId, true));
        return str;
    }

    public static StringBuffer getNVP(String name, String value, boolean encode) throws Exception {
        StringBuffer str = new StringBuffer();
        str.append(name).append("=");
        if (encode){
            str.append(URLEncoder.encode(value, "utf-8")).append("&");
        }else{
            str.append(value).append("&");
        }
        return str;
    }

    public static StringBuffer sendRequest(String urlString) throws IOException, InterruptedException {
        StringBuffer responseBf = new StringBuffer();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response : "+ response);
        System.out.println("response body : "+ response.body());
        System.out.println("response statusCode : "+ response.statusCode());
        return responseBf.append(response.body());
    }
}
