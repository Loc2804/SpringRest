package com.example.laptopshop.util;

import com.example.laptopshop.util.annotation.ApiMessage;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.example.laptopshop.domain.RestResponse;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();
        RestResponse<Object> rs = new RestResponse<Object>();
        rs.setStatusCode(status);

        if (body instanceof String) {
            return body;
        }
        if (status >= 400) {
            // case error
            return body;
        } else {
            // case success
            rs.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            rs.setMessage(message.value() != null ? message.value() : "Call Api Success");
        }

        return rs;
    }

}
