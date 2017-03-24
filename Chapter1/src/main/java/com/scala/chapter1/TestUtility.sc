val createList= List(1,2,3,4,5)
val existList= List(3,4,5,6,7,8)

//val w = Iterator("a", "number", "of", "words")
val w = Iterator[String]("a", "number", "of", "words","afa")

w.size

w.filter(_ != null).toList

val s ="    fsdf   sdf  "

s.trim

s.trim.replaceAll("\\s+"," ")




13 < 12
510 < 650

// ld : LoanDetails( loanType : loanType == "ARM" ,
// firstAdjustmentMonths : firstAdjustmentMonths < 12 , loanAmount <= 650000B )


650>650

700<100
