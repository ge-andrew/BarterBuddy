package com.example.barterbuddy;

import static org.junit.Assert.*;

import com.example.barterbuddy.network.UpdateUserDocument;

import org.junit.Test;

public class UpdateUserDocumentTest {

  @Test
  public void test_goodRating_improvesAverage() {
    float oldTotalRating = 3 + 4 + 2 + 1 + 5 + 4;
    int numOfRatings = 6;
    int newRating = 5;
    double oldAverage = oldTotalRating / numOfRatings;
    double updatedAverage = (oldTotalRating + newRating) / (numOfRatings + 1);
    assertEquals(UpdateUserDocument.calculateNewRating(oldAverage, numOfRatings, newRating), updatedAverage, 0.01);
  }
}
