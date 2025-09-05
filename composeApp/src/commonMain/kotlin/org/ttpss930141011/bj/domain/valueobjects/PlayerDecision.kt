package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.Action

// PlayerDecision value object for tracking decision quality
data class PlayerDecision(
    val action: Action,
    val isCorrect: Boolean
)