package com.scala.chapter1

import scala.io.Source

/**
  * User: sunilkumar.ashok
  * Date: 3/17/2017
  */
class Utility {

  def printMatchingLine : Unit = {

    val filename = "C:\\NMI\\Axis-O\\code\\scripts\\db\\patches\\V1767__AEP-11611.sql"
    for (line <- Source.fromFile(filename).getLines) {
      line.contains("ALTER")
      println(line)
    }

  }



}
