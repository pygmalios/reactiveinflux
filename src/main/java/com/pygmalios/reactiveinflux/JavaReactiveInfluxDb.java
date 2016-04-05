package com.pygmalios.reactiveinflux;

import java.util.List;
import java.util.concurrent.Future;

public interface JavaReactiveInfluxDb {
    Future<Void> create();
    Future<Void> drop();

    Future<Void> write(JavaPointNoTime point);
    Future<Void> write(Iterable<JavaPointNoTime> points);
    Future<Void> write(JavaPointNoTime point, JavaWriteParameters params);
    Future<Void> write(Iterable<JavaPointNoTime> points, JavaWriteParameters params);

    Future<JavaQueryResult> query(JavaQuery q);
    Future<JavaQueryResult> query(JavaQuery q, JavaQueryParameters params);
    Future<JavaQueryResult> query(List<JavaQuery> qs);
    Future<JavaQueryResult> query(List<JavaQuery> qs, JavaQueryParameters params);

    JavaReactiveInfluxConfig getConfig();
}
