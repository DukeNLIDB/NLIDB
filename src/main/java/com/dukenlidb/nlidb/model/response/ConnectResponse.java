package com.dukenlidb.nlidb.model.response;

import lombok.Value;

@Value
public class ConnectResponse {

    boolean success;
    String databaseUrl;

}
