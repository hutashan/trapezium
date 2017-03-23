package com.verizon.bda.trapezium.store.cache.mock

import com.verizon.bda.trapezium.store.cache.CouchBaseTestFactory
import com.verizon.bda.trapezium.store.{CouchBaseStoreImpl, CouchConnection}


/**
  * Created by v468328 on 10/3/16.
  */
class MockCacheCBImpl[K, V] extends CouchBaseStoreImpl[K, V] {

  override
  def init(params : String*): Unit = {

    couchConnection = new CouchConnection(CouchBaseTestFactory.client)
    // override. testing purpose hook

  }



}
