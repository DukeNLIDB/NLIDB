package com.dukenlidb.nlidb.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserSessionTest {

    private UserSession session;

    @Before
    public void init() {
        DBConnectionConfig config = DBConnectionConfig
                .builder()
                .host("hostname")
                .port("portnum")
                .database("db")
                .username("username1")
                .password("passwd")
                .build();
        session = new UserSession(config);
    }

    @Test
    public void serialize() throws Exception {
        session.serialize();
    }

    @Test
    public void deserialize() throws Exception {
        assertEquals(session, UserSession.deserialize(session.serialize()));
    }

}