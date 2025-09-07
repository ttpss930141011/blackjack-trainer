package org.ttpss930141011.bj.domain.enums

/**
 * Casino chip denominations with their monetary values
 * 
 * @param value Monetary value of the chip
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
         * Returns all standard casino chip denominations in ascending order
         * 
         * @return List of all chip values from 5 to 500
         */
        fun standardCasinoChips(): List<ChipValue> = listOf(
            FIVE, TEN, TWENTY_FIVE, FIFTY, ONE_HUNDRED, TWO_HUNDRED, FIVE_HUNDRED
        )
        
        /**
         * Finds chip denomination by monetary value
         * 
         * @param value Monetary value to search for
         * @return Matching chip value or null if not found
         */
        fun fromValue(value: Int): ChipValue? = entries.find { it.value == value }
    }
}