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
package com.verizon.bda.datasimulation

import com.typesafe.config.Config
import com.verizon.bda.trapezium.framework.manager.WorkflowConfig
import com.verizon.bda.utils.Util
import org.apache.spark.sql.DataFrame
import org.slf4j.LoggerFactory

/**
 * Created by parmana on 9/22/16.
 */
object Simulation {
  val logger = LoggerFactory.getLogger(this.getClass)

  def simulation(df : DataFrame, workflowConfig : WorkflowConfig): Unit = {
    val conf = workflowConfig.workflowConfig.getConfig("dataSimulationInfo")
    val runmode = conf.getString("simulationMode")

    runmode match {
      case "hdfs" => {
        logger.info("Mode is " + runmode)
        saveToHdfs(df, conf)
      }
      case "hdfsAndKafka" => {
        logger.info("Mode is " + runmode)
        saveToKafkaAndHdfs(df, conf)
      }
      case "kafka" => {
        logger.info("Mode is " + runmode)
        saveToKafka(df , conf)
      }
      case _ => {
        logger.error("Mode not implemented. Exiting..." , runmode)
        System.exit(1)
      }
    }
  }

  def saveToHdfs(df : DataFrame , conf: Config) : Unit = {
    val starttime = System.currentTimeMillis()
    val outputpath = conf.getString("outputpath")
    val interval = conf.getInt("interval")
    val path = outputpath + "/" + System.currentTimeMillis()
    df.rdd.map(x => x.mkString(", ")).saveAsTextFile(path)
    logger.info("data saved to hdfs ")
    val endtime = System.currentTimeMillis()
    val sleep = 1000*interval-(endtime-starttime)
    logger.info("Sleep == " + sleep)
    if(sleep>0){
      Thread.sleep(sleep)
    } else {
      logger.error("Simulation is too slow not able to write data " +
        "in interval define " + interval ,
        logger.getStackTrace(new Exception("Simulation is too slow not able " +
          " to write data in interval define " + interval)))
      df.rdd.sparkContext.stop()
    }
  }

  def saveToKafka(df : DataFrame , conf: Config): Unit = {

  }

  def saveToKafkaAndHdfs(df : DataFrame, conf : Config) : Unit = {

  }
}
