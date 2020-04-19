package com.georgcantor.wallpaperapp.view.activity

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class DetailActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(DetailActivity::class.java)

    @Test
    fun isActivityInView() {
//        onView(withId(R.id.detailsLayout))
//                .check(matches(isDisplayed()))
    }

}