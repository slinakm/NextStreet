package com.example.nextstreet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.nextstreet.compose.ComposeFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.os.Looper.getMainLooper;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ComposeFragmentTest {

  FragmentScenario<ComposeFragment> fragmentScenario;

  @Before
  public void setup() {
    Bundle fragmentArgs = new Bundle();

    FragmentFactory factory = new FragmentFactory();
    fragmentScenario = FragmentScenario.launch(ComposeFragment.class, fragmentArgs, factory);
  }

  @Test
  public void testButtons() {
    fragmentScenario.onFragment(new testCancelButton());
  }

  private class testCancelButton implements FragmentScenario.FragmentAction<ComposeFragment> {

    @Override
    public void perform(@NonNull ComposeFragment fragment) {
      fragment.getFragmentComposeBinding().ivCancel.performClick();
      shadowOf(getMainLooper()).idle();

      assert(!fragment.isVisible());
    }
  }
}
