package com.example.submissionandroidintermediate.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.submissionandroidintermediate.EspressoIdlingResource
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.userinterface.HomePageActivity
import com.example.submissionandroidintermediate.userinterface.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        Intents.init()
        activityScenario = activityRule.scenario
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun loginAndLogout() {
        // Login Test
        onView(withId(R.id.CVEmail)).perform(typeText("kopret1@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.PasswordLogin)).perform(typeText("12345678"), closeSoftKeyboard())
        onView(withId(R.id.btnLogin)).perform(click())

        // Verify loading indicator is shown
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        // Wait for API response with idling resource
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        // redirect to HomePageActivity
        ActivityScenario.launch(HomePageActivity::class.java)

        // Verify user is logged in with checking displayed data
        onView(withId(R.id.rv_stories))
            .check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(
                10
            )
        )

        // Logout Test
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText(R.string.logout)).perform(click())
        onView(withText(R.string.logoutDescription)).check(matches(isDisplayed()))
        onView(withText(R.string.yes)).perform(click())

        // Verify user is logged out
        onView(withId(R.id.tvLogin)).check(matches(withText(R.string.tvLogin)))
    }
}
