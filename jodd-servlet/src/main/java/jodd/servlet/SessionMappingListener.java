// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.
package jodd.servlet;

import jodd.util.ref.ReferenceMap;
import jodd.util.ref.ReferenceType;

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * Holds a Map of HttpSessions for sharing between the portlet and servlet.
 * The Map uses weak references to the HttpSession objects to ensure this
 * class does not cause any memory leaks if a session is not removed from
 * the map at the appropriate time.
 */
public class SessionMappingListener implements HttpSessionListener {

    @SuppressWarnings({"unchecked"})
    private static final Map<String, HttpSession> sessionMap = new ReferenceMap(ReferenceType.STRONG, ReferenceType.WEAK);
    
    /** 
     * Gets the session with the specified ID.
     */
    public static HttpSession getSession(final String sid) {
        return sessionMap.get(sid);
    }
    
    /**
     * Stores a session.
     */
    public static void setSession(final HttpSession session) {
	    sessionMap.put(session.getId(), session);
        
    }
    
    /**
     * Removes a session with the specified ID.
     */
    public static HttpSession removeSession(final String sid) {
	    return sessionMap.remove(sid);
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionCreated(HttpSessionEvent event) {
	    setSession(event.getSession());
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    public void sessionDestroyed(HttpSessionEvent event) {
	    removeSession(event.getSession().getId());
    }
}
