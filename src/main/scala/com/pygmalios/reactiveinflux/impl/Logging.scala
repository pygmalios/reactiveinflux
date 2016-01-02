package com.pygmalios.reactiveinflux.impl

import org.slf4j.LoggerFactory

/**
  * Logging mix-in.
  */
trait Logging {
  protected val log = LoggerFactory.getLogger(getClass)
}
