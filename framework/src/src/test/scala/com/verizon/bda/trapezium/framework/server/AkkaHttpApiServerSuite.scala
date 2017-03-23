/**
* Copyright (C) 2016 Verizon. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.verizon.bda.trapezium.framework.server

import com.verizon.bda.trapezium.cache.CacheConfig
import com.verizon.bda.trapezium.framework.ApplicationManager
import com.verizon.bda.trapezium.framework.manager.ApplicationConfig
import com.verizon.bda.trapezium.framework.server.directives.CacheRouteUtils
import com.verizon.bda.trapezium.framework.zookeeper.ZooKeeperConnection
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.spark.zookeeper.EmbeddedZookeeper
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.apache.commons.httpclient.Header
import org.slf4j.LoggerFactory
;

/**
  * Created by Pankaj on 8/10/16.
  * Modified by Faraz 0n 08/10/16
  */
class AkkaHttpApiServerSuite extends FunSuite with BeforeAndAfterAll {
  val logger = LoggerFactory.getLogger(this.getClass)

  var appConfig: ApplicationConfig = _
  var zk: EmbeddedZookeeper = null

  val args = Array("--workflow", "akkaHttpApiServer")

  override def beforeAll(): Unit = {
    appConfig = ApplicationManager.getConfig()

    // set up ZooKeeper Server
    zk = new EmbeddedZookeeper(appConfig.zookeeperList.split(",")(0))
    ApplicationManager.main(args)
  }

  override def afterAll: Unit = {
    // Close ZooKeeper connections
    ZooKeeperConnection.close

    if (zk != null) {
      zk.shutdown()
      zk = null
    }

    // stop HTTP server if started
    if (ApplicationManager.getEmbeddedServer != null) {

      logger.info(s"Stopping embedded server")
      ApplicationManager.getEmbeddedServer.stop(true)
    }
  }

  test("configured end-points should work for API tests with or without cache") {
    val client = new HttpClient

    val port = ApplicationManager.getEmbeddedServer.getBindPort
    val method1 = new GetMethod(s"http://localhost:${port}/rest-api/test")
    method1.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0987")

    client.executeMethod(method1)
    assert(method1.getResponseBodyAsString.contains("Hello from EndPoint 1"))

    // now execute the method 1 again. This time it should hit cache. look for output in cache

    assert(method1.getResponseBodyAsString.contains("Hello from EndPoint 1"))
    client.executeMethod(method1)
    method1.getResponseHeaders
    val method2 = new GetMethod(s"http://localhost:${port}/rest-api/test2")

    method2.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0660")
    // logger.debug("request headers we have are " + printheaders(method2.getResponseHeaders))
    client.executeMethod(method2)
    // logger.debug("reponse headers we have are " + printheaders(method2.getResponseHeaders))
    assert(method2.getResponseBodyAsString.contains("Hello from EndPoint 2"))


    // now execute method 1 again. This should hit the cache. Now even api
    method2.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0987")
    client.executeMethod(method2)
   // key is same response should be different as uri path is different
    assert(!method2.getResponseBodyAsString.contains("Hello from EndPoint 1"))

  }

  test("configured end-points should through error if caching is off") {

    CacheConfig.loadConfigFromResource("local2_cache.conf")
    val client = new HttpClient

    val port = ApplicationManager.getEmbeddedServer.getBindPort
    val method1 = new GetMethod(s"http://localhost:${port}/rest-api/test")
    method1.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0987")

    client.executeMethod(method1)
    assert(method1.getResponseBodyAsString.contains("Hello from EndPoint 1"))

    val method2 = new GetMethod(s"http://localhost:${port}/rest-api/test2")
    method2.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0660")
    // logger.debug("request headers we have are " + printheaders(method2.getResponseHeaders))
    client.executeMethod(method2)
    // logger.debug("reponse headers we have are " + printheaders(method2.getResponseHeaders))
    assert(method2.getResponseBodyAsString.contains("Hello from EndPoint 2"))


    // now execute method 1 again. This should hit the cache. look at output in console
    method2.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM0987")
    client.executeMethod(method2)
    // Because caching is off therefore even we are usimh same cache-id it should
    // ignore cache-id altogether.
    assert(method2.getResponseBodyAsString.contains("Hello from EndPoint 2"))


  }

  test("configured end-points should work for API tests with cache and headers") {
    val client = new HttpClient

    val port = ApplicationManager.getEmbeddedServer.getBindPort
    val method1 = new GetMethod(s"http://localhost:${port}/rest-api/actortestjson")
    method1.setRequestHeader(CacheRouteUtils.CACHE_ID, "8765767NHM098F")
    method1.setRequestHeader("Content-Type", "application/json")

    logger.debug("request headers we have are " + printheaders(method1.getRequestHeaders))

    val jsonResponse =
      "[{ \"popularSite\": \"pillsbury.com\"," +
        "\"rank\": 1, " + "}]";

    client.executeMethod(method1)
    assert(method1.getResponseBodyAsString.contains(jsonResponse))

    // now execute the method 1 again. This time it should hit cache. look for output in cache

    logger.debug("response headers we have are " + printheaders(method1.getResponseHeaders))

  }

  private def printheaders(headers: Array[Header]): String = {

    val response_headers: StringBuilder = new StringBuilder()

    headers.foreach(header => {


      response_headers.append("header => " + header.toString)

    })

    return response_headers.toString()
  }

}
