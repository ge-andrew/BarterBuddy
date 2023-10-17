package com.example.barterbuddy;

import com.example.barterbuddy.network.UpdateItemDocument;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.barterbuddy.models.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateItemDocumentTest {

  // Since we don't have access to Firestore we must mock it and pass it into the functions we test
  @Mock FirebaseFirestore mockDb;

  private Item testItem;

  @Before
  public void setupItems() {
    testItem =
        new Item(
            "TestItem",
            "This item is just for use in Unit Tests",
            "test@google.com",
            true,
            "TestUser",
            "test@google.com",
            mockDb);
  }

  @Test
  public void setActive_emailFieldNull_throwsException() {
    testItem.setEmail(null);
    assertThrows(
        NullPointerException.class, () -> UpdateItemDocument.setItemToActive(testItem));
  }

  @Test
  public void setInactive_titleFieldNull_throwsException() {
    testItem.setTitle(null);
    assertThrows(
        NullPointerException.class, () -> UpdateItemDocument.setItemToActive(testItem));
  }
}
