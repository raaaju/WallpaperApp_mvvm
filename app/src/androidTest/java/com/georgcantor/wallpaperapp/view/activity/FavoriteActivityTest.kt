package com.georgcantor.wallpaperapp.view.activity

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import org.junit.Rule
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class FavoriteActivityTest {

    @get: Rule
    val rule = ActivityScenarioRule(MainActivity::class.java)

//    @Test
//    fun isPictureAddedToFavorites() {
//        onView(withId(R.id.recyclerView))
//                .perform(actionOnItemAtPosition<PictureViewHolder>(3, click()))
//        onView(withId(R.id.detailsLayout))
//                .check(matches(ViewMatchers.isDisplayed()))
//        onView(withId(R.id.action_add_to_fav))
//                .perform(click())
//
//        pressBack()
//
//        onView(withId(R.id.drawerLayout))
//                .check(matches(isClosed(Gravity.LEFT)))
//                .perform(DrawerActions.open())
//        onView(withId(R.id.navView))
//                .perform(NavigationViewActions.navigateTo(R.id.nav_favorites))
//    }

}