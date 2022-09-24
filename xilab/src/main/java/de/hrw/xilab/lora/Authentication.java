package de.hrw.xilab.lora;


import de.hrw.xilab.spring.model.TokenResult;

import java.util.Base64;

public class Authentication {
    public enum Type{
        BASIC, TOKEN
    }

    public static class Token{
        private Token(){}
        public static String getToken(TokenResult token){
           return "Bearer " + token.getToken();
        }
    }

    public static class Basic{
        private Basic(){}
        public static String getAuthentication(String username, String password){
            return "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        }
    }
}
