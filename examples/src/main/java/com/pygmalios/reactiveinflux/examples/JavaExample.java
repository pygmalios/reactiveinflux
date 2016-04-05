package com.pygmalios.reactiveinflux.examples;

import com.pygmalios.reactiveinflux.jawa.ReactiveInflux;
import com.pygmalios.reactiveinflux.jawa.ReactiveInfluxFactory;

public class JavaExample {
    public static void main(String[] args) {
        ReactiveInflux reactiveInflux = ReactiveInfluxFactory.fromConfig(null);
    }
}
