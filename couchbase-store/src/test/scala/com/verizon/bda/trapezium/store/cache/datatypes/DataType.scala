package com.verizon.bda.trapezium.store.cache.datatypes

import java.io.Serializable

@SerialVersionUID(130L)
class DataType( ) extends Serializable {

  var fname = ""
  var lname = ""

  override def equals(that: Any): Boolean = {


    if (fname.equals(that.asInstanceOf[DataType].fname)
      && (lname.equals(that.asInstanceOf[DataType].lname))) {
      return true }
    else
     { return false }

  }

  override
  def toString(): String = {

    return fname + " " + lname

  }

}

