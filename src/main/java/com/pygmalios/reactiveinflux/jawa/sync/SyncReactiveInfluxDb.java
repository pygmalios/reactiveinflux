package com.pygmalios.reactiveinflux.jawa.sync;

import com.pygmalios.reactiveinflux.jawa.*;

import java.util.List;

public interface SyncReactiveInfluxDb {
    void create();
    void drop();

    void write(PointNoTime point);
    void write(Iterable<PointNoTime> points);
    void write(PointNoTime point, WriteParameters params);
    void write(Iterable<PointNoTime> points, WriteParameters params);

    QueryResult query(String q);
    QueryResult query(Query q);
    QueryResult query(Query q, QueryParameters params);
    List<QueryResult> query(List<Query> qs);
    List<QueryResult> query(List<Query> qs, QueryParameters params);

    ReactiveInfluxConfig getConfig();
}
