package com.pygmalios.reactiveinflux.jova.wrapper;

import com.pygmalios.reactiveinflux.jova.ReactiveInflux;

public class WrapperFactory {
    public static ReactiveInflux reactiveInflux(com.pygmalios.reactiveinflux.ReactiveInflux reactiveInflux) {
        return new JavaReactiveInflux(reactiveInflux);
    }
}
