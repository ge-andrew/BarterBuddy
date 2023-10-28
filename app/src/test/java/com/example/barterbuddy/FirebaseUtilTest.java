package com.example.barterbuddy;

import com.example.barterbuddy.utils.FirebaseUtil;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FirebaseUtilTest {
  String user1;
  String user2;

  @Before // setup
  public void setup() {
    user1 = "Alice";
    user2 = "Bob";
  }

  @Test
  public void getChatroomId_twoStrings_returnsCombination() {
    assertEquals(FirebaseUtil.getChatroomId(user1, user2), "Bob_Alice");
    assertEquals(FirebaseUtil.getChatroomId(user2, user1), "Bob_Alice");
  }
}
