package jp.co.keyplanning.itfescounter

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CounterTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun fiveVisitorsAndReset() {
        compose.onNodeWithText("0人").assertIsDisplayed()
        repeat(5) { compose.onNodeWithText("来場者+1").performClick() }
        compose.onNodeWithText("5人").assertIsDisplayed()
        compose.onNodeWithText("10%").assertIsDisplayed()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithText("5人").assertIsDisplayed()
        compose.onNodeWithText("リセット").performClick()
        compose.onNodeWithText("0人").assertIsDisplayed()
        compose.onNodeWithText("0%").assertIsDisplayed()
    }

    @Test
    fun capacityIsExceededOnlyAfterFifty() {
        repeat(40) { compose.onNodeWithText("来場者+1").performClick() }
        compose.onNodeWithText("80%").assertIsDisplayed()
        repeat(10) { compose.onNodeWithText("来場者+1").performClick() }
        compose.onNodeWithText("100%").assertIsDisplayed()
        compose.onNodeWithText("定員オーバー").assertDoesNotExist()
        compose.onNodeWithText("来場者+1").performClick()
        compose.onNodeWithText("51人").assertIsDisplayed()
        compose.onNodeWithText("102%").assertIsDisplayed()
        compose.onNodeWithText("定員オーバー").assertIsDisplayed()
        compose.onNodeWithText("リセット").performClick()
        compose.onNodeWithText("定員オーバー").assertDoesNotExist()
        compose.onNodeWithText("0%").assertIsDisplayed()
    }
}
