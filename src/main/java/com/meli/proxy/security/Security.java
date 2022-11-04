package com.meli.proxy.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class Security {
    @Value("${security.app-key}")
    private String appKey;
    @Value("${security.app-id}")
    private String appId;
    @Value("${security.token}")
    private String token;

    public boolean isAuthenticated(HttpServletRequest request){
        String authorization = request.getHeader("Authorization");
        String requestAppKey = request.getHeader("app-key");
        String requestAppId = request.getHeader("app-id");
        if(StringUtils.isNotEmpty(authorization) && authorization.replace("Bearer", StringUtils.EMPTY).trim().equals(token))
            return true;
        if(StringUtils.isNotEmpty(requestAppKey) && requestAppKey.equals(appKey)
            && StringUtils.isNotEmpty(appId) && requestAppId.equals(appId))
            return true;

        return false;
    }

}
