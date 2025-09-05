package org.ttpss930141011.bj.domain.enums

enum class HandStatus {
    ACTIVE,       // 可繼續行動
    STANDING,     // 已站牌，等待結果
    BUSTED,       // 爆牌
    SURRENDERED,  // 投降
    WIN,          // 勝利
    LOSS,         // 失敗  
    PUSH          // 平手
}