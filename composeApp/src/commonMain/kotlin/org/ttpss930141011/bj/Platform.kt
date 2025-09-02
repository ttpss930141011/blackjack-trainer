package org.ttpss930141011.bj

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform