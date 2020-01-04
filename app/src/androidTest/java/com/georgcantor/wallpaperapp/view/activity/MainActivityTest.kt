package com.georgcantor.wallpaperapp.view.activity

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.georgcantor.wallpaperapp.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class MainActivityTest {

    @Test
    fun isActivityInView() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.drawerLayout))
                .check(matches(isDisplayed()))
    }

    @Test
    fun isNavigationDisplayed() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.navigation))
                .check(matches(isDisplayed()))
    }

    @Test
    fun navigateSearchActivity() {
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.action_search))
                .perform(click())
        onView(withId(R.id.searchLayout))
                .check(matches(isDisplayed()))
    }

}

