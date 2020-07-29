package com.vitkaloff.benzorro;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.Visibility.VISIBLE;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Rule
    public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.vitkaloff.benzorro", appContext.getPackageName());
    }

    @Test
    public void isDisplayed() {
        // провер€ет, отображаетс€ ли объект на экране
        // главна€ страница
        onView(withId(R.id.nav_view)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.nav_host_fragment)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.services)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.choice_chip)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.choice_chip2)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.choice_chip3)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.choice_chip4)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.choice_chip5)).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.FuelStationList)).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void clickView(){
        // провер€ем, что объекты отображаютс€ на экране после нажати€
        // главна€ страница
        onView(withId(R.id.nav_view)).perform(click()).check(matches(ViewMatchers.isDisplayed()));
        onView(withId(R.id.nav_host_fragment)).perform(click()).check(matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void scrollView(){
        onView(withId(R.id.FuelStationList)).perform(scrollTo());
        onView(withId(R.id.FuelStationList)).perform(RecyclerViewActions.scrollToPosition(99));
    }

    @Test
    public void bottomButtons(){
        // провер€ем, правильно измен€ютс€ объекты в нижней панели при нажатии

        // переход на главную страницу
        onView(withId(R.id.navigation_home)).perform(click());
        // проверка: видна ли надпись "«аправки" на иконке главной страницы
        onView(withId(R.id.navigation_home)).check(matches(ViewMatchers.isSelected()));
        // проверка: спр€таны ли надписи других иконок
        onView(withId(R.id.navigation_dashboard)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_notifications)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_about)).check(matches(not(ViewMatchers.isSelected())));

        // переход на страницу с картой
        onView(withId(R.id.navigation_dashboard)).perform(click());
        // проверка: видна ли надпись "Ќа карте" на иконке открытой страницы
        onView(withId(R.id.navigation_dashboard)).check(matches(ViewMatchers.isSelected()));
        // проверка: спр€таны ли надписи других иконок
        onView(withId(R.id.navigation_home)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_notifications)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_about)).check(matches(not(ViewMatchers.isSelected())));

        // переход на страницу с динамикой
        onView(withId(R.id.navigation_notifications)).perform(click());
        // проверка: видна ли надпись "ƒинамика" на иконке открытой страницы
        onView(withId(R.id.navigation_notifications)).check(matches(ViewMatchers.isSelected()));
        // проверка: спр€таны ли надписи других иконок
        onView(withId(R.id.navigation_home)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_dashboard)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_about)).check(matches(not(ViewMatchers.isSelected())));

        onView(withId(R.id.navigation_about)).perform(click());
        // проверка: видна ли надпись "»нформаци€" на иконке открытой страницы
        onView(withId(R.id.navigation_about)).check(matches(ViewMatchers.isSelected()));
        // проверка: спр€таны ли надписи других иконок
        onView(withId(R.id.navigation_home)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_dashboard)).check(matches(not(ViewMatchers.isSelected())));
        onView(withId(R.id.navigation_notifications)).check(matches(not(ViewMatchers.isSelected())));
    }

    @Test
    public void topButtons() {

        // переход на главную страницу
        onView(withId(R.id.navigation_home)).perform(click());

        // провер€ем, что варианты топлива измен€ютс€ при нажатии (ставитс€ галочка)
        // проверка первой кнопки
        onView(withId(R.id.choice_chip)).perform(click()).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip2)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip3)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip4)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip5)).check(matches(ViewMatchers.isEnabled()));

        // проверка второй кнопки
        onView(withId(R.id.choice_chip2)).perform(click()).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip3)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip4)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip5)).check(matches(ViewMatchers.isEnabled()));

        // проверка третьей кнопки
        onView(withId(R.id.choice_chip3)).perform(click()).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip2)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip4)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip5)).check(matches(ViewMatchers.isEnabled()));

        // проверка четвЄртой кнопки
        onView(withId(R.id.choice_chip4)).perform(click()).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip2)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip3)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip5)).check(matches(ViewMatchers.isEnabled()));

        // проверка п€той кнопки
        onView(withId(R.id.choice_chip5)).perform(click()).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip2)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip3)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip4)).check(matches(ViewMatchers.isEnabled()));
        onView(withId(R.id.choice_chip)).check(matches(ViewMatchers.isEnabled()));
    }

    @Test
    public void recyclerViewData() {
        // переход на главную страницу
        onView(withId(R.id.navigation_home)).perform(click());
        //onView(allOf(withId(R.id.brand))).check(matches(not(withText("Бренд"))));
        //onView(allOf(withId(R.id.address))).check(matches(not(withText("Адрес"))));
        //onView(allOf(withId(R.id.price))).check(matches(not(withText("Цена"))));
        //onView(allOf(withId(R.id.distance))).check(matches(not(withText("Расстояние"))));
    }

    @Test
    public void ifDataIsCorrect() {
        // переход на главную страницу
        onView(withId(R.id.navigation_home)).perform(click());

        onView(allOf(withId(R.id.price), withEffectiveVisibility(VISIBLE))).check(matches(not(withText("0"))));
    }

    @Test
    public void checkStationScreenObjectVisibility() {
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.FuelStationList))
                .perform(RecyclerViewActions.actionOnItemAtPosition((int) (Math.random()*10), click()));
        onView(withId(R.id.address)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.logo)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.distance)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.brand)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.Prices)).check(matches(withEffectiveVisibility(VISIBLE)));

        onView(withId(R.id.phone)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.email)).check(matches(withEffectiveVisibility(VISIBLE)));
        onView(withId(R.id.website)).check(matches(withEffectiveVisibility(VISIBLE)));

        onView(withId(R.id.shareStation)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.toFavourites)).check(matches(isCompletelyDisplayed()));

        onView(withId(R.id.openRouteMap)).check(matches(isCompletelyDisplayed()));
    }

    @Test
    public void checkStationScreenClickable() {
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.FuelStationList))
                .perform(RecyclerViewActions.actionOnItemAtPosition((int) (Math.random()*10), click()));

        onView(withId(R.id.address)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.logo)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.distance)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.brand)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.Prices)).check(matches(withEffectiveVisibility(VISIBLE)));

        onView(withId(R.id.phone)).check(matches(isClickable()));
        onView(withId(R.id.website)).check(matches(isClickable()));
        onView(withId(R.id.email)).check(matches(isClickable()));
        onView(withId(R.id.openRouteMap)).check(matches(isClickable()));
        onView(withId(R.id.toFavourites)).check(matches(isClickable()));
        onView(withId(R.id.shareStation)).check(matches(isClickable()));

        onView(withId(R.id.toFavourites)).perform(click());
        onView(withId(R.id.shareStation)).perform(click());
    }

    @Test
    public void checkStationObjectDataIntegrity() {
        onView(withId(R.id.navigation_home)).perform(click());
        onView(withId(R.id.FuelStationList))
                .perform(RecyclerViewActions.actionOnItemAtPosition((int) (Math.random()*10), click()));

        onView(withId(R.id.address)).check(matches(not(withText(""))));
        onView(withId(R.id.distance)).check(matches(not(withText(""))));
        onView(withId(R.id.brand)).check(matches(not(withText(""))));
        onView(withId(R.id.Prices)).check(matches(not(withText(""))));

        Intents.init();

        Intent stubIntent = new Intent();
        Instrumentation.ActivityResult stubResult = new Instrumentation.ActivityResult(Activity.RESULT_OK, stubIntent);

        intending(hasAction(Intent.ACTION_VIEW)).respondWith(stubResult);
        onView(withId(R.id.phone)).perform(click());
        intended(Matchers.allOf(hasAction(Intent.ACTION_VIEW)));
        Intents.release();

        Intents.init();
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(stubResult);
        onView(withId(R.id.email)).perform(click());
        intended(Matchers.allOf(hasAction(Intent.ACTION_VIEW)));
        Intents.release();

        Intents.init();
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(stubResult);
        onView(withId(R.id.website)).perform(click());
        intended(Matchers.allOf(hasAction(Intent.ACTION_VIEW)));
        Intents.release();

        Intents.init();
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(stubResult);
        onView(withId(R.id.openRouteMap)).perform(click());
        intended(Matchers.allOf(hasAction(Intent.ACTION_VIEW)));

        Intents.release();
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}
