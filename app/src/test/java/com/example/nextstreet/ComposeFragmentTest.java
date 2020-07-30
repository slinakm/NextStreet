package com.example.nextstreet;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.nextstreet.compose.ComposeFragment;
import com.example.nextstreet.login.StartActivity;

import org.apache.tools.ant.Main;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class ComposeFragmentTest {

    private ComposeFragment composeFragment;
    final FakeContext fakeContext = new FakeContext();
    com.example.nextstreet.databinding.FragmentComposeBinding binding;
    @Before
    public void setup() {
        Bundle args = new Bundle();

        composeFragment = new ComposeFragment() {
            @Nullable
            @Override
            public Context getContext() {
                return fakeContext;
            }
        };

        LayoutInflater fakeLayoutInflator = new FakeLayoutInflator(fakeContext);
        ViewGroup fakeViewGroup = new FakeViewGroup(fakeContext);

        composeFragment.onCreateView(fakeLayoutInflator,
                fakeViewGroup, args);

        binding = composeFragment.getFragmentComposeBinding();
    }

    @Test
    public void testButtonClick() {
        binding.ivCancel.performClick();
        assert(!composeFragment.isVisible());
    }

    private static class FakeContext extends Activity { }
    private static class FakeLayoutInflator extends LayoutInflater {
        protected FakeLayoutInflator(Context context) {
            super(context);
        }
        @Override
        public LayoutInflater cloneInContext(Context context) {
            return this;
        }
    }
    private static class FakeViewGroup extends ViewGroup {
        public FakeViewGroup(Context context) {
            super(context);
        }
        @Override
        protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

        }
    }

}

