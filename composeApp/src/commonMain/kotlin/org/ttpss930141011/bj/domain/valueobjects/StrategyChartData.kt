package org.ttpss930141011.bj.domain.valueobjects

import org.ttpss930141011.bj.domain.enums.Action

/**
 * StrategyChartData - Domain value objects for blackjack basic strategy charts.
 * 
 * Pure domain representation of optimal strategy decisions based on player hand
 * and dealer up card combinations. Contains embedded strategy tables following
 * standard blackjack basic strategy principles.
 */

/**
 * Strategy chart types corresponding to different hand categories
 */
enum class ChartType {
    HARD,   // Hard totals (no ace or ace counted as 1)
    SOFT,   // Soft totals (ace counted as 11)
    SPLITS  // Pair splitting decisions
}

/**
 * Strategy chart data for a specific rule set
 */
data class StrategyChart(
    val chartType: ChartType,
    val dealerHitsOnSoft17: Boolean,
    val strategies: Map<Pair<Int, Int>, Action> // (playerValue, dealerValue) -> Action
) {
    fun getOptimalAction(playerValue: Int, dealerValue: Int): Action? {
        return strategies[Pair(playerValue, dealerValue)]
    }
}

/**
 * Complete strategy chart set for a specific rule variation
 */
data class StrategyChartSet(
    val dealerHitsOnSoft17: Boolean,
    val hardTotals: StrategyChart,
    val softTotals: StrategyChart,
    val splits: StrategyChart
) {
    fun getChart(chartType: ChartType): StrategyChart {
        return when (chartType) {
            ChartType.HARD -> hardTotals
            ChartType.SOFT -> softTotals
            ChartType.SPLITS -> splits
        }
    }
}

/**
 * Factory for creating strategy chart data based on basic strategy principles
 */
object StrategyChartFactory {
    
    /**
     * Creates strategy chart set for dealer stands on soft 17 rules
     */
    fun createDealerStandsOnSoft17(): StrategyChartSet {
        return StrategyChartSet(
            dealerHitsOnSoft17 = false,
            hardTotals = createHardTotalsStandOn17(),
            softTotals = createSoftTotalsStandOn17(),
            splits = createSplitsStandOn17()
        )
    }
    
    /**
     * Creates strategy chart set for dealer hits on soft 17 rules
     */
    fun createDealerHitsOnSoft17(): StrategyChartSet {
        return StrategyChartSet(
            dealerHitsOnSoft17 = true,
            hardTotals = createHardTotalsHitOn17(),
            softTotals = createSoftTotalsHitOn17(),
            splits = createSplitsHitOn17()
        )
    }
    
    private fun createHardTotalsStandOn17(): StrategyChart {
        val strategies = mutableMapOf<Pair<Int, Int>, Action>()
        
        // Hard totals 4-8: Always Hit
        for (player in 4..8) {
            for (dealer in 2..11) { // 11 represents Ace
                strategies[Pair(player, dealer)] = Action.HIT
            }
        }
        
        // Hard 9: Double on 3-6, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(9, dealer)] = when (dealer) {
                in 3..6 -> Action.DOUBLE
                else -> Action.HIT
            }
        }
        
        // Hard 10: Double on 2-9, Hit on 10/A
        for (dealer in 2..11) {
            strategies[Pair(10, dealer)] = when (dealer) {
                in 2..9 -> Action.DOUBLE
                else -> Action.HIT
            }
        }
        
        // Hard 11: Double on 2-10, Hit on A (dealer stands on soft 17)
        for (dealer in 2..11) {
            strategies[Pair(11, dealer)] = when (dealer) {
                in 2..10 -> Action.DOUBLE
                else -> Action.HIT
            }
        }
        
        // Hard 12: Stand on 4-6, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(12, dealer)] = when (dealer) {
                in 4..6 -> Action.STAND
                else -> Action.HIT
            }
        }
        
        // Hard 13-14: Stand on 2-6, Hit on 7-A
        for (player in 13..14) {
            for (dealer in 2..11) {
                strategies[Pair(player, dealer)] = when (dealer) {
                    in 2..6 -> Action.STAND
                    else -> Action.HIT
                }
            }
        }
        
        // Hard 15: Stand on 2-6, Surrender vs 10, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(15, dealer)] = when (dealer) {
                in 2..6 -> Action.STAND
                10 -> Action.SURRENDER
                else -> Action.HIT
            }
        }
        
        // Hard 16: Stand on 2-6, Surrender vs 9,10,A, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(16, dealer)] = when (dealer) {
                in 2..6 -> Action.STAND
                9, 10, 11 -> Action.SURRENDER // 9, 10, A
                else -> Action.HIT
            }
        }
        
        // Hard 17+: Always Stand
        for (player in 17..21) {
            for (dealer in 2..11) {
                strategies[Pair(player, dealer)] = Action.STAND
            }
        }
        
        return StrategyChart(ChartType.HARD, false, strategies)
    }
    
    private fun createSoftTotalsStandOn17(): StrategyChart {
        val strategies = mutableMapOf<Pair<Int, Int>, Action>()
        
        // Soft 13-14: Double on 5-6, Hit otherwise
        for (player in 13..14) {
            for (dealer in 2..11) {
                strategies[Pair(player, dealer)] = when (dealer) {
                    in 5..6 -> Action.DOUBLE
                    else -> Action.HIT
                }
            }
        }
        
        // Soft 15-16: Double on 4-6, Hit otherwise
        for (player in 15..16) {
            for (dealer in 2..11) {
                strategies[Pair(player, dealer)] = when (dealer) {
                    in 4..6 -> Action.DOUBLE
                    else -> Action.HIT
                }
            }
        }
        
        // Soft 17: Double on 3-6, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(17, dealer)] = when (dealer) {
                in 3..6 -> Action.DOUBLE
                else -> Action.HIT
            }
        }
        
        // Soft 18: Double on 3-6, Stand on 2/7/8/A, Hit on 9/10
        for (dealer in 2..11) {
            strategies[Pair(18, dealer)] = when (dealer) {
                in 3..6 -> Action.DOUBLE
                2, 7, 8, 11 -> Action.STAND // A should be STAND when dealer stands on soft 17
                else -> Action.HIT // 9, 10
            }
        }
        
        // Soft 19+: Always Stand
        for (player in 19..21) {
            for (dealer in 2..11) {
                strategies[Pair(player, dealer)] = Action.STAND
            }
        }
        
        return StrategyChart(ChartType.SOFT, false, strategies)
    }
    
    private fun createSplitsStandOn17(): StrategyChart {
        val strategies = mutableMapOf<Pair<Int, Int>, Action>()
        
        // 2,2: Split on 2-7, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(2, dealer)] = when (dealer) {
                in 2..7 -> Action.SPLIT
                else -> Action.HIT
            }
        }
        
        // 3,3: Split on 2-7, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(3, dealer)] = when (dealer) {
                in 2..7 -> Action.SPLIT
                else -> Action.HIT
            }
        }
        
        // 4,4: Hit (never split)
        for (dealer in 2..11) {
            strategies[Pair(4, dealer)] = Action.HIT
        }
        
        // 6,6: Split on 2-6, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(6, dealer)] = when (dealer) {
                in 2..6 -> Action.SPLIT
                else -> Action.HIT
            }
        }
        
        // 7,7: Split on 2-7, Hit otherwise
        for (dealer in 2..11) {
            strategies[Pair(7, dealer)] = when (dealer) {
                in 2..7 -> Action.SPLIT
                else -> Action.HIT
            }
        }
        
        // 8,8: Always Split
        for (dealer in 2..11) {
            strategies[Pair(8, dealer)] = Action.SPLIT
        }
        
        // 9,9: Split on 2-9 except 7, Stand on 7/10/A
        for (dealer in 2..11) {
            strategies[Pair(9, dealer)] = when (dealer) {
                in 2..6, 8, 9 -> Action.SPLIT
                else -> Action.STAND
            }
        }
        
        // 10,10: Never Split (Always Stand)
        for (dealer in 2..11) {
            strategies[Pair(10, dealer)] = Action.STAND
        }
        
        // A,A: Always Split
        for (dealer in 2..11) {
            strategies[Pair(11, dealer)] = Action.SPLIT // A,A represented as 11
        }
        
        return StrategyChart(ChartType.SPLITS, false, strategies)
    }
    
    private fun createHardTotalsHitOn17(): StrategyChart {
        val strategies = createHardTotalsStandOn17().strategies.toMutableMap()
        
        // Hard 11 vs A: Double when dealer hits soft 17
        strategies[Pair(11, 11)] = Action.DOUBLE
        
        return StrategyChart(ChartType.HARD, true, strategies)
    }
    
    private fun createSoftTotalsHitOn17(): StrategyChart {
        val strategies = createSoftTotalsStandOn17().strategies.toMutableMap()
        
        // Soft 17 vs 2: Double when dealer hits soft 17
        strategies[Pair(17, 2)] = Action.DOUBLE
        
        // Soft 18 vs A: Hit when dealer hits soft 17  
        strategies[Pair(18, 11)] = Action.HIT
        
        return StrategyChart(ChartType.SOFT, true, strategies)
    }
    
    private fun createSplitsHitOn17(): StrategyChart {
        // Splits strategy doesn't change based on dealer soft 17 rule
        return createSplitsStandOn17().copy(dealerHitsOnSoft17 = true)
    }
}