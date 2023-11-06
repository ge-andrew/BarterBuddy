package com.example.barterbuddy.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barterbuddy.R;
import com.example.barterbuddy.models.Item;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddNewItemPage extends AppCompatActivity {

  private static final int CAMERA_REQUEST_CODE = 0;
  private static final int GALLERY_REQUEST_CODE = 1;
  private static final String USER_NAME = "username";
  private final FirebaseFirestore DATABASE_INSTANCE = FirebaseFirestore.getInstance();
  private final FirebaseStorage FIRESTORE_INSTANCE = FirebaseStorage.getInstance();
  private final FirebaseAuth AUTHENTICATION_INSTANCE = FirebaseAuth.getInstance();
  private FirebaseUser currentUser;
  private ShapeableImageView itemImageView;
  private Button save_button;
  private TextInputEditText titleEditText;
  private TextInputEditText descriptionEditText;
  private TextInputEditText valueEditText;
  private TextView missingTitleTextView;
  private TextView missingDescriptionTextView;
  private TextView missingValueTextView;
  private TextView missingImageTextView;
  private ImageView backArrow;
  private String username;
  private String email;
  private String title;
  private String description;
  private String perceivedValue;
  private Bitmap photoBitmap;
  private String itemId;
  private Item newItem;
  private boolean imageWasChanged = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_personal_item);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();
    getXmlElements();

    valueEditText.setText("0.00");

    itemImageView.setOnClickListener(view -> showCustomDialog());
    save_button.setOnClickListener(view -> saveItem());

    backArrow.setOnClickListener(view -> finish());

    // formats text field for monetary values
    valueEditText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            String tempString = "";
            perceivedValue = String.valueOf(valueEditText.getText());
            int positionOfDecimal = perceivedValue.indexOf('.');
            int lengthOfValue = perceivedValue.length();
            boolean isLeadingZero;

            if ((lengthOfValue < 3) || (positionOfDecimal != perceivedValue.length() - 3)) {
              isLeadingZero = true;
              for (int index = 0; index < perceivedValue.length(); index++) {
                if (perceivedValue.charAt(index) != '0' && perceivedValue.charAt(index) != '.') {
                  tempString = tempString + perceivedValue.charAt(index);
                  isLeadingZero = false;
                } else if (perceivedValue.charAt(index) == '0' && !isLeadingZero) {
                  tempString = tempString + perceivedValue.charAt(index);
                }
              }

              int lengthOfTempString = tempString.length();
              if (lengthOfTempString < 3) {
                for (int index = 0; index < 3 - lengthOfTempString; index++) {
                  tempString = '0' + tempString;
                }

                String firstHalf = tempString.substring(0, 1);
                String secondHalf = tempString.substring(1, 3);
                tempString = firstHalf + '.' + secondHalf;
              } else {
                String firstHalf = tempString.substring(0, lengthOfTempString - 2);
                String secondHalf =
                    tempString.substring(lengthOfTempString - 2, lengthOfTempString);
                tempString = firstHalf + '.' + secondHalf;
              }
              lengthOfTempString = tempString.length();
              valueEditText.setText(tempString);
              valueEditText.setSelection(lengthOfTempString);
            }
          }
        });
  }

  protected void saveItem() {
    hideWarnings();
    getItemInfo();
    if (missingItemData()) {
      return;
    }
    initializeItem();

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    StorageReference imageReference =
        FIRESTORE_INSTANCE.getReference().child("users/" + email + "/" + itemId + ".jpg");
    byte[] imageData = baos.toByteArray();

    imageReference
        .putBytes(imageData)
        .addOnSuccessListener(taskSnapshot -> {})
        .addOnFailureListener(
            e ->
                Toast.makeText(AddNewItemPage.this, "Failed to upload photo", Toast.LENGTH_SHORT)
                    .show());

    DocumentReference userDocumentReference = DATABASE_INSTANCE.collection("users").document(email);
    Map<String, Object> user_name_to_store = new HashMap<>();
    user_name_to_store.put(USER_NAME, username);
    userDocumentReference.set(user_name_to_store);

    DocumentReference itemDocumentReference =
        DATABASE_INSTANCE.collection("users").document(email).collection("items").document(itemId);

    itemDocumentReference
        .set(newItem)
        .addOnSuccessListener(
            unused -> {
              Toast.makeText(AddNewItemPage.this, "Added item", Toast.LENGTH_SHORT).show();

              Intent intent = new Intent();
              setResult(Activity.RESULT_OK, intent);
              finish();
            })
        .addOnFailureListener(
            e ->
                Toast.makeText(AddNewItemPage.this, "Failed to add item", Toast.LENGTH_SHORT)
                    .show());
  }

  protected void showCustomDialog() {
    Dialog dialog = new Dialog(AddNewItemPage.this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    dialog.setContentView(R.layout.activity_get_image_dialog_box);
    if (dialog.getWindow() != null)
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    Button choose_photo = dialog.findViewById(R.id.choose_photo_button);
    Button take_photo = dialog.findViewById(R.id.take_photo_button);

    take_photo.setOnClickListener(
        v -> {
          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(intent, CAMERA_REQUEST_CODE);
          dialog.dismiss();
        });

    choose_photo.setOnClickListener(
        v -> {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          startActivityForResult(
              Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
          dialog.dismiss();
        });
    dialog.show();
  }

  /** this function handles the results from the camera intent and the gallery intent */
  @Override
  protected void onActivityResult(int request_code, int result_code, Intent data) {
    super.onActivityResult(request_code, result_code, data);
    if (request_code == CAMERA_REQUEST_CODE && result_code == RESULT_OK) {
      // getting bitmap from camera
      photoBitmap = (Bitmap) data.getExtras().get("data");

      // setting image view scale type
      itemImageView.setScaleType(ShapeableImageView.ScaleType.CENTER_CROP);

      // setting image view
      itemImageView.setImageBitmap(photoBitmap);
      imageWasChanged = true;
    } else if (request_code == GALLERY_REQUEST_CODE && result_code == RESULT_OK) {
      // getting data from gallery
      Uri photoUri = data.getData();

      // checking if uri is null
      if (photoUri != null) {

        try {
          // convert uri to bitmap
          photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);

        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        // setting image view scale type
        itemImageView.setScaleType(ShapeableImageView.ScaleType.CENTER_CROP);

        // setting image view
        itemImageView.setImageURI(photoUri);
        imageWasChanged = true;
      }
    }
  }

  private void goToLoginPage() {
    Intent intent = new Intent(getApplicationContext(), LoginPage.class);
    startActivity(intent);
    finish();
  }

  private void getCurrentUser() {
    currentUser = AUTHENTICATION_INSTANCE.getCurrentUser();
  }

  private void getCurrentUserInfo() {
    username = currentUser.getDisplayName();
    email = currentUser.getEmail();
  }

  private void getXmlElements() {
    itemImageView = findViewById(R.id.item_image_view);
    titleEditText = findViewById(R.id.title);
    descriptionEditText = findViewById(R.id.description);
    valueEditText = findViewById(R.id.estimatedValue);
    save_button = findViewById(R.id.save_new_item_button);
    backArrow = findViewById(R.id.back_arrow);
    missingTitleTextView = findViewById(R.id.missingTitle);
    missingDescriptionTextView = findViewById(R.id.missingDescription);
    missingValueTextView = findViewById(R.id.missingValue);
    missingImageTextView = findViewById(R.id.missingImage);
  }

  private boolean missingItemData() {
    boolean dataIsMissing = false;
    if (TextUtils.isEmpty(title)) {
      missingTitleTextView.setVisibility(View.VISIBLE);
      dataIsMissing = true;
    }
    if (TextUtils.isEmpty(description)) {
      missingDescriptionTextView.setVisibility(View.VISIBLE);
      dataIsMissing = true;
    }
    if (TextUtils.isEmpty(perceivedValue)) {
      missingValueTextView.setVisibility(View.VISIBLE);
      dataIsMissing = true;
    }
    if (!imageWasChanged) {
      missingImageTextView.setVisibility(View.VISIBLE);
      dataIsMissing = true;
    }
    return dataIsMissing;
  }

  private void getItemInfo() {
    title = String.valueOf(titleEditText.getText());
    description = String.valueOf(descriptionEditText.getText());
    perceivedValue = String.valueOf(valueEditText.getText());
  }

  private void initializeItem() {
    newItem = new Item();
    itemId = email + "-" + title;
    newItem.setTitle(title);
    newItem.setDescription(description);
    newItem.setUsername(username);
    newItem.setImageId(itemId);
    newItem.setEmail(email);
    newItem.setActive(false);
    newItem.setPerceivedValue(perceivedValue);
  }

  private void hideWarnings() {
    missingTitleTextView.setVisibility(View.GONE);
    missingDescriptionTextView.setVisibility(View.GONE);
    missingValueTextView.setVisibility(View.GONE);
    missingImageTextView.setVisibility(View.GONE);
  }
}
