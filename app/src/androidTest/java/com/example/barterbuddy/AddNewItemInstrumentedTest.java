package com.example.barterbuddy;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AddNewItemInstrumentedTest {
  private FirebaseFirestore localFirestore;

  private User userAndrew;
  private Item waterBottle;

  @Before
  public void setUpFirestoreInstance() {
    // 10.0.2.2 is the special IP address to connect to the 'localhost' of
    // the host computer from an Android emulator.
    localFirestore = FirebaseFirestore.getInstance();
    localFirestore.useEmulator("10.0.2.2", 8080);

    FirebaseFirestoreSettings settings =
        new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
    localFirestore.setFirestoreSettings(settings);
  }

  @Before
  public void populateLocalFirestoreWithData() {
    userAndrew = new User("Andrew Ge", "andrew@google.com", "password");
    waterBottle =
        new Item(
            "Insulated Water Bottle",
            "One dent!",
            "andrew@google.com-Insulated Water Bottle",
            false,
            "Andrew Ge",
            "andrew@google.com");
  }

    @Test
    public void addScissors() {

    }
}
