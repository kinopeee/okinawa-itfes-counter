package jp.co.keyplanning.okinawaitfes

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

data class Choice(val value: String, val label: String)

object Questions {
    val satisfaction = listOf("1 とても不満", "2 不満", "3 普通", "4 満足", "5 とても満足")
        .mapIndexed { index, label -> Choice((index + 1).toString(), label) }
    val discovery = listOf(
        Choice("sns", "SNS"), Choice("website", "Webサイト"),
        Choice("referral", "知人・同僚の紹介"), Choice("poster", "チラシ・ポスター"),
        Choice("other", "その他"),
    )
    val age = listOf(
        Choice("under20", "10代以下"), Choice("20s", "20代"), Choice("30s", "30代"),
        Choice("40s", "40代"), Choice("50s", "50代"), Choice("60plus", "60代以上"),
        Choice("no_answer", "回答しない"),
    )
}

data class Answer(val satisfaction: Int, val discovery: String, val ageGroup: String) {
    init {
        require(satisfaction in 1..5)
        require(Questions.discovery.any { it.value == discovery })
        require(Questions.age.any { it.value == ageGroup })
    }
}

data class Response(val id: String, val submittedAt: String, val answer: Answer) {
    init {
        require(UUID.fromString(id).toString().equals(id, ignoreCase = true))
        require(OffsetDateTime.parse(submittedAt).offset == ZoneOffset.UTC)
    }

    companion object {
        fun create(answer: Answer) = Response(UUID.randomUUID().toString(), Instant.now().toString(), answer)
    }
}

enum class Capacity { NORMAL, WARNING, OVER }

fun capacity(count: Int): Capacity = when {
    count > 50 -> Capacity.OVER
    count >= 40 -> Capacity.WARNING
    else -> Capacity.NORMAL
}

fun percentage(count: Int): Long = count.toLong() * 2

fun counts(responses: List<Response>, choices: List<Choice>, select: (Answer) -> String): List<Int> =
    choices.map { choice -> responses.count { select(it.answer) == choice.value } }
