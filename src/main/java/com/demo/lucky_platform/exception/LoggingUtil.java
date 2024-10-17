package com.demo.lucky_platform.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.net.InetAddress;
import java.security.Principal;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoggingUtil {

    @SneakyThrows
    public static void errorLogging(HttpServletRequest request, Exception e, boolean printStackTrace) {
        Map<String, Object> map = new HashMap<>();

        map.put("request", "[" + request.getMethod() + "] " + request.getRequestURI());
        map.put("host", InetAddress.getLocalHost().getHostName());
        map.put("ip", LoggingUtil.getClientIp(request));
        map.put("user", Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).orElse("Anonymous"));
        map.put("exception", e.toString());
        map.put("header_map", headerMap(request));
        map.put("parameter_map", parameterMap(request));
        map.put("body", getBody(request));

        log.error(new JSONObject(map).toString(4));

        if (printStackTrace) {
            log.error(getPrintMessage(e), e);
        }
    }

    @SneakyThrows
    public static void warningLogging(HttpServletRequest request, Exception e, boolean printStackTrace) {
        Map<String, Object> map = new HashMap<>();

        map.put("request", "[" + request.getMethod() + "] " + request.getRequestURI());
        map.put("host", InetAddress.getLocalHost().getHostName());
        map.put("ip", LoggingUtil.getClientIp(request));
        map.put("user", Optional.ofNullable(request.getUserPrincipal()).map(Principal::getName).orElse("Anonymous"));
        map.put("exception", e.toString());
        map.put("header_map", headerMap(request));
        map.put("parameter_map", parameterMap(request));
        map.put("body", getBody(request));

        log.warn(new JSONObject(map).toString(4));

        if (printStackTrace) {
            log.warn(getPrintMessage(e), e);
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        StringBuilder message = new StringBuilder();

        message.append(throwable.toString()).append(System.lineSeparator());

        for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            message.append("\tat ").append(stackTraceElement.toString()).append(System.lineSeparator());
        }

        return message.toString();
    }

    public static Map<String, String> headerMap(HttpServletRequest request) {
        Map<String, String> convertedHeaderMap = new HashMap<>();
        Enumeration<String> headerMap = request.getHeaderNames();

        while (headerMap.hasMoreElements()) {
            String name = headerMap.nextElement();
            String value = request.getHeader(name);

            convertedHeaderMap.put(name, value);
        }

        return convertedHeaderMap;
    }

    public static Map<String, String> parameterMap(HttpServletRequest request) {
        Map<String, String> convertedParameterMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            StringJoiner valueString = new StringJoiner(",");

            for (String value : values) {
                valueString.add(value);
            }

            convertedParameterMap.put(key, valueString.toString());
        }

        return convertedParameterMap;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return request.getHeader("X-Requested-With") != null && request.getHeader("X-Requested-With").equals("XMLHttpRequest");
    }

    public static String getPrintMessage(Throwable e) {
        if (isNull(e)) {
            return null;
        }

        var lastCallClassMethod = e.getStackTrace()[0].getClassName() + "." + e.getStackTrace()[0].getMethodName();
        return ObjectUtils.isEmpty(e.getMessage()) ? lastCallClassMethod : e.getMessage();
    }

    public static Map<String, Object> getBody(HttpServletRequest request) {

        Map<String, Object> result = Collections.emptyMap();

        if (nonNull(request) && request instanceof ContentCachingRequestWrapper) {
            var body = ((ContentCachingRequestWrapper)request).getContentAsByteArray();
            if (!ObjectUtils.isEmpty(body)) {
                try {
                    ObjectMapper om = new ObjectMapper();
                    result = om.readValue(body, HashMap.class);
                } catch (Exception e) {

                }
            }
        }

        return result;
    }

    private static String getClientIp(HttpServletRequest request) {
        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-Fowarded-For");

            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-Ip");
            }

            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }
}

