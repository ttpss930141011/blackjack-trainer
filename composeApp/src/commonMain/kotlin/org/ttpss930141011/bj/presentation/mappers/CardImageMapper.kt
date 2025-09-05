package org.ttpss930141011.bj.presentation.mappers

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.DrawableResource
import org.ttpss930141011.bj.domain.valueobjects.Card
import org.ttpss930141011.bj.domain.valueobjects.Rank
import org.ttpss930141011.bj.domain.valueobjects.Suit
import androidx.compose.ui.graphics.painter.Painter
import blackjack_strategy_trainer.composeapp.generated.resources.Res
import blackjack_strategy_trainer.composeapp.generated.resources.*

/**
 * Domain-to-UI mapping for card images
 * Maps domain Card objects to Compose Painter resources
 */
object CardImageMapper {
    
    @Composable
    fun getCardPainter(card: Card): Painter {
        val resource = getCardResource(card)
        return painterResource(resource)
    }
    
    @Composable
    fun getCardBackPainter(): Painter {
        return painterResource(Res.drawable.card_back)
    }
    
    private fun getCardResource(card: Card): DrawableResource {
        return when (card.rank) {
            Rank.ACE -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.ace_hearts
                Suit.DIAMONDS -> Res.drawable.ace_diamonds
                Suit.CLUBS -> Res.drawable.ace_clubs
                Suit.SPADES -> Res.drawable.ace_spades
            }
            Rank.TWO -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.two_hearts
                Suit.DIAMONDS -> Res.drawable.two_diamonds
                Suit.CLUBS -> Res.drawable.two_clubs
                Suit.SPADES -> Res.drawable.two_spades
            }
            Rank.THREE -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.three_hearts
                Suit.DIAMONDS -> Res.drawable.three_diamonds
                Suit.CLUBS -> Res.drawable.three_clubs
                Suit.SPADES -> Res.drawable.three_spades
            }
            Rank.FOUR -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.four_hearts
                Suit.DIAMONDS -> Res.drawable.four_diamonds
                Suit.CLUBS -> Res.drawable.four_clubs
                Suit.SPADES -> Res.drawable.four_spades
            }
            Rank.FIVE -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.five_hearts
                Suit.DIAMONDS -> Res.drawable.five_diamonds
                Suit.CLUBS -> Res.drawable.five_clubs
                Suit.SPADES -> Res.drawable.five_spades
            }
            Rank.SIX -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.six_hearts
                Suit.DIAMONDS -> Res.drawable.six_diamonds
                Suit.CLUBS -> Res.drawable.six_clubs
                Suit.SPADES -> Res.drawable.six_spades
            }
            Rank.SEVEN -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.seven_hearts
                Suit.DIAMONDS -> Res.drawable.seven_diamonds
                Suit.CLUBS -> Res.drawable.seven_clubs
                Suit.SPADES -> Res.drawable.seven_spades
            }
            Rank.EIGHT -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.eight_hearts
                Suit.DIAMONDS -> Res.drawable.eight_diamonds
                Suit.CLUBS -> Res.drawable.eight_clubs
                Suit.SPADES -> Res.drawable.eight_spades
            }
            Rank.NINE -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.nine_hearts
                Suit.DIAMONDS -> Res.drawable.nine_diamonds
                Suit.CLUBS -> Res.drawable.nine_clubs
                Suit.SPADES -> Res.drawable.nine_spades
            }
            Rank.TEN -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.ten_hearts
                Suit.DIAMONDS -> Res.drawable.ten_diamonds
                Suit.CLUBS -> Res.drawable.ten_clubs
                Suit.SPADES -> Res.drawable.ten_spades
            }
            Rank.JACK -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.jack_hearts
                Suit.DIAMONDS -> Res.drawable.jack_diamonds
                Suit.CLUBS -> Res.drawable.jack_clubs
                Suit.SPADES -> Res.drawable.jack_spades
            }
            Rank.QUEEN -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.queen_hearts
                Suit.DIAMONDS -> Res.drawable.queen_diamonds
                Suit.CLUBS -> Res.drawable.queen_clubs
                Suit.SPADES -> Res.drawable.queen_spades
            }
            Rank.KING -> when (card.suit) {
                Suit.HEARTS -> Res.drawable.king_hearts
                Suit.DIAMONDS -> Res.drawable.king_diamonds
                Suit.CLUBS -> Res.drawable.king_clubs
                Suit.SPADES -> Res.drawable.king_spades
            }
        }
    }
}