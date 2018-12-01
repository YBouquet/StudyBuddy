package ch.epfl.sweng.studdybuddy;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.epfl.sweng.studdybuddy.activities.NavigationActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static ch.epfl.sweng.studdybuddy.NavigationTestHelper.navigate;

public class SettingsSignOutTest {
    @Rule
    public IntentsTestRule<NavigationActivity> mActivityTestRule = new IntentsTestRule<>(NavigationActivity.class);

    @Before
    public void setup(){
        navigate("Settings", R.id.navToSettings,3);
    }


    @Test
    public void signoutWorks() throws InterruptedException {
        Thread.sleep(1000);
        onView(withId(R.id.btn_sign_out)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.googleBtn)).check(matches(isDisplayed()));
    }
}
