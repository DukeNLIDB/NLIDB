package com.dukenlidb.nlidb.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class CookieService {

    public static final String COOKIE_NAME = "nlidbUser";
    public static final String USER_NONE = "none";

    public void setUserIdCookie(HttpServletResponse res, String userId) {
        Cookie cookie = new Cookie(COOKIE_NAME, userId);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600 * 24);
        cookie.setPath("/");
        res.addCookie(cookie);
    }

    public void expireUserIdCookie(HttpServletResponse res, String userId) {
        Cookie cookie = new Cookie(COOKIE_NAME, userId);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        res.addCookie(cookie);
    }

}
