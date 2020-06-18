package io.geekhub.service.account.web.model

import org.hibernate.validator.constraints.Length

data class UpdateClientUserRequest(
        val name: String,
        val nickname: String,
        @Length(min = 0, max = 100)
        val companyName: String?,
        @Length(min = 0, max = 1000)
        val note: String?,
        @Length(min = 0, max = 200)
        val linkedIn: String?,
        @Length(min = 0, max = 200)
        val github: String?,
        @Length(min = 0, max = 200)
        val facebook: String?,
        @Length(min = 0, max = 200)
        val ig: String?,
        @Length(min = 0, max = 200)
        val twitter: String?
) {

    fun toSocialProfiles(): List<SocialProfile> {
        return listOf(
                SocialProfile("linkedIn", (this.linkedIn ?: "")),
                SocialProfile("github", (this.github ?: "")),
                SocialProfile("facebook", (this.facebook ?: "")),
                SocialProfile("ig", (this.ig ?: "")),
                SocialProfile("twitter", (this.twitter ?: ""))
        )
    }

    data class SocialProfile(val name: String, val value: String)
}