package org.xgllhz.security.handler;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;
import org.xgllhz.security.util.APIResponse;
import org.xgllhz.security.util.ResponseUtils;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author: XGLLHZ
 * @date: 2022/8/13 15:52
 * @description: custom session expired handler
 */
@Component
public class MySessionExpiredHandler implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        ResponseUtils.responseJson(event.getResponse(), APIResponse.failure("你说尼玛呢"));
    }

}
