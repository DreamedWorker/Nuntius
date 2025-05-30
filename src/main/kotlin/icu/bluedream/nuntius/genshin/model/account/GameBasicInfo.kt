package icu.bluedream.nuntius.genshin.model.account

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class GameBasicInfo(
    val retcode: Long,
    val message: String,
    val data: GameBasicInfoClass
) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class GameBasicInfoClass(
        val list: List<GameBasic>
    ) {
        @Serializable
        @JsonIgnoreUnknownKeys
        data class GameBasic(
            @SerialName("game_biz") val gameBiz: String,
            val region: String,
            @SerialName("game_uid") val gameUid: String,
            val nickname: String,
            val level: Int,
            @SerialName("region_name") val regionName: String
        )
    }
}
