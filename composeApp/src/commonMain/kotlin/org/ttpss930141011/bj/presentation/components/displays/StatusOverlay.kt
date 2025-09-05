package org.ttpss930141011.bj.presentation.components.displays

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.ttpss930141011.bj.domain.enums.HandStatus
import org.ttpss930141011.bj.presentation.mappers.DealerStatus
import org.ttpss930141011.bj.presentation.design.Tokens
import org.ttpss930141011.bj.presentation.design.GameStatusColors

/**
 * Status overlay component that shows visual status indicators
 * Appears over hand cards with dark overlay and emoji icons + text
 * Supports both player and dealer states following DDD principles
 * Uses matchParentSize() equivalent to exactly match the Card's bounds
 */
@Composable
fun StatusOverlay(
    // Player status parameters (existing)
    status: HandStatus? = null,
    isBusted: Boolean = false,
    showStatus: Boolean = false,
    
    // Dealer status parameters (new)
    dealerStatus: DealerStatus? = null,
    showDealerStatus: Boolean = false,
    
    modifier: Modifier = Modifier
) {
    if ((showStatus && (isBusted || status != null)) || (showDealerStatus && dealerStatus != null)) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(Tokens.Space.m))
                .background(GameStatusColors.statusOverlayColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Emoji icon (unified for player and dealer)
                Text(
                    text = when {
                        // Player statuses (existing)
                        isBusted -> "💥"
                        status == HandStatus.WIN -> "👑"
                        status == HandStatus.LOSS -> "💸"
                        status == HandStatus.PUSH -> "🤝"
                        status == HandStatus.SURRENDERED -> "🏳️"
                        
                        // Dealer statuses (new)
                        dealerStatus == DealerStatus.REVEALING -> "🔄"
                        dealerStatus == DealerStatus.BUSTED -> "💥"
                        dealerStatus == DealerStatus.STANDING -> "🛑"
                        dealerStatus == DealerStatus.HITTING -> "⚡"
                        else -> ""
                    },
                    fontSize = 32.sp,
                    color = Color.White
                )
                
                // Status text (unified for player and dealer)
                Text(
                    text = when {
                        // Player text (existing)
                        isBusted -> "BUST"
                        status == HandStatus.WIN -> "WIN"
                        status == HandStatus.LOSS -> "LOSE"
                        status == HandStatus.PUSH -> "PUSH"
                        status == HandStatus.SURRENDERED -> "SURRENDER"
                        
                        // Dealer text (new)
                        dealerStatus == DealerStatus.REVEALING -> "REVEALING"
                        dealerStatus == DealerStatus.BUSTED -> "DEALER BUST"
                        dealerStatus == DealerStatus.STANDING -> "DEALER STANDS"
                        dealerStatus == DealerStatus.HITTING -> "DEALER HITS"
                        else -> ""
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}