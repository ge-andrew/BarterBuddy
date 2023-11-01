package com.example.barterbuddy;

import static com.example.barterbuddy.network.UpdateItemDocument.setAsTheActiveItem;

import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.barterbuddy.models.Item;
import com.example.barterbuddy.models.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UpdateItemDocumentInstrumentedTest {
  private FirebaseFirestore localFirestore;

  // CountDownLatch is used to count down for asynchronous tasks
  private final CountDownLatch lock = new CountDownLatch(1);

  private User userAndrew;
  private Item waterBottle;
  private Item scissors;

  private boolean isActiveResult;

  private CollectionReference andrewItemsCollection;

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
    scissors =
        new Item(
            "Scissors",
            "Still very sharp.",
            "andrew@google.com-Scissors",
            false,
            "Andrew Ge",
            "andrew@google.com");
    localFirestore.collection("users").add(userAndrew);
    andrewItemsCollection =
        localFirestore.collection("users").document(userAndrew.getEmail()).collection("items");
    andrewItemsCollection.document(waterBottle.getId()).set(waterBottle);
    andrewItemsCollection.document(scissors.getId()).set(scissors);
  }

  @Test
  public void scissorsGetsSetToActive() throws InterruptedException {
    setAsTheActiveItem(scissors);
    andrewItemsCollection
        .document(scissors.getId())
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              isActiveResult = documentSnapshot.toObject(Item.class).getActive();
              lock.countDown();
            });

    // await is necessary to make sure the asynchronous "get" call
    //  is resolved before testing the assert
    lock.await(2000, TimeUnit.MILLISECONDS);

    assertTrue(isActiveResult);
  }
}
