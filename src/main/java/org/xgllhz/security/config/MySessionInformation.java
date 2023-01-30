package org.xgllhz.security.config;

import org.springframework.security.core.session.SessionInformation;

import java.util.Date;

/**
 * @author: XGLLHZ
 * @date: 2022/8/17 15:55
 * @description: custom session information
 */
public class MySessionInformation extends SessionInformation {

    private static final long serialVersionUID = 7136764596787584258L;

    private boolean expireState;

    public MySessionInformation(Object principal, String sessionId, Date lastRequest, boolean expireState) {
        super(principal, sessionId, lastRequest);
        this.expireState = expireState;
    }

    @Override
    public void expireNow() {
        String sessionId = getSessionId();
        MySessionInformation sessionInformation = MySessionRegistry.getSession(sessionId);
        if (sessionInformation != null) {
            super.expireNow();
            sessionInformation.setExpireState(true);
            MySessionRegistry.updateSessionState(sessionInformation);
        }
    }

    @Override
    public boolean isExpired() {
        return this.expireState;
    }

    public boolean isExpireState() {
        return expireState;
    }

    public void setExpireState(boolean expireState) {
        this.expireState = expireState;
    }

}
