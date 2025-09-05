package org.ttpss930141011.bj.presentation.mappers

// DealerTurnState - 精細的dealer回合狀態機
sealed class DealerTurnState {
    // 翻開dealer暗牌階段
    object Revealing : DealerTurnState()
    
    // dealer必須補牌 (手牌 < 17)
    object MustHit : DealerTurnState()
    
    // dealer必須停牌 (手牌 >= 17 或 soft 17 根據規則)
    object MustStand : DealerTurnState()
    
    // dealer回合完成
    object Completed : DealerTurnState()
}

// DealerCommand - 用戶控制的dealer操作命令
sealed class DealerCommand {
    // 翻開dealer暗牌
    object RevealCards : DealerCommand()
    
    // dealer補一張牌
    object DrawCard : DealerCommand()
    
    // dealer停牌並完成回合
    object Stand : DealerCommand()
    
    // 進入結算階段
    object ProceedToSettlement : DealerCommand()
}