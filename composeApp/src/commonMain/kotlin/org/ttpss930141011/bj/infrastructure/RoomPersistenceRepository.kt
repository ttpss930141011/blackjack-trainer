package org.ttpss930141011.bj.infrastructure

import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.ttpss930141011.bj.domain.services.PersistenceRepository
import org.ttpss930141011.bj.domain.valueobjects.*
import org.ttpss930141011.bj.infrastructure.database.BlackjackDatabase
import org.ttpss930141011.bj.infrastructure.database.entities.RoundHistoryEntity
import org.ttpss930141011.bj.infrastructure.database.entities.DecisionRecordEntity
import kotlin.reflect.KClass
import org.ttpss930141011.bj.domain.enums.RoundResult

/**
 * Room-based implementation of PersistenceRepository
 * 
 * 按照 Linus 的 "good taste" 原則：
 * - 簡單的 3-method interface 橋接到 Room
 * - 使用 Room 的 TypeConverter 處理複雜對象
 * - 沒有特殊情況，每種類型都有清晰的處理邏輯
 */
class RoomPersistenceRepository(
    private val database: BlackjackDatabase
) : PersistenceRepository {
    
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    
    
    override suspend fun save(data: Any) {
        when (data) {
            is RoundHistory -> {
                val entity = RoundHistoryEntity(
                    roundId = data.roundId,
                    sessionId = data.sessionId,
                    timestamp = data.timestamp,
                    gameRulesJson = json.encodeToString(data.gameRules),
                    initialBet = data.initialBet,
                    decisionsJson = json.encodeToString(data.decisions),
                    roundResult = data.roundResult,
                    netChipChange = data.netChipChange,
                    roundDurationMs = data.roundDurationMs
                )
                database.roundHistoryDao().insertRound(entity)
            }
            
            is DecisionRecord -> {
                val entity = DecisionRecordEntity(
                    timestamp = data.timestamp,
                    handCardsJson = json.encodeToString(data.handCards),
                    dealerUpCardJson = json.encodeToString(data.dealerUpCard),
                    playerAction = data.playerAction,
                    isCorrect = data.isCorrect,
                    baseScenarioKey = data.baseScenarioKey,
                    ruleHash = data.ruleHash,
                    handValue = data.handCards.sumOf { it.blackjackValue },
                    isHandSoft = data.handCards.any { it.rank == Rank.ACE } && data.handCards.sumOf { it.blackjackValue } + 10 <= 21,
                    canDouble = true, // Simplified - in real implementation would check game state
                    canSplit = data.handCards.size == 2 && data.handCards[0].rank == data.handCards[1].rank
                )
                database.decisionRecordDao().insertDecision(entity)
            }
            
            is UserPreferences -> {
                // TODO: 實現 UserPreferences 保存
                // 可以使用 SharedPreferences 或者創建專門的 Entity
            }
            
            else -> {
                // 按照 3-method interface 的設計，直接忽略不支持的類型
                // 這避免了複雜的錯誤處理邏輯
            }
        }
    }
    
    override suspend fun <T : Any> load(key: String, type: KClass<T>): T? {
        return when (type) {
            RoundHistory::class -> {
                // 按照 key 載入單個 RoundHistory
                // key 格式: "round_${roundId}"
                val roundId = key.removePrefix("round_")
                val entities = database.roundHistoryDao().getRecentRounds(InfrastructureConstants.CACHE_CLEANUP_THRESHOLD_SIZE) // 取大範圍然後過濾
                val entity = entities.find { it.roundId == roundId }
                entity?.let { convertEntityToDomain(it) } as? T
            }
            
            UserPreferences::class -> {
                // TODO: 實現 UserPreferences 載入
                null
            }
            
            else -> null
        }
    }
    
    override suspend fun <T : Any> query(type: KClass<T>, criteria: Map<String, Any>): List<T> {
        return when (type) {
            RoundHistory::class -> {
                val entities = queryRoundHistoryEntities(criteria)
                entities.map { convertEntityToDomain(it) } as List<T>
            }
            DecisionRecord::class -> {
                val entities = queryDecisionRecordEntities(criteria)
                entities.map { convertDecisionEntityToDomain(it) } as List<T>
            }
            else -> emptyList()
        }
    }
    
    /**
     * Centralized query logic for RoundHistory entities.
     * Eliminates code duplication and provides clear criteria handling.
     */
    private suspend fun queryRoundHistoryEntities(criteria: Map<String, Any>): List<RoundHistoryEntity> {
        return if (criteria.isEmpty()) {
            database.roundHistoryDao().getRecentRounds(InfrastructureConstants.DEFAULT_RECENT_ROUNDS_LIMIT)
        } else {
            when {
                criteria.containsKey("sessionId") -> {
                    val sessionId = criteria["sessionId"] as String
                    database.roundHistoryDao().getRoundsBySession(sessionId)
                }
                criteria.containsKey("roundResult") -> {
                    val result = criteria["roundResult"] as RoundResult
                    val limit = criteria["limit"] as? Int ?: InfrastructureConstants.DEFAULT_RECENT_ROUNDS_LIMIT
                    database.roundHistoryDao().getRoundsByResult(result, limit)
                }
                else -> {
                    database.roundHistoryDao().getRecentRounds(InfrastructureConstants.DEFAULT_RECENT_ROUNDS_LIMIT)
                }
            }
        }
    }
    
    /**
     * Centralized query logic for DecisionRecord entities.
     * Eliminates code duplication and provides clear criteria handling.
     */
    private suspend fun queryDecisionRecordEntities(criteria: Map<String, Any>): List<DecisionRecordEntity> {
        return if (criteria.isEmpty()) {
            database.decisionRecordDao().getRecentDecisions(InfrastructureConstants.DEFAULT_RECENT_DECISIONS_LIMIT)
        } else {
            when {
                criteria.containsKey("baseScenarioKey") -> {
                    val scenarioKey = criteria["baseScenarioKey"] as String
                    database.decisionRecordDao().getDecisionsByScenario(scenarioKey)
                }
                criteria.containsKey("ruleHash") -> {
                    val ruleHash = criteria["ruleHash"] as String
                    database.decisionRecordDao().getDecisionsByRuleHash(ruleHash)
                }
                criteria.containsKey("isCorrect") -> {
                    val isCorrect = criteria["isCorrect"] as Boolean
                    val limit = criteria["limit"] as? Int ?: InfrastructureConstants.DEFAULT_RECENT_DECISIONS_LIMIT
                    database.decisionRecordDao().getDecisionsByCorrectness(isCorrect, limit)
                }
                else -> {
                    database.decisionRecordDao().getAllDecisions()
                }
            }
        }
    }
    
    /**
     * 將 RoomHistoryEntity 轉換為 RoundHistory domain 對象
     * 使用 JSON 反序列化將字串轉換回複雜對象
     */
    private fun convertEntityToDomain(entity: RoundHistoryEntity): RoundHistory {
        return RoundHistory(
            roundId = entity.roundId,
            sessionId = entity.sessionId,
            timestamp = entity.timestamp,
            gameRules = json.decodeFromString(entity.gameRulesJson),
            initialBet = entity.initialBet,
            decisions = json.decodeFromString(entity.decisionsJson),
            roundResult = entity.roundResult,
            netChipChange = entity.netChipChange,
            roundDurationMs = entity.roundDurationMs
        )
    }
    
    /**
     * 將 DecisionRecordEntity 轉換為 DecisionRecord domain 對象
     * 使用 JSON 反序列化將字串轉換回複雜對象
     */
    private fun convertDecisionEntityToDomain(entity: DecisionRecordEntity): DecisionRecord {
        return DecisionRecord(
            beforeAction = HandSnapshot(
                cards = json.decodeFromString(entity.handCardsJson),
                dealerUpCard = json.decodeFromString(entity.dealerUpCardJson),
                gameRules = GameRules() // Default rules - in real implementation could be stored separately
            ),
            action = entity.playerAction,
            afterAction = ActionResult.Stand(json.decodeFromString(entity.handCardsJson)), // Placeholder
            isCorrect = entity.isCorrect,
            timestamp = entity.timestamp
        )
    }
}