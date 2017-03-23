package com.verizon.bda.trapezium.store.cache.datatypes

/**
  * Created by v468328 on 9/30/16.
  */
@SerialVersionUID(150L)
class NestedDataType() extends Serializable {

  var testDataType: DataType = null
  val ipaddr: Array[Byte] = Array(192.toByte, 168.toByte, 1.toByte, 9.toByte)
  var middleName = ""
  var number: Integer = null

  override def equals(that: Any): Boolean = {


    if (testDataType.equals(that.asInstanceOf[NestedDataType].testDataType) &&
      middleName.equals(that.asInstanceOf[NestedDataType].middleName)
      && (number.equals(that.asInstanceOf[NestedDataType].number))) {
      return true
    }
    else {
      return false
    }

  }

  override
  def toString(): String = {

    return testDataType + " " + middleName + " " + ipaddr.toList.toString()

  }


}
