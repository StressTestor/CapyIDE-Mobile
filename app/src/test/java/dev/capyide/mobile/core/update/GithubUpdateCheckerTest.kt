package dev.capyide.mobile.core.update

import org.junit.Assert.assertEquals
import org.junit.Test

class GithubUpdateCheckerTest {

    @Test
    fun versionNameToCode_handlesSemVer() {
        val code = GithubUpdateChecker.versionNameToCode("1.2.3")
        assertEquals(10203, code)
    }

    @Test
    fun versionNameToCode_ignoresNonNumericSegments() {
        val code = GithubUpdateChecker.versionNameToCode("1.2-beta")
        assertEquals(102, code)
    }

    @Test
    fun versionNameToCode_coercesMinimumValue() {
        val code = GithubUpdateChecker.versionNameToCode("")
        assertEquals(1, code)
    }
}
