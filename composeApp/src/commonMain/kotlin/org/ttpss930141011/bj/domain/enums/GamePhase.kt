package org.ttpss930141011.bj.domain.enums

/**
 * Game phase enumeration representing the current state of a blackjack round
 */
enum class GamePhase {
    /** Waiting for player to place bets */
    WAITING_FOR_BETS,
    /** Cards being dealt to players and dealer */
    DEALING,
    /** Player making decisions (hit, stand, double, split) */
    PLAYER_TURN,
    /** Dealer playing according to house rules */
    DEALER_TURN,
    /** Final payouts and hand resolution */
    SETTLEMENT
}