package com.example.mypersonality

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import com.example.mypersonality.core.model.UserRole
import com.example.mypersonality.feature.auth.AuthContent
import com.example.mypersonality.feature.auth.AuthUiState
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.fotur.mypersonality", appContext.packageName)
    }

    @Test
    fun authScreen_showsRoleAndActionTexts() {
        composeRule.setContent {
            AuthContent(
                state = AuthUiState(
                    fullName = "Иван Петров",
                    email = "ivan@example.com",
                    selectedRole = UserRole.SEEKER
                ),
                onNameChanged = {},
                onEmailChanged = {},
                onPasswordChanged = {},
                onRoleChanged = {},
                onModeChanged = {},
                onSubmit = {}
            )
        }

        composeRule.onNodeWithText("Соискатель").assertIsDisplayed()
        composeRule.onNodeWithText("Зарегистрироваться").assertIsDisplayed()
    }
}
