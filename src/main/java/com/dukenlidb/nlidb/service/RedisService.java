package com.dukenlidb.nlidb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.dukenlidb.nlidb.model.UserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;

@Service
public class RedisService {

    private Jedis jedis;

    @Autowired
    public RedisService(
            @Value("${redis.host}") String host,
            @Value("${redis.port}") int port
    ) {
        jedis = new Jedis(host, port);
    }

    public boolean hasUser(String userId) {
        return jedis.exists(userId);
    }

    public void removeUser(String userId) {
        jedis.del(userId);
    }

    public void refreshUser(String userId) {
        jedis.expire(userId, 3600 * 24);
    }

    public UserSession getUserSession(String userId)
            throws IOException {
        String sessionStr = jedis.get(userId);
        return UserSession.deserialize(sessionStr);
    }

    public void setUserSession(String userId, UserSession session)
            throws JsonProcessingException {
        jedis.set(userId, session.serialize());
        jedis.expire(userId, 3600 * 24);
    }

}
