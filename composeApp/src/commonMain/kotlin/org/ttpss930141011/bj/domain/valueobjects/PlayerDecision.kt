package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.Action

/**
 * Represents a player's decision in blackjack with correctness evaluation
 * 
 * Simple value object that pairs a player's chosen action with an assessment
 * of whether it was the optimal decision according to basic strategy.
 * 
 * @param action The action chosen by the player (HIT, STAND, DOUBLE, SPLIT, SURRENDER)
 * @param isCorrect Whether the chosen action matches optimal basic strategy
 */
data class PlayerDecision(
    val action: Action,
    val isCorrect: Boolean
)