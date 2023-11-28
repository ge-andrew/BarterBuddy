package com.example.barterbuddy.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class TestChatPageClass extends AppCompatActivity {
    // This is a temporary class for testing chat page.
    // Its code can be copied into the See Trade Offers page later on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(TestChatPageClass.this, ChatPage.class);
        intent.putExtra("otherUserEmail", "wadsworth@google.com");
        startActivity(intent);
    }
}
