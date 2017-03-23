package com.verizon.bda.trapezium.store.cache

import com.verizon.bda.trapezium.cache.{CommonCacheSessionFactory, CacheConfig}
import com.verizon.bda.trapezium.store.cache.datatypes.{NestedDataType, DataType}
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.slf4j.LoggerFactory
import scala.collection.mutable

/**
  * Couch Base unit tests for basic CRUD (read, write, update and delete.
  */
class CommonsCacheTestSuite  extends FunSuite with BeforeAndAfterAll  {
  val logger = LoggerFactory.getLogger(this.getClass)

  override def beforeAll() {
    super.beforeAll()
    // initialize configs
    CacheConfig.loadConfigFromResource("cache.conf")
  }

  test("Cache Connectivity Test For Global with simple get/put") {


    val factory: CommonCacheSessionFactory[String, String] =
      new CommonCacheSessionFactory[String, String]

    val cache = factory.getCache()
    logger.debug("cache we have " + cache)
    val testValue = "cache-world"
    cache.put("hello", testValue)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get("hello"))
    assert(Some(testValue).equals(cache.get("hello")))


  }

  test("Cache get with complex data type ") {


    val factory: CommonCacheSessionFactory[DataType, String] =
      new CommonCacheSessionFactory[DataType, String]

    val cache = factory.getCache()
    logger.debug("cache we have " + cache)
    val testKey: DataType = new DataType()
    testKey.fname = "Faraz"
    testKey.lname = "Waseem"
    val testValue = "hello-generic-cache-world"
    cache.put(testKey, testValue)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get(testKey))
    assert(Some(testValue).equals(cache.get(testKey)))

  }


  test("Cache get with nesteded complex data type ") {


    val factory: CommonCacheSessionFactory[ String, NestedDataType] = new
        CommonCacheSessionFactory[String, NestedDataType]

    val cache = factory.getCache()
    logger.debug("cache we have " + cache)
    val testValue: DataType = new DataType()
    testValue.fname = "Faraz"
    testValue.lname = "Waseem"

    var nestedTestValue: NestedDataType = new NestedDataType
    nestedTestValue.testDataType = testValue
    nestedTestValue.middleName = "great"
    nestedTestValue.number = 8
    val testKey = "hello-generic-cache-world"

    logger.debug("response from cache --> " + nestedTestValue.toString)
   cache.put(testKey, nestedTestValue)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get(testKey).toString)
    assert(nestedTestValue.equals(cache.get(testKey).get))

  }

  test("Cache put with complex build-in data type ") {


    val factory: CommonCacheSessionFactory[String, Integer] =
      new CommonCacheSessionFactory[String, Integer]

    val cache = factory.getCache()
    val number: Integer = new Integer(6)

    logger.debug("cache we have " + cache)

    val testKey = "hello-world"
    cache.put(testKey, number)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get(testKey))
    assert(Some(number).equals(cache.get(testKey)))


  }

  test("Cache put with custom complex  data type ") {


    val factory: CommonCacheSessionFactory[String, DataType] =
      new CommonCacheSessionFactory[String, DataType]

    val cache = factory.getCache()


    logger.debug("cache we have " + cache)

    val datatype2: DataType = new DataType()
    datatype2.fname = "Faraz"
    datatype2.lname = "Waseem"

    logger.debug("before putting in cache for hello " + datatype2)

    val testKey = "hello-world"
    cache.put(testKey, datatype2)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get(testKey))
    assert(datatype2.equals(cache.get(testKey).get))


  }


  test("test get and put with ttl field") {


    val factory: CommonCacheSessionFactory[String, String] =
      new CommonCacheSessionFactory[String, String]

    val cache = factory.getCache()
    logger.debug("cache we have " + cache)
    val testValue = "cache-world"
    cache.put("hello", testValue, 6)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get("hello").get)


    // now wait for 8 seconds and we should not be able to get response back.

    try {
      Thread.sleep(7000); // 60 seconds.
    }
    catch {
      case e: Exception => {
        logger.info("exception we got is " + e)
      }
    }


    logger.debug("response from cache for hello after TTL timeout " + cache.get("hello"))
    assert(cache.get("hello") == None)

  }

  test("delete a key from cache") {

    val factory: CommonCacheSessionFactory[String, String] =
      new CommonCacheSessionFactory[String, String]
    val cache = factory.getCache()
    logger.debug("cache we have " + cache)
    val testValue = "cache-world"
    cache.put("hello", testValue, 7)

    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + cache.get("hello"))
    assert(testValue.equals(cache.get("hello").get))

    // now delete this key from cache
    cache.delete("hello")

    // now assert again
    assert(None == cache.get("hello")) // it should be null after deletaion.


  }


  test("test multi get and multi put") {


    val factory: CommonCacheSessionFactory[String, String] =
      new CommonCacheSessionFactory[String, String]

    val cache = factory.getCache()

    val map: scala.collection.mutable.HashMap[String, String] =
      new mutable.HashMap[String, String]()
    map.put("hello", "cache-world")
    map.put("hello2", "cache-world2")
    map.put("hello3", "cache-world3")


    cache.putMulti(map.toMap) // convert into immutable map

    // now create a key List

    var list: mutable.MutableList[String] = new mutable.MutableList[String]
    list += "hello"
    list += "hello2"
    list += "hello3"

    val listFromCache = cache.getMulti(list)
    // now try to get hello world back from cache
    logger.debug("response from cache for hello " + listFromCache)
    logger.debug("response from cache for hello " + listFromCache("hello"))

    assert(Some("cache-world").equals(listFromCache("hello")))
    assert(Some("cache-world2").equals(listFromCache("hello2")))
    assert(Some("cache-world3").equals(listFromCache("hello3")))

  }

  /**
    * Need to shutdown Cassandra and Embeded Cassandra. As SessionManager is
    * object it will shutdown for all instances of Cassandra DAOs
    */
  override def afterAll(): Unit = {
    super.afterAll()

  }




}
