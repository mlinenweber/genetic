package blackjack

import scala.util.Random
import scala.collection.mutable.MutableList
import org.junit.Test
import org.junit.Assert
import org.hamcrest.core.IsSame

object Chart {
  def fromLine(line:String) = {
    val chart = new Chart
    val rows = line.split(";")
    var h = new Array[Array[ACTION.Value]](16)
    var s = new Array[Array[ACTION.Value]](9)
    var s2 = new Array[Array[ACTION.Value]](10)
    for (r <- 0 to 15) {
      val rr = rows(r)
      val rrr = rr.split(",").map(ACTION.fromString2)
      h(r) = rrr
    }
    //chart.hard = h.reverse.toIndexedSeq
    chart.hard = h.reverse
    
    for (r <- 16 to 24) {
      val rr = rows(r)
      val rrr = rr.split(",").map(ACTION.fromString2)
      s(r-16) = rrr
    }
    chart.soft = s.reverse

    for (r <- 25 to 34) {
      val rr = rows(r)
      val rrr = rr.split(",").map(ACTION.fromString2)
      s2(r-25) = rrr
    }
    chart.same = s2.reverse

    chart
  }
 
}
case class Chart {

  randomize
  
  def mutate():Chart = {
    val line = this.toLine(",")
    
    val chart = new Chart
    
    val line2 = chart.toLine(",")
    
    val x = line.split(",")
    val y = line2.split(",")
    val r = (new Random).nextInt(x.length)
    var line4 = x.take(r).mkString(",") + "," + y.takeRight(y.length- r).mkString(",")

    val arr = line4.split(",")
    var line3 = ""
    for (ss <- 0 to arr.length - 1) {
      line3 += arr(ss)
      line3 += (if ((ss+1) % 10 == 0) ";" else ",")
    }
    
    val ccc = Chart.fromLine(line3)
    ccc
  }

  def mutate2(num:Integer):Chart = {
    for (i <- 0 until num) {
      (new Random).nextInt(3) match {
        case 0 => this.mutateSection(this.hard, ACTION.random(2))
        case 1 => this.mutateSection(this.soft, ACTION.random(2))
        case 2 => this.mutateSection(this.same, ACTION.random(3))
      }
    }
    this
  }

  def mutateSection(section:Array[Array[blackjack.ACTION.Value]], action:ACTION.Value):Chart = {
    val hardRow = (new Random).nextInt(section.length)
    val hardCol = (new Random).nextInt(section(0).length)
    section(hardRow)(hardCol) = action

    this
  }
  
  def getAction(handType:HAND_TYPE.Value, hand:Hand, card:Card):ACTION.Value = {
    val c = card.value-2
    handType match {
      case HAND_TYPE.SAME => val r = hand.cards(0).value-2; val row = same(r); row(c)
      case HAND_TYPE.SOFT => {
        val r = hand.nonAce.value-2;
        val row = soft(r);
        row(c)
      }
      case HAND_TYPE.HARD => {
        val r = hand.total-5;
        val row = hard(r);
        row(c)
      }
    }
  }
  
  def play(hand:Hand, card:Card) = {
    if (hand.canSplit) {
      getAction(HAND_TYPE.SAME, hand, card)
    }
    else if (hand.isSoft) {
      getAction(HAND_TYPE.SOFT, hand, card)
    }
    else {
      var action = getAction(HAND_TYPE.HARD, hand, card)
      if (hand.cards.size > 2 && action == ACTION.DOUBLE) {
        action = ACTION.HIT
      }

      action
    }
  }

  var hard:Array[Array[ACTION.Value]] = _
  var soft:Array[Array[ACTION.Value]] = _
  var same:Array[Array[ACTION.Value]] = _

  def randomize = {
    hard = ((1 to 16) map { i => ((1 to 10) map { j => ACTION.random(ACTION.DOUBLE.id) }).toArray }).toArray
    soft = ((1 to 9) map { i => ((1 to 10) map { j => ACTION.random(ACTION.DOUBLE.id) }).toArray }).toArray
    same = ((1 to 10) map { i => ((1 to 10) map { j => ACTION.random(ACTION.SPLIT.id) }).toArray }).toArray
  }

  def toLine(sep:String=";") = {
    var str = ""
    hard.reverse.foreach(str += _.map(ACTION.ts).mkString(",") + sep)
    soft.reverse.foreach(str += _.map(ACTION.ts).mkString(",") + sep)
    same.reverse.foreach(str += _.map(ACTION.ts).mkString(",") + sep)
    str
  }
  
  override def toString() = {
    var s = "      2,  3,  4,  5,  6,  7,  8,  9,  T,  A\n"
    (0 to hard.length-1) foreach { i =>
      val index = 20 - i
      val row = hard.reverse(i)
      if (index < 10) { s += "  " + index } else s += " " + index
      s += "  " + row.map(ACTION.ts).mkString(", ") + "\n"
    }

    if (soft != null)
      (0 to soft.length-1) foreach { i =>
      val index = soft.length-1-i
      if (index == 8) s+= "A,T" else s += "A," + (index+2)
      val row = soft(index)
      s += "  " + row.map(ACTION.ts).mkString(", ") + "\n"
    }

    if (same != null)
      (0 to same.length-1) foreach { i =>
        val index = same.length - i

        val row = same(index-1)
        val index2= index + 1
        if (index2==11) { s += "A,A" } else if (index2==10) { s += "T,T" } else s += index2 + "," + index2
        s += "  " + row.map(ACTION.ts).mkString(", ") + "\n"
      }

    s
  }
}

object Blackjack {
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
  }
}

class Blackjack {
  val dealer = Hand
  val player = Hand
}

case class Hand(deck:Deck, card:Card) {
  var cards = MutableList(card)
  def isSoft = cards.size == 2 &&numAces > 0 && (total2 != total - numAces)
  def isSame = cards.size == 2 && cards(0).value == cards(1).value
  def numAces = cards.filter(c => c.isAce).size
  def nonAce = if (cards(0).isAce) cards(1) else cards(0)
  def draw = { cards += deck.next }
  def canSplit = cards.length == 2 && isSame
  def total2:Int = {
    var tot = 0
    cards.filter(c => !c.isAce).foreach(c => tot = tot + c.value)
    tot
  }
  def total:Int = {
    var tot = 0
    cards.foreach(c => tot = tot + c.value)
    if (tot > 21) {
      for (a <- 1 to numAces) {
        tot = tot - 10
        if (tot <= 21) return tot;
      }
    }
    tot
  }
  override def toString() = {
    cards.toString+"::"+total
  }
}

class Person(chart:Chart, deck:Deck) {
  val players = new MutableList[Player]
  var p = new Player(chart, deck, deck.next)

  players += p
  var active = p
  var index = 0
  active.draw

  def getNext() = {
    index = index + 1
    if (players.length > index) players(index) else null
  }
  def isBusted = {
    players.forall(p => p.total > 21)
  }

  def numWins(dealer:Dealer) = {
    players.foldLeft(0)(_ + _.numWins(dealer))
  }
  
  def play(dealer:Dealer) {
    val card = dealer.cards(0)
    play(card)
  }

  def play(card:Card) {
    if (active.total < 21) {
      var action = active.play(card)
      while (active != null && active.total < 21 && action != ACTION.STAND) {
        if (action == ACTION.HIT) {
          active.draw
        }
        if (action == ACTION.DOUBLE) {
          active.doubled = true
          active.draw
          active = getNext
        }
        else if (action == ACTION.SPLIT) {
          val player = new Player(chart, deck, active.cards(1))
          players += player
          active.cards = active.cards.take(1)
          active.draw
          player.draw
        }

        if (active != null) {
          if (active.total < 21) {
            action = active.play(card)
          }
        }
      }
    }
  }
    
  override def toString = {
    players.mkString("\n")
  }
}

class Dealer(deck:Deck) extends Hand(deck, deck.next) {
  def play() {
    while (total < 16) {
      draw
    }
  }
}

class Player(chart:Chart, deck:Deck, card:Card) extends Hand(deck, card) {
  var doubled = false;
  
  def play(card:Card) = {
    chart.play(this, card)
  }

  // -1 for lose
  //  0 for push
  //  1 for win
  def numWins(dealer:Dealer) = {
    if (total <= 21 && (total > dealer.total) || dealer.total > 21) if (doubled) 2 else 1
    else if (total != dealer.total) if (doubled) -2 else -1
    else 0
  }
}

class Deck {
  var i = 0
  var cards = Random.shuffle(SUIT.values.flatMap{s => DENOMINATION.values.map{d => Card(s, d)}}.toList)
  def next = { val c = cards(i); i = i + 1 ; if (i==cards.size) { cards = Random.shuffle(cards); i = 0 }; c }
}

case class Card(suit:SUIT.Value, denom:DENOMINATION.Value) {
  def isAce = (denom == DENOMINATION.ACE)
  def value = denom match {
    case DENOMINATION.JACK => 10
    case DENOMINATION.QUEEN => 10
    case DENOMINATION.KING => 10
    case DENOMINATION.ACE => 11
    case _ => denom.id
  }
}

object HAND_TYPE extends Enumeration {
  type HAND_TYPE = Value
  val SAME = Value(0)
  val SOFT = Value(1)
  val HARD = Value(2)
}

object ACTION extends Enumeration {
  type ACTION = Value
  val STAND = Value(0)
  val HIT = Value(1)
  val DOUBLE = Value(2)
  val SPLIT = Value(3)
  
  def random(max:Int):ACTION = {
    ACTION(new Random().nextInt(max))
  }

  def ts(action:ACTION.Value) = { 
    action match {
      case STAND => "S"
      case HIT => "H"
      case DOUBLE => "D"
      case SPLIT => "Sp"
    }    
  }
  
  def fromString2(str:String) = {
    str match {
      case "S" => STAND
      case "H" => HIT
      case "D" => DOUBLE
      case "Sp" => SPLIT
    }
  }
}

object SUIT extends Enumeration {
  type SUIT = Value
  val CLUBS = Value(0)
  val DIAMONDS = Value(1)
  val HEARTS = Value(2)
  val SPADES = Value(3)
}

object DENOMINATION extends Enumeration {
  type DENOMINATION = Value
  val TWO = Value(2)
  val THREE = Value(3)
  val FOUR = Value(4)
  val FIVE = Value(5)
  val SIX = Value(6)
  val SEVEN = Value(7)
  val EIGHT = Value(8)
  val NINE = Value(9)
  val TEN = Value(10)
  val JACK = Value(11)
  val QUEEN = Value(12)
  val KING = Value(13)
  val ACE = Value(14)
}