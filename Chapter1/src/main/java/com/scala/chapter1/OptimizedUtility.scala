package com.scala.chapter1

import java.io._

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * User: sunilkumar.ashok
  * Date: 3/21/2017
  */
object OptimizedUtility {

  def main(args: Array[String]): Unit = {
    //CREATE, ALTER, DROP, RENAME, INSERT, UPDATE
    val actions = List("CREATE", "ALTER", "DROP", "RENAME", "INSERT", "UPDATE")

    actions.map(x => findActionMatchResults("C:\\NMI\\Axis-O\\code\\scripts\\db\\patches\\R9\\",x))
  }

  def findActionMatchResults(dirName: String, action: String): Unit= {
    if(action.equals("DROP")){
      var dropTableList = findMatchingLineInDirectory(dirName, action)

      val createTableList = findMatchingLineInDirectory(dirName,"CREATE")
      val sudoDropList = findMatchingLineInDirectory(dirName,"EXISTS")

      dropTableList = dropTableList.union(sudoDropList.diff(createTableList))

      writeToFile(createOutputFile("DROP"), dropTableList.toList.sorted)
    }
    else {
      findMatchingLineInDirectory(dirName,action)
    }
  }

  def findMatchingLineInDirectory(dirName: String, matchingWord: String): ListBuffer[String]= {
    val directory = new File(dirName);
    var list = new ListBuffer[String]()
    if(directory.exists() && directory.isDirectory) {

      val strings: List[String] = directory.listFiles()
                                      .flatMap(file => printMatchingLine(file, matchingWord))
                                      .toList

      writeToFile(createOutputFile(matchingWord), strings.sorted)
      list = strings.to[ListBuffer]

      println("Finished"+ list.size)
    }
    return list;

  }

  def createOutputFile(matchWord: String):File = {
    val file = new File(matchWord.concat("_matching_lines"));
    if(file.exists()){
      file.createNewFile();
    }
    println("Writing to output file @" +file.getAbsolutePath)
    return file;
  }


  def printMatchingLine (inputFile: File, matchingWord: String) :List[String]= {
    val bf = Source.fromFile(inputFile.getAbsolutePath)

    var formattedLines = new ListBuffer[String]()
    var formattedLine = new StringBuilder()

    bf.getLines().foreach(x=>{
      if(isValidLine(x)){
        val sb = formattedLine
        sb.append(x.trim.replaceAll("`","").replaceAll("\\s+"," "))
        if(sb.endsWith(";")){
          formattedLines += sb.toString()
          formattedLine = new StringBuilder()
        } else {
          sb.append(" ")
        }
      }
    })

    val strings: List[String] = formattedLines.toList.map(p =>(dataToWrite(p,matchingWord))).filter(_ != null)

    return strings
  }

  def isValidLine(data: String): Boolean ={
    if(data.isEmpty || data.startsWith("#") || data.startsWith("--")) {
      return false;
    }
    return true;
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

  def dataToWrite(line: String,  matchWord: String) : String ={
    if(isLineMatching(line.trim.toUpperCase,matchWord)){
        return getFormattedContent(line.trim, matchWord)
    }
    return null
  }


  def getFormattedContent(line: String,  matchWord: String) : String = matchWord.toUpperCase match {
    case "CREATE" => return  line.replaceFirst("CREATE","").replaceFirst("TABLE","").trim.split(" ").head.replace(";","")
    case "RENAME" => return  line.replaceFirst("RENAME","").replaceFirst("TABLE","").trim.replace(";","")
    case "DROP" => return  line.replaceFirst("DROP","").replaceFirst("TABLE","").trim.split(" ").head.replace(";","")
    case "EXISTS" => return  line.replaceFirst("DROP","").replaceFirst("TABLE","").replaceFirst("IF","").replaceFirst("EXISTS","").trim.split(" ").head.replace(";","")
    case _ => return line
  }


  def writeToFile(file: File, lists: List[String])={
    val fileWriter =new FileWriter(file)
    val bw =new BufferedWriter(fileWriter)

    lists.foreach(p => if(p != null){fileWriter.append(p).append("\n")})

    bw.flush();
    fileWriter.flush();
    bw.close();
    fileWriter.close();

  }

}
