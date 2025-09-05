package org.ttpss930141011.bj.presentation.mappers

import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*
import org.ttpss930141011.bj.domain.services.*

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

/**
 * Extension function to convert domain state to UI status
 * Follows DDD pattern: domain model -> UI representation
 */
fun Dealer.getDisplayStatus(phase: GamePhase, turnState: DealerTurnState?): DealerStatus = 
    when {
        hand == null -> DealerStatus.WAITING
        phase == GamePhase.PLAYER_ACTIONS -> DealerStatus.WAITING
        turnState == DealerTurnState.Revealing -> DealerStatus.REVEALING
        hand!!.isBusted -> DealerStatus.BUSTED
        turnState == DealerTurnState.MustStand || turnState == DealerTurnState.Completed -> DealerStatus.STANDING
        turnState == DealerTurnState.MustHit -> DealerStatus.HITTING
        else -> DealerStatus.WAITING
    }