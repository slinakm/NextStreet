package com.example.nextstreet;

import android.content.Intent;
import android.widget.Button;

import com.example.nextstreet.login.DriverLoginActivity;
import com.example.nextstreet.login.LoginActivity;
import com.example.nextstreet.login.StartActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class StartActivityTest {

    private StartActivity startActivity;

    @Before
    public void setup() {
        startActivity = Robolectric.setupActivity(StartActivity.class);
    }

    @Test
    public void validateButtonsExistence() {
        Button driverButton = (Button) startActivity.findViewById(R.id.driverButton);
        assertNotNull("Driver login button could not be found", driverButton);
        Button userButton = (Button) startActivity.findViewById(R.id.userButton);
        assertNotNull("User login button could not be found", userButton);
    }

    @Test
    public void clickingUserLoginShouldStartLoginActivity() {
        startActivity.findViewById(R.id.userButton).performClick();

        Intent expectedIntent = new Intent(startActivity, LoginActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void clickingDriverLoginShouldStartDriverLoginActivity() {
        startActivity.findViewById(R.id.driverButton).performClick();

        Intent expectedIntent = new Intent(startActivity, DriverLoginActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }
}
