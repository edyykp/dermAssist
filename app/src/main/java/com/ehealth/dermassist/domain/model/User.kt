package com.ehealth.dermassist.domain.model

data class User(
    val id: String,
    val name: String,
    val age: Int,
    val skinType: String,
    val memberSince: String,
    val email: String,
) {
    fun getInitials(): String {
        val parts = name.trim().split("\\s+".toRegex())

        return when (parts.size) {
            0 -> ""
            1 -> parts[0].first().uppercase()
            else -> "${parts.first().first()}${parts.last().first()}".uppercase()
        }
    }
}
