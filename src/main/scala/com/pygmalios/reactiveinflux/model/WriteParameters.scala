package com.pygmalios.reactiveinflux.model

import com.pygmalios.reactiveinflux.command.{Consistency, Precision}

case class WriteParameters(retentionPolicy: Option[String] = None,
                           precision: Option[Precision] = None,
                           consistency: Option[Consistency] = None)