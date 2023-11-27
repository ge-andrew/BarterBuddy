package com.example.barterbuddy;

import com.example.barterbuddy.network.UpdateItemDocument;

import com.example.barterbuddy.models.Item;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class UpdateItemDocumentTest {

  private Item testItem;

  @Before
  public void setupItems() {
    testItem =
        new Item(
                "test@google.com",
            "TestItem",
            "This item is just for use in Unit Tests",
            "test@google.com",
            true,
            "TestUser",
            "test@google.com",
                "1.30");
  }

  @Test
  public void setActive_emailFieldNull_throwsException() {
    testItem.setEmail(null);
    assertThrows(
        NullPointerException.class, () -> UpdateItemDocument.setAsTheActiveItem(testItem));
  }

  @Test
  public void setInactive_titleFieldNull_throwsException() {
    testItem.setTitle(null);
    assertThrows(
        NullPointerException.class, () -> UpdateItemDocument.setAsTheActiveItem(testItem));
  }
}
