package com.pygmalios.reactiveinflux;

import com.typesafe.config.Config;

import java.io.Serializable;
import java.net.URI;

public interface JavaReactiveInfluxConfig extends Serializable {
    URI getUrl();
    String getUsername();
    String getPassword();
    Config getConfig();
}
