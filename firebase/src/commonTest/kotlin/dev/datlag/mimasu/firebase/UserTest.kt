package dev.datlag.mimasu.firebase

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import dev.datlag.mimasu.firebase.auth.User
import kotlin.test.Test

class UserTest {

    @Test
    fun `email to name`() {
        val email = "test.user0123@example.com"
        val converted = User.emailToName(email)
        val expected = "Test"

        assertThat(converted).isEqualTo(expected)
    }

    @Test
    fun `email with number to name`() {
        val email = "firstname123lastname@example.com"
        val converted = User.emailToName(email)
        val expected = "Firstname"

        assertThat(converted).isEqualTo(expected)
    }

    @Test
    fun `empty email to null name`() {
        val email = ""
        val converted = User.emailToName(email)

        assertThat(converted).isNull()
    }

    @Test
    fun `number email to null name`() {
        val email = "0123456789@example.com"
        val converted = User.emailToName(email)

        assertThat(converted).isNull()
    }
}