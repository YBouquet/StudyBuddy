package ch.epfl.sweng.studdybuddy;

import android.content.ComponentName;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.studdybuddy.activities.CourseSelectActivity;
import ch.epfl.sweng.studdybuddy.activities.ProfileTab;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)

public class MainActivityTest {
  /*  @Rule
    public IntentsTestRule<DummyMainActivity> DummyMainActivityIntentRule =
            new IntentsTestRule<>(DummyMainActivity.class);

    @Test
    public void clickGoToGroupsGoToGroupsActivity() throws InterruptedException{
        Thread.sleep(1000);
        onView(withId(R.id.gotoGroups)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), GroupsActivity.class)));
    }

    @Test
    public void clickCourseButtonGoToCourseSelectActivity() throws InterruptedException{
        Thread.sleep(1000);
        onView(withId(R.id.courseButton)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), CourseSelectActivity.class)));
    }

    @Test
    public void clickProfileButtonGoToCProfileTab() throws InterruptedException{
        Thread.sleep(1000);
        onView(withId(R.id.go_to_profile_btn)).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), ProfileTab.class)));
    }
    */
}
