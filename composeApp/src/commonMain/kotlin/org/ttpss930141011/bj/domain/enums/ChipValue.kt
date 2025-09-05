package org.ttpss930141011.bj.domain.enums

/**
 * Domain enum for chip denominations
 */
enum class ChipValue(val value: Int) {
    FIVE(5),
    TEN(10), 
    TWENTY_FIVE(25),
    FIFTY(50),
    ONE_HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500);
    
    companion object {
        /**
         * Standard casino chip denominations
         */
        fun standardCasinoChips(): List<ChipValue> = listOf(
            FIVE, TEN, TWENTY_FIVE, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED
        )
        
        fun fromValue(value: Int): ChipValue? = entries.find { it.value == value }
    }
}