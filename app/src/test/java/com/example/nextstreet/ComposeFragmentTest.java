package com.example.nextstreet;

import android.os.Bundle;
import android.widget.Button;

import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;

import com.example.nextstreet.compose.ComposeFragment;

import org.apache.tools.ant.Main;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ComposeFragmentTest {
    @Test
    public void testEventFragment() {
        Bundle fragmentArgs = new Bundle();

        FragmentFactory factory = new FragmentFactory();
        FragmentScenario scenario
                = FragmentScenario.launchInContainer(ComposeFragment.class, fragmentArgs, factory);
    }
}

