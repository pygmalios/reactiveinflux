package com.pygmalios.reactiveinflux.jawa;

import com.pygmalios.reactiveinflux.ReactiveInflux$;

import static com.pygmalios.reactiveinflux.jawa.Conversions.toJava;
import static com.pygmalios.reactiveinflux.jawa.Conversions.toScala;

public class ReactiveInfluxFactory {
    private ReactiveInfluxFactory() {
    }

    public static ReactiveInflux fromConfig(final ReactiveInfluxConfig javaConfig) {
        return toJava(ReactiveInflux$.MODULE$.apply(toScala(javaConfig)));
    }
}
