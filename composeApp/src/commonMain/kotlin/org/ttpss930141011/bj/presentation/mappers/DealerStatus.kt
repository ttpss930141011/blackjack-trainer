package org.ttpss930141011.bj.presentation.mappers

/**
 * DealerStatus - UI display states for dealer
 * Maps domain states to visual representation following DDD principles
 */
enum class DealerStatus {
    WAITING,     // Before cards dealt or during player turn
    REVEALING,   // Hole card reveal phase
    HITTING,     // Taking additional cards  
    STANDING,    // Final hand, no more cards
    BUSTED       // Hand value over 21
}