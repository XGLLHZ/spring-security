package org.xgllhz.security.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.xgllhz.security.exception.GlobalException;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * @author: XGLLHZ
 * @date: 2022/7/13 23:16
 * @description: response util
 */
@Component
public class ResponseUtils {

    private static ObjectMapper objectMapper;

    public ResponseUtils(ObjectMapper objectMapper) {
        ResponseUtils.objectMapper = objectMapper;
    }

    public static void responseJson(HttpServletResponse response, APIResponse<?> apiResponse) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            response.getWriter()
                    .write(objectMapper.writeValueAsString(apiResponse));
        } catch (Exception e){
            throw new GlobalException("response writing exception", e);
        }
    }

}
