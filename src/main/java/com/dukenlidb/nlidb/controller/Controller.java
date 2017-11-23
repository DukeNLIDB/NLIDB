package com.dukenlidb.nlidb.controller;

import com.dukenlidb.nlidb.model.request.ExecuteSQLRequest;
import com.dukenlidb.nlidb.model.request.TranslateNLRequest;
import com.dukenlidb.nlidb.model.response.*;
import com.dukenlidb.nlidb.service.SQLExecutionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.dukenlidb.nlidb.model.DBConnectionConfig;
import com.dukenlidb.nlidb.model.UserSession;
import com.dukenlidb.nlidb.model.request.ConnectDBRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dukenlidb.nlidb.service.CookieService;
import com.dukenlidb.nlidb.service.DBConnectionService;
import com.dukenlidb.nlidb.service.RedisService;
import com.dukenlidb.nlidb.service.TranslationService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import static com.dukenlidb.nlidb.service.CookieService.COOKIE_NAME;
import static com.dukenlidb.nlidb.service.CookieService.USER_NONE;

@RestController
public class Controller {

    private CookieService cookieService;
    private RedisService redisService;
    private DBConnectionService dbConnectionService;
    private SQLExecutionService sqlExecutionService;
    private TranslationService translationService;

    @Autowired
    public Controller(
            CookieService cookieService,
            RedisService redisService,
            DBConnectionService dbConnectionService,
            SQLExecutionService sqlExecutionService,
            TranslationService translationService
    ) {
        this.cookieService = cookieService;
        this.redisService = redisService;
        this.dbConnectionService = dbConnectionService;
        this.sqlExecutionService = sqlExecutionService;
        this.translationService = translationService;
    }

    @RequestMapping("/api/connect/user")
    public ResponseEntity connectUser(
            @CookieValue(value = COOKIE_NAME, defaultValue = USER_NONE) String userId,
            HttpServletResponse res
    ) throws IOException {
        if (userId.equals(USER_NONE) || !redisService.hasUser(userId)) {
            return ResponseEntity.ok(new StatusMessageResponse(false, "No user session found"));
        } else {
            redisService.refreshUser(userId);
            UserSession session = redisService.getUserSession(userId);
            cookieService.setUserIdCookie(res, userId);
            return ResponseEntity.ok(new ConnectResponse(true, session.getDbConnectionConfig().getUrl()));
        }
    }

    @RequestMapping("/api/disconnect")
    public ResponseEntity disconnect(
            @CookieValue(value = COOKIE_NAME, defaultValue = USER_NONE) String userId,
            HttpServletResponse res
    ) {
        if (userId.equals(USER_NONE) || !redisService.hasUser(userId)) {
            return ResponseEntity.status(401).body(new MessageResponse("You are not logged in."));
        } else {
            redisService.removeUser(userId);
            cookieService.expireUserIdCookie(res, userId);
            return ResponseEntity.status(200).body(new MessageResponse("Disconnect successfully."));
        }
    }


    @RequestMapping("/api/connect/db")
    public ResponseEntity connectDB(
            @RequestBody ConnectDBRequest req,
            HttpServletResponse res
    ) throws JsonProcessingException {

        DBConnectionConfig config = DBConnectionConfig.builder()
                .host(req.getHost())
                .port(req.getPort())
                .database(req.getDatabase())
                .username(req.getUsername())
                .password(req.getPassword())
                .build();

        try {
            dbConnectionService.getConnection(config);
            String userId = UUID.randomUUID().toString();
            UserSession session = new UserSession(config);
            redisService.setUserSession(userId, session);
            cookieService.setUserIdCookie(res, userId);
            return ResponseEntity.ok().body(new ConnectResponse(true, config.getUrl()));
        } catch (SQLException e) {
            // TODO: different kinds of connection failure.
            return ResponseEntity.status(400).body(new MessageResponse("Connection Failed!"));
        }
    }

    @RequestMapping("/api/translate/nl")
    public ResponseEntity translateNL(
            @CookieValue(value = COOKIE_NAME, defaultValue = USER_NONE) String userId,
            @RequestBody TranslateNLRequest req
    ) throws IOException {

        if (userId.equals(USER_NONE) || !redisService.hasUser(userId)) {
            return ResponseEntity.status(401).body(new MessageResponse("You are not connected to a Database."));
        }
        UserSession session = redisService.getUserSession(userId);
        String resultString = translationService.translateToSQL(session.getDbConnectionConfig(), req.getInput());
        return ResponseEntity.ok(new TranslateResponse(
            resultString
            //"We are still writing the code to translate your natural language input..."
        ));
    }

    @RequestMapping("/api/execute/sql")
    public ResponseEntity executeSQL(
            @CookieValue(value = COOKIE_NAME, defaultValue = USER_NONE) String userId,
            @RequestBody ExecuteSQLRequest req
    ) throws IOException, SQLException {

        if (userId.equals(USER_NONE) || !redisService.hasUser(userId)) {
            return ResponseEntity.status(401).body(new MessageResponse("You are not connected to a Database."));
        }
        UserSession session = redisService.getUserSession(userId);
        String resultString = sqlExecutionService.executeSQL(session.getDbConnectionConfig(), req.getQuery());
        return ResponseEntity.ok(new QueryResponse(resultString));
    }
}
