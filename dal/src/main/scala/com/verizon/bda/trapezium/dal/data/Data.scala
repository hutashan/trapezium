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
/**
 * Copyright (C) Verizon Corp.
 */
package com.verizon.bda.trapezium.dal.data

import org.apache.log4j.Logger

/**
 * Trait for common data model. Here <code>env</code> is read from the cluster.
 *
 * @author pramod.lakshminarasimha
 */
trait Data[T] {

  protected lazy val log = Logger.getLogger(this.getClass.getName)

  /**
   * All the columns from source are returned as batch.
   *
   * @return T distributed collection of data containing all columns.
   */
  protected def getAll(): T

  /**
   * The requested list of columns are returned as batch.
   *
   * @param cols List of columns to return
   * @return T distributed collection of data containing the requested columns.
   */
  protected def getColumns(cols: List[String]): T

  /**
   * Write the batch of records (eg. Rows) into the data store.
   *
   * @param data distributed collection of data to be persisted of type T
   */
  protected def write(data: T): Unit

}

