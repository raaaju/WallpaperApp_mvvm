package com.georgcantor.wallpaperapp.view.fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.georgcantor.wallpaperapp.R
import com.georgcantor.wallpaperapp.view.activity.MainActivity
import com.georgcantor.wallpaperapp.view.adapter.holder.PictureViewHolder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class BmwFragmentTest {

    @get: Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun isRecyclerViewVisible() {
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun openDetailsWhenClickOnItem() {
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<PictureViewHolder>(3, click()))
//        onView(withId(R.id.detailsLayout)).check(matches(isDisplayed()))
    }

    @Test
    fun openDetailsWhenClickOnItemAndPressBack() {
        onView(withId(R.id.recyclerView)).perform(actionOnItemAtPosition<PictureViewHolder>(3, click()))
//        onView(withId(R.id.detailsLayout)).check(matches(isDisplayed()))
        pressBack()
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()))
    }

}