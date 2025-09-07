package org.ttpss930141011.bj.application

import androidx.compose.runtime.*
import org.ttpss930141011.bj.domain.entities.*
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.domain.enums.*

/**
 * FeedbackManager - Focuses ONLY on decision feedback and evaluation
 * 
 * Linus: "Handle feedback, track decisions. No game state shit, no analytics shit."
 */
internal class FeedbackManager(
    private val decisionEvaluator: DecisionEvaluator
) {
    private var _feedback by mutableStateOf<DecisionFeedback?>(null)
    val feedback: DecisionFeedback? get() = _feedback
    
    private var _roundDecisions by mutableStateOf<List<PlayerDecision>>(emptyList())
    val roundDecisions: List<PlayerDecision> get() = _roundDecisions
    
    fun evaluatePlayerAction(
        handBeforeAction: PlayerHand,
        dealerUpCard: Card,
        playerAction: Action,
        rules: GameRules
    ): DecisionFeedback {
        val feedback = decisionEvaluator.evaluateDecision(
            handBeforeAction = handBeforeAction,
            dealerUpCard = dealerUpCard,
            playerAction = playerAction,
            rules = rules
        )
        _feedback = feedback
        
        // Track decision for round
        val playerDecision = PlayerDecision(playerAction, feedback.isCorrect)
        _roundDecisions = _roundDecisions + playerDecision
        
        return feedback
    }
    
    fun clearFeedback() {
        _feedback = null
    }
    
    fun reset() {
        _feedback = null
        _roundDecisions = emptyList()
    }
    
    fun startNewRound() {
        _feedback = null
        _roundDecisions = emptyList()
    }
}