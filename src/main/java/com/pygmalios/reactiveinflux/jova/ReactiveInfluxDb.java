package com.pygmalios.reactiveinflux.jova;

import java.util.List;
import java.util.concurrent.Future;

public interface ReactiveInfluxDb {
    Future<Void> create();
    Future<Void> drop();

    Future<Void> write(PointNoTime point);
    Future<Void> write(Iterable<PointNoTime> points);
    Future<Void> write(PointNoTime point, WriteParameters params);
    Future<Void> write(Iterable<PointNoTime> points, WriteParameters params);

    Future<QueryResult> query(Query q);
    Future<QueryResult> query(Query q, QueryParameters params);
    Future<QueryResult> query(List<Query> qs);
    Future<QueryResult> query(List<Query> qs, QueryParameters params);

    ReactiveInfluxConfig getConfig();
}
