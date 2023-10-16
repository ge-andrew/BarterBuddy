package com.example.barterbuddy;

import com.example.barterbuddy.network.UpdateItemDocument;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.barterbuddy.models.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateItemDocumentTest {

  // Since we don't have access to Firestore we must mock it and pass it into the functions we test
  @Mock FirebaseFirestore mockDb;
  @Mock
  CollectionReference mockUsersRef;
  @Mock
  DocumentReference mockUserRef;
  @Mock
  CollectionReference mockItemsRef;
  @Mock
  DocumentReference mockItemRef;
  @Mock
  Task<Void> mockTask;


  private Item testItem;

  @Before // setup
  public void initMocks() {
    // Set up the mock database to return mock references
    Mockito.when(mockDb.collection("users"))
            .thenReturn(mockUsersRef);
    Mockito.when(mockUsersRef.document(Mockito.anyString()))
            .thenReturn(mockUserRef);
    Mockito.when(mockUserRef.collection("items"))
            .thenReturn(mockItemsRef);
    Mockito.when(mockItemsRef.document(Mockito.anyString()))
            .thenReturn(mockItemRef);
    Mockito.when(mockItemRef.update(Mockito.anyString(), Mockito.anyBoolean()))
            .thenReturn(mockTask);
    Mockito.when(mockTask.addOnSuccessListener(Mockito.any()))
            .thenReturn(mockTask);
    Mockito.when(mockTask.addOnFailureListener(Mockito.any()))
            .thenReturn(mockTask);

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

  @Test
  public void makeItemActive_changedStatus_returnsTrue() {
    assertTrue(UpdateItemDocument.makeItemActive(testItem, false, mockDb));
  }

  @Test
  public void makeItemActive_emailFieldNull_throwsException() {
    testItem.setEmail(null);
    assertThrows(
        NullPointerException.class,
        () -> UpdateItemDocument.makeItemActive(testItem, true, mockDb));
  }

  @Test
  public void makeItemActive_titleFieldNull_throwsException() {
    testItem.setTitle(null);
    assertThrows(
            NullPointerException.class,
            () -> UpdateItemDocument.makeItemActive(testItem, true, mockDb));
  }
}
