package blackjack
import org.junit.Test
import org.junit.Assert
import org.scalatest.junit.AssertionsForJUnit

class MockDeck(c:List[Card]) extends Deck {
  cards = c
}

class MockChart extends Chart {
  val H = ACTION.HIT
  val D = ACTION.DOUBLE
  val S = ACTION.STAND
  val Sp = ACTION.SPLIT

                  // 2  3  4  5  6  7  8  9 10  A
  hard = Array(Array(S, S, S, S, S, S, S, S, S, S), // 20
               Array(S, S, S, S, S, S, S, S, S, S), // 19
               Array(S, S, S, S, S, S, S, S, S, S), // 18
               Array(S, S, S, S, S, S, S, S, S, S), // 17
               Array(S, S, S, S, S, H, H, H, H, H), // 16
               Array(S, S, S, S, S, H, H, H, H, H), // 15
               Array(S, S, S, S, S, H, H, H, H, H), // 14
               Array(S, S, S, S, S, H, H, H, H, H), // 13
               Array(H, H, S, S, S, H, H, H, H, H), // 12
               Array(D, D, D, D, D, D, D, D, D, H), // 11
               Array(D, D, D, D, D, D, D, D, H, H), // 10
               Array(H, D, D, D, D, H, H, H, H, H), // 9
               Array(H, H, H, H, H, H, H, H, H, H), // 8
               Array(H, H, H, H, H, H, H, H, H, H), // 7
               Array(H, H, H, H, H, H, H, H, H, H), // 6
               Array(H, H, H, H, H, H, H, H, H, H)) // 5
               .reverse

                  // 2  3  4  5  6  7  8  9 10  A
  soft = Array(Array(S, S, S, S, S, S, S, S, S, S), // A,T
               Array(S, S, S, S, S, S, S, S, S, S), // A,9
               Array(S, S, S, S, S, S, S, S, S, S), // A,8
               Array(S, D, D, D, D, S, S, H, H, H), // A,7
               Array(H, D, D, D, D, H, H, H, H, H), // A,6
               Array(H, H, D, D, D, H, H, H, H, H), // A,5
               Array(H, H, D, D, D, H, H, H, H, H), // A,4
               Array(H, H, H, D, D, H, H, H, H, H), // A,3
               Array(H, H, H, D, D, H, H, H, H, H)) // A,2
               .reverse

                  // 2   3   4   5   6   7   8   9   10  A
  same = Array(Array(Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp), // A,A
               Array(S,  S,  S,  S,  S,  S,  S,  S,  S,  S),  // T,T
               Array(Sp, Sp, Sp, Sp, Sp, H,  Sp, Sp, H,  H),  // 9,9
               Array(Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp, Sp), // 8,8
               Array(Sp, Sp, Sp, Sp, Sp, Sp, H,  H,  H,  H),  // 7,7
               Array(Sp, Sp, Sp, Sp, Sp, H,  H,  H,  H,  H),  // 6,6
               Array(D,  D,  D,  D,  D,  D,  D,  D,  H,  H),  // 5,5
               Array(H,  H,  H, Sp, Sp,  H,  H,  H,  H,  H),  // 4,4
               Array(Sp, Sp, Sp, Sp, Sp, Sp, H,  H,  H,  H),  // 3,3
               Array(Sp, Sp, Sp, Sp, Sp, Sp, H,  H,  H,  H))  // 2,2
               .reverse
}

class BlackjackTest extends AssertionsForJUnit {

  val TWO = Card(SUIT.SPADES, DENOMINATION.TWO)
  val THREE = Card(SUIT.SPADES, DENOMINATION.THREE)
  val FOUR = Card(SUIT.SPADES, DENOMINATION.FOUR)
  val FIVE = Card(SUIT.SPADES, DENOMINATION.FIVE)
  val SIX = Card(SUIT.SPADES, DENOMINATION.SIX)
  val SEVEN = Card(SUIT.SPADES, DENOMINATION.SEVEN)
  val EIGHT = Card(SUIT.SPADES, DENOMINATION.EIGHT)
  val NINE = Card(SUIT.SPADES, DENOMINATION.NINE)
  val TEN = Card(SUIT.SPADES, DENOMINATION.TEN)
  val ACE = Card(SUIT.SPADES, DENOMINATION.ACE)
  
  @Test def test_4s() {
    val deck = new MockDeck(List(FOUR, FOUR))
    var player = new Player(new MockChart, deck, deck.next)
    player.draw
    Assert.assertEquals(ACTION.HIT, player.play(FOUR))
    Assert.assertEquals(ACTION.SPLIT, player.play(SIX))
    Assert.assertEquals(ACTION.HIT, player.play(SEVEN))
  }

    @Test def test_A4() {
    val deck = new MockDeck(List(ACE, FOUR))
    var player = new Player(new MockChart, deck, deck.next)
    player.draw
    Assert.assertEquals(ACTION.HIT, player.play(THREE))
    Assert.assertEquals(ACTION.DOUBLE, player.play(FOUR))
    Assert.assertEquals(ACTION.DOUBLE, player.play(SIX))
    Assert.assertEquals(ACTION.HIT, player.play(SEVEN))
  }

  @Test def test_9() {
    val deck = new MockDeck(List(FIVE, FOUR))
    var player = new Player(new MockChart, deck, deck.next)
    player.draw
    Assert.assertEquals(ACTION.HIT, player.play(TWO))
    Assert.assertEquals(ACTION.DOUBLE, player.play(THREE))
    Assert.assertEquals(ACTION.DOUBLE, player.play(SIX))
    Assert.assertEquals(ACTION.HIT, player.play(SEVEN))
  }

  @Test def test_play1() {
    val deck = new MockDeck(List(FIVE, TEN, FOUR, TEN, TEN, FIVE, EIGHT))
    val person = new Person(new MockChart, deck)
    person.play(SEVEN)
    Assert.assertEquals(3, person.players(0).cards.length)
    Assert.assertEquals(1, person.players.length)
  }
  @Test def test_play2() {
    val deck = new MockDeck(List(FIVE, TWO, FOUR, TEN, TEN, FIVE, EIGHT))
    val person = new Person(new MockChart, deck)
    person.play(SEVEN)
    Assert.assertEquals(4, person.players(0).cards.length)
    Assert.assertEquals(1, person.players.length)
  }

  @Test def test_soft() {
    val deck = new MockDeck(List(ACE, TWO, TEN, TEN, FIVE, EIGHT))
    val person = new Person(new MockChart, deck)
    person.play(SEVEN)
    Assert.assertTrue(person.isBusted)
    Assert.assertEquals(4, person.players(0).cards.length)
    Assert.assertEquals(1, person.players.length)
  }

  @Test def test_split_both_lose() {
    val deck = new MockDeck(List(EIGHT, EIGHT, SEVEN, TEN, TEN, FIVE, EIGHT))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 8, 10
    Assert.assertEquals(18, person.players(0).total) // 18
    Assert.assertEquals(2, person.players(1).cards.length) // 8, 10
    Assert.assertEquals(18, person.players(1).total) // 18

    dealer.play
    Assert.assertEquals(3, dealer.cards.length)
    Assert.assertEquals(20, dealer.total) // 7, 5, 8
    
    Assert.assertEquals(-2, person.numWins(dealer))
  }
  
  // Test A 2,2 == A,4
  
  @Test def test_push() {
    val deck = new MockDeck(List(TEN, TEN, TEN, TEN))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 10, 10
    Assert.assertEquals(20, person.players(0).total) // 20

    dealer.play
    Assert.assertEquals(2, dealer.cards.length)
    Assert.assertEquals(20, dealer.total) // 10
    
    Assert.assertEquals(0, person.numWins(dealer))
  }

  @Test def test_lose() {
    val deck = new MockDeck(List(TEN, NINE, TEN, TEN))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 10, 9
    Assert.assertEquals(19, person.players(0).total) // 19

    dealer.play
    Assert.assertEquals(2, dealer.cards.length)
    Assert.assertEquals(20, dealer.total) // 20

    Assert.assertEquals(-1, person.numWins(dealer))
  }

    @Test def test_win() {
    val deck = new MockDeck(List(TEN, TEN, TEN, NINE))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 10, 10
    Assert.assertEquals(20, person.players(0).total) // 20

    dealer.play
    Assert.assertEquals(2, dealer.cards.length)
    Assert.assertEquals(19, dealer.total) // 19

    Assert.assertEquals(1, person.numWins(dealer))
  }

    @Test def test_split_both_push() {
    val deck = new MockDeck(List(EIGHT, EIGHT, SEVEN, TEN, TEN, FIVE, SIX))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 8, 10
    Assert.assertEquals(18, person.players(0).total) // 18
    Assert.assertEquals(2, person.players(1).cards.length) // 8, 10
    Assert.assertEquals(18, person.players(1).total) // 18

    dealer.play
    Assert.assertEquals(3, dealer.cards.length) // 7, 5, 6
    Assert.assertEquals(18, dealer.total) // 18
    
    Assert.assertEquals(0, person.numWins(dealer))
  }

  @Test def test_split_one_push_one_lose() {
    val deck = new MockDeck(List(EIGHT, EIGHT, SEVEN, TEN, NINE, FIVE, SIX))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 8, 10
    Assert.assertEquals(18, person.players(0).total) // 18
    Assert.assertEquals(2, person.players(1).cards.length) // 8, 9
    Assert.assertEquals(17, person.players(1).total) // 17

    dealer.play
    Assert.assertEquals(3, dealer.cards.length) // 7, 5, 6
    Assert.assertEquals(18, dealer.total) // 18
    
    Assert.assertEquals(-1, person.numWins(dealer))
  }

  @Test def test_split_one_win_one_lose() {
    val deck = new MockDeck(List(EIGHT, EIGHT, SEVEN, ACE, NINE, FIVE, SIX))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    Assert.assertEquals(2, person.players(0).cards.length) // 8, 11
    Assert.assertEquals(19, person.players(0).total) // 19
    Assert.assertEquals(2, person.players(1).cards.length) // 8, 9
    Assert.assertEquals(17, person.players(1).total) // 17

    dealer.play
    Assert.assertEquals(3, dealer.cards.length) // 7, 5, 6
    Assert.assertEquals(18, dealer.total) // 18
    
    Assert.assertEquals(0, person.numWins(dealer))
  }
  
  @Test def test_dealer_busted_player_not_busted() {
    val deck = new MockDeck(List(FOUR, NINE, FOUR, TEN, TEN))
    val person = new Person(new MockChart, deck)
    val dealer = new Dealer(deck)
    person.play(dealer)
    dealer.play

    Assert.assertEquals(2, person.players(0).cards.length) // 4, 9 = 13
    Assert.assertEquals(13, person.players(0).total) 

    Assert.assertEquals(3, dealer.cards.length) // 4, 10, 10 = 24
    Assert.assertEquals(24, dealer.total)

    Assert.assertEquals(1, person.numWins(dealer))
  }

    
  @Test def test_double() {
    val deck = new MockDeck(List(FOUR, SIX, FOUR, TEN, TEN))
    val chart = new MockChart
    val person = new Person(chart, deck)
    val dealer = new Dealer(deck)

    var action = chart.getAction(HAND_TYPE.HARD, person.active, dealer.cards(0))
    
    person.play(dealer)
    dealer.play

/*
    Assert.assertEquals(2, person.players(0).cards.length) // 4, 9 = 13
    Assert.assertEquals(13, person.players(0).total) 

    Assert.assertEquals(3, dealer.cards.length) // 4, 10, 10 = 24
    Assert.assertEquals(24, dealer.total)
*/
    Assert.assertEquals(2, person.numWins(dealer))
  }

}
