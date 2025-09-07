package org.ttpss930141011.bj.domain.enums

/**
 * Final outcome of a completed blackjack round
 */
enum class RoundResult {
    /** Player won with regular hand */
    PLAYER_WIN,
    /** Player won with natural blackjack */
    PLAYER_BLACKJACK,
    /** Dealer won the round */
    DEALER_WIN,
    /** Round ended in a tie */
    PUSH,
    /** Player forfeited half bet */
    SURRENDER
}