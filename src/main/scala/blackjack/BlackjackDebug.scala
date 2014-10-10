package blackjack

import blackjack.MockChart

object BlackjackDebug {
  
  def main(args:Array[String]) = {
    //val chart = new Chart
    val chart = new MockChart
    //val line = chart.toLine(";")
    //println(line)
    //println(Chart.fromLine(line))
    println(chart)
    //println("tot:"+runGames(chart.toLine(";"),100))
    println(chart.mutate2(1))
  }
  /*
  def runGames(line:String, count:Integer) = {
    val deck = new Deck
    val chart = Chart.fromLine(line)

    var sum = 0
    for (i <- 1 to count) {
      val person = new Person(chart, deck)
      val dealer = new Dealer(deck)
      person.play(dealer)
      if (!person.isBusted)
    	  dealer.play

      val wins = person.numWins(dealer)
      sum = sum + wins
    }
    
    sum
  }*/
  
  def summary(person:Person, dealer:Dealer) = {
    println("Person. " + person)
    println("Dealer. " + dealer)
      if (!person.isBusted) {
        if (person.players(0).total == dealer.total) {
          println("Push")
        }
        else if (person.players(0).total > dealer.total) {
          println("Win")
        }
        else if (dealer.total > 21) {
          println("Win. Dealer Busted.")
        }
        else {
          println("Lose")
        }
      }
      else {
        println("Busted. LOSE")
      }
  }

}