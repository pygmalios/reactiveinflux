package com.pygmalios.reactiveinflux;

import java.io.Serializable;
import java.util.List;

public interface JavaResult extends Serializable {
    List<JavaSeries> getSeries();
}
