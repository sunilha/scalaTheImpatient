package com.scala.chapter1

import java.io._
import java.util

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * User: sunilkumar.ashok
  * Date: 3/17/2017
  */
object UtilityObject {

  def main(args: Array[String]): Unit = {

    //CREATE, ALTER, DROP, RENAME, INSERT, UPDATE
    findMatchingResults("C:\\NMI\\Axis-O\\code\\scripts\\db\\patches\\R9\\","RENAME")

    //reformatData(new File("C:\\NMI\\Axis-O\\code\\scripts\\db\\patches\\V1771__US12237_US12236.sql"))

  }

  def findMatchingResults(dirName: String, matchingWord: String): Unit= {
    /*if(matchingWord.equals("CREATE")){
      val createTableList = findMatchingLineInDirectory(dirName,matchingWord)
      val dropIfList = findMatchingLineInDirectory(dirName,"EXISTS")

      println("Number Table Created"+createTableList.diff(dropIfList).toList.size)
      println("Table Created"+createTableList.diff(dropIfList).toList)
      println("Table Created"+createTableList.intersect(dropIfList).toList.size)
      println("Number Table dropped"+dropIfList.diff(createTableList).toList.size)
      println("Table dropped"+dropIfList.diff(createTableList).toList)

    } else*/ if(matchingWord.equals("DROP")){
      var dropTableList = findMatchingLineInDirectory(dirName, matchingWord)

      val createTableList = findMatchingLineInDirectory(dirName,"CREATE")
      val dropIfList = findMatchingLineInDirectory(dirName,"EXISTS")

      dropTableList = dropTableList.union(dropIfList.diff(createTableList))

      println("asdfa: " + dropTableList.size)
      println("asdfa: " + dropTableList)
    }

    else {
      findMatchingLineInDirectory(dirName,matchingWord)
    }
  }

  def findMatchingLineInDirectory(dirName: String, matchingWord: String): ListBuffer[String]= {
    val directory = new File(dirName);
    val list = new ListBuffer[String]()
    if(directory.exists() && directory.isDirectory) {
      val fileName = matchingWord.concat("_matching_lines")
      val outputFile = createOutputFile(fileName)
      println("Writing to output file @" +outputFile.getAbsolutePath)
      directory.listFiles().foreach (file => {
        printMatchingLine(file, matchingWord, outputFile, list)
      })

      println("Finished"+ list.size)
    }
    return list;

  }

  def printMatchingLine (inputFile: File, matchingWord: String, outputFile: File, list:ListBuffer[String]) = {
    val reformatFile= getFormatDataFile(inputFile)
    val bf = Source.fromFile(reformatFile.getAbsolutePath)

    val fileWriter =new FileWriter(outputFile, true)
    val bw =new BufferedWriter(fileWriter)

    for (line <- bf.getLines) {
      if(isLineMatching(line.trim.toUpperCase, matchingWord.toUpperCase)) {
        //println(line)
        //fileWriter.append("*********"+inputFile.getName+"********* ")
        writeToOutputFile(fileWriter, line.trim, matchingWord)
        list += (getFormattedContent(line.trim, matchingWord).trim)
        //fileWriter.append("****************** ")
      }
    }

    bw.flush();
    fileWriter.flush();
    bw.close();
    fileWriter.close();
  }

  def isLineMatching(data: String, matchWord: String): Boolean = matchWord match {
    case "CREATE" => return data.startsWith(matchWord)  && data.contains("CREATE TABLE")
    case "ALTER" => return data.startsWith(matchWord) && data.contains("ALTER TABLE")
    case "RENAME" => return data.startsWith(matchWord) && data.contains("RENAME TABLE")
    case "DROP" => return (data.startsWith(matchWord) && data.contains("TABLE") &&  !(data.contains("IF EXISTS") && data.contains("EXISTS")))
    case "EXISTS" => return (data.contains(matchWord) && data.startsWith("DROP")&& data.contains("TABLE") &&  (data.contains("IF EXISTS") && data.contains("EXISTS")))
    case "INSERT" => return (data.startsWith(matchWord) && data.contains("_XREF"))
    case "UPDATE" => return (data.startsWith(matchWord) && data.contains("_XREF"))
    case _ => return false
  }

  def isValidLine(data: String): Boolean ={
    if(data.isEmpty || data.startsWith("#") || data.startsWith("--")) {
      return false;
    }
    return true;
  }

  def getFormatDataFile(inputFile: File):File = {
    val bf = Source.fromFile(inputFile.getAbsolutePath)

    val file = new File("C:\\NMI\\MyProjects\\scalaTheImpatient\\Chapter1\\temp\\RefactoredFile".concat(inputFile.getName));
    if(!file.exists()){
      file.createNewFile()
    }
    val fileWriter = new FileWriter(file)

    for (line <- bf.getLines) {
      if (isValidLine(line)) {
        val formattedLine: String = line.trim.replace("`","").replaceAll("\\s+"," ")
        fileWriter.write(formattedLine)
        if (formattedLine.endsWith(";")) {
          fileWriter.append("\n")
        }
        fileWriter.append(" ")
      }
    }

    val bw =new BufferedWriter(fileWriter)

    bw.flush();
    fileWriter.flush();
    bw.close();
    fileWriter.close();

    return file;
  }


  def createOutputFile(fileName:String):File = {
    val file = new File(fileName);
    if(file.exists()){
      file.createNewFile();
    }
    println("File created @ " +file.getAbsolutePath)
    val inputStream =new BufferedOutputStream(new FileOutputStream(file))
    inputStream.flush();
    inputStream.close();

    return file;
  }

  def writeToOutputFile(fileWriter:FileWriter, data:String, matchWord: String):Unit = {

    fileWriter.append(getFormattedContent(data, matchWord))
    fileWriter.append("\n")

  }


  def getFormattedContent(line: String,  matchWord: String) : String = matchWord.toUpperCase match {
    case "CREATE" => return  line.replaceFirst("CREATE","").replaceFirst("TABLE","").trim.split(" ").head.replace(";","")
    case "RENAME" => return  line.replaceFirst("RENAME","").replaceFirst("TABLE","").trim.replace(";","")
    case "DROP" => return  line.replaceFirst("DROP","").replaceFirst("TABLE","").trim.split(" ").head.replace(";","")
    case "EXISTS" => return  line.replaceFirst("DROP","").replaceFirst("TABLE","").replaceFirst("IF","").replaceFirst("EXISTS","").trim.split(" ").head.replace(";","")
    case _ => return line
  }



}
