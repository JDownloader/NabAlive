package com.nabalive.server.jabber;

import com.nabalive.common.server.Event;
import com.nabalive.common.server.EventListener;
import com.nabalive.server.jabber.util.Jid;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Julien Cheype
 * Date: 11/15/11
 */
public class Status {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean authenticated;
    private String username;
    private final ChannelHandlerContext context;

    private List<EventListener> eventListenerList = new ArrayList<EventListener>();
    private Jid jid;
    private String presence;

    public Status(ChannelHandlerContext ctx, List<EventListener> eventListeners) {
        this.context = ctx;
        this.eventListenerList = eventListeners;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public ChannelHandlerContext getContext() {
        return context;
    }

    public Jid getJid() {
        return jid;
    }

    public void setJid(Jid jid) {
        this.jid = jid;
    }

    public void registerEvent(EventListener eventListener) {
        synchronized (eventListenerList) {
            eventListenerList.add(eventListener);
        }
    }

    public void onEvent(Event event) {
        synchronized (eventListenerList) {
            Iterator<EventListener> iterator = eventListenerList.iterator();
            while (iterator.hasNext()) {
                EventListener listener = iterator.next();
                try {
                    listener.onEvent(jid.getUser(), event);
                } catch (Exception e) {
                    logger.debug("eventListener exception (unregister): ", e);
                    iterator.remove();
                }
            }
        }
    }

    public void write(String data) {
        logger.debug(">>>>>>>>>> " + data);
        context.getChannel().write(ChannelBuffers.copiedBuffer(data.getBytes(CharsetUtil.UTF_8)));
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    public boolean isAsleep(){
        return "asleep".equalsIgnoreCase(presence);
    }

    public boolean isIdle(){
        return "idle".equalsIgnoreCase(presence);
    }
}
