package de.hrw.xilab.lora;


import com.fasterxml.jackson.databind.json.JsonMapper;
import de.hrw.xilab.lora.Authentication.Type;
import de.hrw.xilab.spring.model.TokenResult;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class LoraSpringGlue {
    private TokenResult token;
    private final String baseUrl = "http://localhost:8080";

    private static final LoraSpringGlue instance = new LoraSpringGlue();

    public static LoraSpringGlue getInstance() {
        return instance;
    }

    @SneakyThrows
    private LoraSpringGlue(){
        updateToken();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(token.getExpiration().isBefore(Instant.now())) {
                    updateToken();
                }
            }
        };
        new Timer().scheduleAtFixedRate(task, 0, TimeUnit.MINUTES.toMillis(30));
    }

    private void updateToken(){
        try {
            String json = send("/token/create", Type.BASIC, "GET");
            JsonMapper obj = JsonMapper.builder().build();
            obj.findAndRegisterModules();
            token = obj.readValue(json, TokenResult.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateSpringDatabase(String uuid, Optional<Integer> battery, Optional<Integer> waterLevel) {

        String params = "";
        if(battery.isPresent()){
            params += "?battery=" + battery.get();
        }
        if(waterLevel.isPresent()){
            if(params.equals("")){
                params += "?waterLevel=" + waterLevel.get();
            }else{
                params += "&waterLevel=" + waterLevel.get();
            }
        }
        send("/device/" + uuid + params, Type.TOKEN, "PATCH");
    }


    private String send(String resourcePath, Type type, String method){

        HttpClient client= HttpClient.newBuilder().build();
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();

        String authorization;
        if(type == Type.BASIC){
            authorization = Authentication.Basic.getAuthentication("admin", "admin");
        }else {
            authorization = Authentication.Token.getToken(token);
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .method(method, publisher)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .uri(URI.create(baseUrl + resourcePath));
        HttpRequest request = requestBuilder.build();

        String result = "";
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            result = response.body();

        }catch (Exception ignored){}
        return result;
    }
}
