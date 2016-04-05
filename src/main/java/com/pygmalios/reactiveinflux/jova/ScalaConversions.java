package com.pygmalios.reactiveinflux.jova;

import com.pygmalios.reactiveinflux.ReactiveInfluxConfig$;
import com.pygmalios.reactiveinflux.jova.wrapper.WrapperFactory;
import scala.Option;

/**
 * Conversions between Java and Scala types.
 */
public class ScalaConversions {
    public static com.pygmalios.reactiveinflux.ReactiveInfluxConfig toScala(ReactiveInfluxConfig reactiveInfluxConfig) {
        return ReactiveInfluxConfig$.MODULE$.apply(
                reactiveInfluxConfig.getUrl(),
                Option.<String>apply(reactiveInfluxConfig.getUsername()),
                Option.<String>apply(reactiveInfluxConfig.getPassword()));
    }

    public static ReactiveInflux toJava(com.pygmalios.reactiveinflux.ReactiveInflux reactiveInflux) {
        return WrapperFactory.reactiveInflux(reactiveInflux);
    }
}
