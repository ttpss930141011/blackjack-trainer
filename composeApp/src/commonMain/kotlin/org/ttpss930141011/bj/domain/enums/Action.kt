package org.ttpss930141011.bj.domain.enums

/**
 * Player actions available in blackjack
 */
enum class Action {
    /** Request additional card */
    HIT,
    /** Keep current hand value */  
    STAND,
    /** Double bet and take exactly one more card */
    DOUBLE,
    /** Split identical cards into two hands */
    SPLIT,
    /** Forfeit hand for half the bet */
    SURRENDER
}