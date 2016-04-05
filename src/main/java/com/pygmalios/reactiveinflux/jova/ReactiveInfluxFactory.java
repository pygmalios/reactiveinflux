package com.pygmalios.reactiveinflux.jova;

import com.pygmalios.reactiveinflux.ReactiveInflux$;
import static com.pygmalios.reactiveinflux.jova.ScalaConversions.*;

public class ReactiveInfluxFactory {
    public static ReactiveInflux fromConfig(ReactiveInfluxConfig javaConfig) {
        return toJava(ReactiveInflux$.MODULE$.apply(toScala(javaConfig)));
    }
}
