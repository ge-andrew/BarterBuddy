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

  @Before // setup
  public void before() {
    testItem =
        new Item(
            "TestItem",
            "This item is just for use in Unit Tests",
            "test@google.com",
            true,
            "TestUser",
            "test@google.com");
  }

  @Test
  public void makeItemActive_unchangedStatus_returnsFalse() {
    assertFalse(UpdateItemDocument.makeItemActive(testItem, true, mockDb));
  }
}
