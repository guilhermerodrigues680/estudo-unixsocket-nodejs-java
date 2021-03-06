package com.example.unixsocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class UnixSocketEvent {

    private String type;
    private UnixSocketData data;

}
