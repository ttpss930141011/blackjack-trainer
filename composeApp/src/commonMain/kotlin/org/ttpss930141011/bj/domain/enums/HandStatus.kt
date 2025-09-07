package org.ttpss930141011.bj.domain.enums

/**
 * Status of a player hand during and after play
 */
enum class HandStatus {
    /** Hand can still take actions */
    ACTIVE,
    /** Player chose to stand, awaiting dealer */
    STANDING,
    /** Hand exceeded 21 points */
    BUSTED,
    /** Player surrendered */
    SURRENDERED,
    /** Hand won against dealer */
    WIN,
    /** Hand lost to dealer */
    LOSS,
    /** Hand tied with dealer */
    PUSH
}