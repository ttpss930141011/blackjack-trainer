package org.ttpss930141011.bj.application

actual object TimeProvider {
    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}