// Quick test to verify dealer hole card reveal logic
import org.ttpss930141011.bj.domain.*

fun main() {
    // Create game and deal cards
    val rules = GameRules()
    val game = Game.create(rules)
        .addPlayer(Player(id = "test", chips = 100))
        .placeBet(25)
        .dealRound()
    
    println("=== PLAYER_ACTIONS Phase ===")
    println("Game phase: ${game.phase}")
    println("Dealer hand size: ${game.dealer.hand?.cards?.size}")
    println("Dealer hand cards: ${game.dealer.hand?.cards}")
    println("Dealer hole card: ${game.dealer.holeCard}")
    println("Dealer up card: ${game.dealer.upCard}")
    
    // Player stands to move to dealer turn
    val gameAfterStand = game.playerAction(Action.STAND)
    println("\n=== After STAND (should be DEALER_TURN) ===")
    println("Game phase: ${gameAfterStand.phase}")
    println("Dealer hand size: ${gameAfterStand.dealer.hand?.cards?.size}")
    println("Dealer hand cards: ${gameAfterStand.dealer.hand?.cards}")
    println("Dealer hole card: ${gameAfterStand.dealer.holeCard}")
    
    // Dealer plays automated
    val gameAfterDealer = gameAfterStand.dealerPlayAutomated()
    println("\n=== After dealerPlayAutomated() ===")
    println("Game phase: ${gameAfterDealer.phase}")
    println("Dealer hand size: ${gameAfterDealer.dealer.hand?.cards?.size}")
    println("Dealer hand cards: ${gameAfterDealer.dealer.hand?.cards}")
    println("Dealer hole card: ${gameAfterDealer.dealer.holeCard}")
}