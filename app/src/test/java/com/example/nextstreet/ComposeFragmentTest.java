package com.example.nextstreet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.nextstreet.compose.ComposeFragment;
import com.example.nextstreet.databinding.FragmentComposeBinding;

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
    fragmentScenario = getFragmentScenario();
  }

  private FragmentScenario<ComposeFragment> getFragmentScenario() {
    Bundle fragmentArgs = new Bundle();

    FragmentFactory factory = new FragmentFactory();
    fragmentScenario = FragmentScenario.launch(ComposeFragment.class, fragmentArgs, factory);
    return fragmentScenario;
  }

  @Test
  public void testButtons() {
    fragmentScenario.onFragment(new testCancelButton());
    fragmentScenario = getFragmentScenario();
    fragmentScenario.onFragment(new testSubmitButton());
  }

  private class testCancelButton implements FragmentScenario.FragmentAction<ComposeFragment> {

    @Override
    public void perform(@NonNull ComposeFragment fragment) {
      fragment.getFragmentComposeBinding().ivCancel.performClick();
      shadowOf(getMainLooper()).idle();

      assert(!fragment.isVisible());
    }
  }

  private class testSubmitButton implements FragmentScenario.FragmentAction<ComposeFragment> {

    @Override
    public void perform(@NonNull ComposeFragment fragment) {
      fragment.getFragmentComposeBinding().submitButton.performClick();
      shadowOf(getMainLooper()).idle();

      FragmentComposeBinding binding = fragment.getFragmentComposeBinding();
      assert(binding.pbLoading.isAnimating()
              || binding.pbLoading.isIndeterminate());
    }
  }
  
}

