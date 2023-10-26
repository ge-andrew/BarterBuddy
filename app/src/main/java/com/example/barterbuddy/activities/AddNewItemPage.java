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
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
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
  private String username;
  private String email;
  private String title;
  private String description;
  private Bitmap photoBitmap;
  private String itemId;
  private Item newItem;
  private boolean imageWasChanged = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_item);

    getCurrentUser();
    if (currentUser == null) {
      goToLoginPage();
    }
    getCurrentUserInfo();
    getXmlElements();

    itemImageView.setOnClickListener(view -> showCustomDialog());
    save_button.setOnClickListener(
            view -> {
              saveItem();
            });
  }

  protected void saveItem() {
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
        DATABASE_INSTANCE
            .collection("users")
            .document(newItem.getEmail())
            .collection("items")
            .document(newItem.getImageId());

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

      // setting image view width
      ViewGroup.LayoutParams layoutParams = itemImageView.getLayoutParams();
      layoutParams.width = -2;

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

        // setting image view width
        ViewGroup.LayoutParams layoutParams = itemImageView.getLayoutParams();
        layoutParams.width = -2;

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
    save_button = findViewById(R.id.save_new_item_button);
  }

  private boolean missingItemData() {
    if ((TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && !imageWasChanged)
            || (TextUtils.isEmpty(title) && TextUtils.isEmpty(description))
            || ((TextUtils.isEmpty(title) && !imageWasChanged))
            || ((TextUtils.isEmpty(description) && !imageWasChanged))) {
      Toast.makeText(AddNewItemPage.this, "Missing information", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(title)) {
      Toast.makeText(AddNewItemPage.this, "Missing title", Toast.LENGTH_SHORT).show();
      return true;
    } else if (TextUtils.isEmpty(description)) {
      Toast.makeText(AddNewItemPage.this, "Missing description", Toast.LENGTH_SHORT).show();
      return true;
    } else if (!imageWasChanged) {
      Toast.makeText(AddNewItemPage.this, "Missing picture", Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }

  private void getItemInfo() {
    title = String.valueOf(titleEditText.getText());
    description = String.valueOf(descriptionEditText.getText());
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
  }
}
