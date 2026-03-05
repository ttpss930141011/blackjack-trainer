package org.ttpss930141011.bj.application

import org.ttpss930141011.bj.domain.enums.Action
import org.ttpss930141011.bj.domain.valueobjects.ActionResult
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.PlayerHand

/**
 * Creates ActionResult from before/after hand snapshots.
 */
internal object ActionResultFactory {

    fun create(
        action: Action,
        handBefore: PlayerHand,
        handAfter: PlayerHand?,
        splitHands: List<PlayerHand>? = null
    ): ActionResult {
        val gotNewCard = handAfter != null && handAfter.cards.size > handBefore.cards.size

        return when (action) {
            Action.HIT -> {
                val (card, cards) = extractNewCard(gotNewCard, handBefore, handAfter)
                ActionResult.Hit(card, cards)
            }
            Action.DOUBLE -> {
                val (card, cards) = extractNewCard(gotNewCard, handBefore, handAfter)
                ActionResult.Double(card, cards)
            }
            Action.STAND -> ActionResult.Stand(handBefore.cards)
            Action.SURRENDER -> ActionResult.Surrender(handBefore.cards)
            Action.SPLIT -> {
                if (splitHands != null && splitHands.size >= 2) {
                    ActionResult.Split(
                        originalPair = handBefore.cards,
                        hand1 = splitHands[0].cards,
                        hand2 = splitHands[1].cards
                    )
                } else {
                    ActionResult.Split(
                        originalPair = handBefore.cards,
                        hand1 = handBefore.cards,
                        hand2 = handBefore.cards
                    )
                }
            }
        }
    }

    private fun extractNewCard(
        gotNewCard: Boolean,
        handBefore: PlayerHand,
        handAfter: PlayerHand?
    ): Pair<Card, List<Card>> {
        val card = if (gotNewCard) handAfter!!.cards.last() else handBefore.cards.first()
        val cards = if (gotNewCard) handAfter!!.cards else handBefore.cards
        return card to cards
    }
}
