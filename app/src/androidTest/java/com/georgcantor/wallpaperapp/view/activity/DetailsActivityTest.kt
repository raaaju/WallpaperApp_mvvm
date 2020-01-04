package com.georgcantor.wallpaperapp.view.activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.georgcantor.wallpaperapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DetailsActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(DetailsActivity::class.java)

    @Test
    fun isActivityInView() {
        onView(withId(R.id.detailsLayout))
                .check(matches(isDisplayed()))
    }

}