package com.pygmalios.reactiveinflux.jova;

import com.typesafe.config.Config;

import java.io.Serializable;
import java.net.URI;

public interface ReactiveInfluxConfig extends Serializable {
    URI getUrl();
    String getUsername();
    String getPassword();
    Config getConfig();
}
