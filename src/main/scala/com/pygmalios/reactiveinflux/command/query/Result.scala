package com.pygmalios.reactiveinflux.command.query

trait QueryResult {
  def q: Query
  def result: Result
}

trait Result {
}
