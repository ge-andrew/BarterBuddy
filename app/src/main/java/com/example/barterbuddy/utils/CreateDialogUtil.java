package com.example.barterbuddy.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.barterbuddy.R;
import com.example.barterbuddy.network.UpdateUserDocument;

public class CreateDialogUtil {
  public static void createRateUserDialogBox(Context activityContext, String ratedUserEmail) {
    Dialog rateUserDialog = new Dialog(activityContext);
    Window dialogWindow = rateUserDialog.getWindow();
    assert dialogWindow != null;
    dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
    rateUserDialog.setCancelable(true);
    rateUserDialog.setCanceledOnTouchOutside(true);
    rateUserDialog.setContentView(R.layout.dialog_rate_user);
    dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    Button skip = rateUserDialog.findViewById(R.id.skip_button);
    Button submit = rateUserDialog.findViewById(R.id.submit_button);
    RatingBar ratingBar = rateUserDialog.findViewById(R.id.rating_bar);
    TextView usernameView = rateUserDialog.findViewById(R.id.username);

    FirebaseUtil.getUserReference(ratedUserEmail)
        .get()
        .addOnSuccessListener(
            documentSnapshot -> {
              String username = documentSnapshot.getString("username");
              usernameView.setText(username);
            });

    submit.setOnClickListener(
        l -> {
          int rating = ratingBar.getNumStars();
          if (rating == 0) {
            Toast toast =
                Toast.makeText(activityContext, "Please select a rating", Toast.LENGTH_LONG);
            toast.show();
          } else {
            UpdateUserDocument.addRating(ratedUserEmail, rating);
            rateUserDialog.dismiss();
          }
        });

    skip.setOnClickListener(v -> rateUserDialog.dismiss());
    rateUserDialog.show();
  }
}
