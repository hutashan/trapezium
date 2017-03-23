package com.verizon.bda.trapezium.store.cache

import java.util


import org.couchbase.mock.{BucketConfiguration, CouchbaseMock}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.couchbase.mock.client.{MockClient, MockHttpClient}
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactoryBuilder;
import org.couchbase.mock.Bucket.BucketType;
import org.couchbase.mock.BucketConfiguration;
import org.couchbase.mock.CouchbaseMock;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
  * Created by v468328 on 9/30/16.
  */
object CouchBaseTestFactory  {

  initJCBCEnv();
  protected var cfb: CouchbaseConnectionFactoryBuilder = new CouchbaseConnectionFactoryBuilder();
  protected var bucketConfiguration: BucketConfiguration = new BucketConfiguration();
  protected var couchbaseMock = createMock("GlobalApps", "")
  couchbaseMock.start();
  couchbaseMock.waitForStartup();
  protected var mockClient = new MockClient(new InetSocketAddress("localhost", 0));
  couchbaseMock.startHarakiriMonitor("localhost:" + mockClient.getPort(), false);
 // mockClient.negotiate();

  private var uriList: List[URI] = new ArrayList[URI]();
  uriList.add(new URI("http", null, "localhost", couchbaseMock.getHttpPort(), "/pools", "", ""));
  protected var connectionFactory = cfb.buildCouchbaseConnection(uriList,
    bucketConfiguration.name, bucketConfiguration.password);
  var client = new CouchbaseClient(connectionFactory);



  /**
    * Need to shutdown Cassandra and Embeded Cassandra. As SessionManager is
    * object it will shutdown for all instances of Cassandra DAOs
    */
    def shutdown(): Unit = {
    if (client != null) {
      client.shutdown();
    }
    if (couchbaseMock != null) {
      couchbaseMock.stop();
    }
    if (mockClient != null) {
      mockClient.shutdown();
    }



  }

  def createMock(name: String, password: String): CouchbaseMock = {

    bucketConfiguration.numNodes = 1;
    bucketConfiguration.numReplicas = 1;
    bucketConfiguration.name = name;
    bucketConfiguration.`type` = BucketType.COUCHBASE;
    bucketConfiguration.password = password;
    bucketConfiguration.hostname = "localhost";
    var configList: util.ArrayList[BucketConfiguration] = new util.ArrayList[BucketConfiguration]();
    configList.add(bucketConfiguration);
    var mockCouchbase: CouchbaseMock = new CouchbaseMock(0, configList);
    return mockCouchbase;
  }


  // Don't make the client flood the screen with log messages..
  def initJCBCEnv(): Unit = {
    System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.SunLogger");
    System.setProperty("cbclient.disableCarrierBootstrap", "true");

  }






}
