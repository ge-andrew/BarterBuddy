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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddNewItemPage extends AppCompatActivity {

  // request codes to allow result handler to find correct result
  private static final int CAMERA_REQUEST = 0;
  private static final int GALLERY_REQUEST = 1;

  // constant for Firestore setup
  private static final String USER_NAME = "username";
  private final FirebaseFirestore DB_USER = FirebaseFirestore.getInstance();
  private final FirebaseFirestore DB_ITEM = FirebaseFirestore.getInstance();
  private final FirebaseStorage DB_IMAGES = FirebaseStorage.getInstance();
  // used to determine if a picture was taken
  boolean imageWasChanged = false;
  // declaring views and buttons
  ShapeableImageView itemImageView;
  Button save_button;
  TextInputEditText titleEditText;
  TextInputEditText descriptionEditText;
  // variables for item
  String userName;
  String email;
  String title;
  String description;
  String itemId;
  Bitmap photoBitmap;
  Uri photoUri;
  StorageReference imageReference;

  /** this function is called when the activity is first loaded */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_item);

    // getting data from intent
    userName = getIntent().getStringExtra("username");
    email = getIntent().getStringExtra("email");

    // getting views and buttons
    itemImageView = findViewById(R.id.item_image_view);
    titleEditText = findViewById(R.id.title);
    descriptionEditText = findViewById(R.id.description);
    save_button = findViewById(R.id.save_new_item_button);

    // setting up onclick behaviors
    itemImageView.setOnClickListener(view -> showCustomDialog());
    save_button.setOnClickListener(
        view -> {
          // saving item
          saveItem();
        });
  }

  /** This function saves teh data from the data fields and saves them to the database */
  protected void saveItem() {
    Item newItem = new Item();

    title = String.valueOf(titleEditText.getText());
    description = String.valueOf(descriptionEditText.getText());
    itemId = email + "-" + title;

    // checking for empty fields
    if ((TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && !imageWasChanged)
        || (TextUtils.isEmpty(title) && TextUtils.isEmpty(description))
        || ((TextUtils.isEmpty(title) && !imageWasChanged))
        || ((TextUtils.isEmpty(description) && !imageWasChanged))) {
      Toast.makeText(AddNewItemPage.this, "Missing information", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(title)) {
      Toast.makeText(AddNewItemPage.this, "Missing title", Toast.LENGTH_SHORT).show();
    } else if (TextUtils.isEmpty(description)) {
      Toast.makeText(AddNewItemPage.this, "Missing description", Toast.LENGTH_SHORT).show();
    } else if (!imageWasChanged) {
      Toast.makeText(AddNewItemPage.this, "Missing picture", Toast.LENGTH_SHORT).show();
    } else {
      // saving data

      // initializing item to not active
      newItem.setActive(false);

      // getting item data from fields
      newItem.setTitle(title);
      newItem.setDescription(description);
      newItem.setUsername(userName);
      newItem.setImageId(itemId);
      newItem.setEmail(email);

      // converting bitmap to byte array
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
      imageReference = DB_IMAGES.getReference().child("users/" + email + "/" + itemId + ".jpg");
      byte[] imageData = baos.toByteArray();

      // storing byte array in Firebase Storage
      imageReference
          .putBytes(imageData)
          .addOnSuccessListener(taskSnapshot -> {})
          .addOnFailureListener(
              e ->
                  Toast.makeText(AddNewItemPage.this, "Failed to upload photo", Toast.LENGTH_SHORT)
                      .show());

      // creating user document
      // firebase variables
      DocumentReference userDocumentReference = DB_USER.collection("users").document(email);
      Map<String, Object> user_name_to_store = new HashMap<>();
      user_name_to_store.put(USER_NAME, userName);
      userDocumentReference.set(user_name_to_store);

      // creating item document
      DocumentReference itemDocumentReference =
          DB_ITEM
              .collection("users")
              .document(email)
              .collection("items")
              .document(newItem.getImageId());

      // saving item to new item document
      itemDocumentReference
          .set(newItem)
          .addOnSuccessListener(
              unused -> {
                Toast.makeText(AddNewItemPage.this, "Added item", Toast.LENGTH_SHORT).show();

                // causing UserItemsPage to refresh if save button is pressed
                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
              })
          .addOnFailureListener(
              e ->
                  Toast.makeText(AddNewItemPage.this, "Failed to add item", Toast.LENGTH_SHORT)
                      .show());
    }
  }

  /**
   * this function generates and shows a custom dialog to give the user the choice between taking a
   * picture and choosing a picture from the gallery
   */
  protected void showCustomDialog() {
    // creating a dialog box
    Dialog dialog = new Dialog(AddNewItemPage.this);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setCancelable(true);
    dialog.setContentView(R.layout.activity_get_image_dialog_box);
    if (dialog.getWindow() != null)
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    // getting buttons from UI
    Button choose_photo = dialog.findViewById(R.id.choose_photo_button);
    Button take_photo = dialog.findViewById(R.id.take_photo_button);

    // setting up on click listener
    take_photo.setOnClickListener(
        v -> {
          // sending intent to get get picture
          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          startActivityForResult(intent, CAMERA_REQUEST);
          dialog.dismiss();
        });

    // setting up on click listener for picking image
    choose_photo.setOnClickListener(
        v -> {
          // sending intent to get picture from gallery
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setType("image/*");
          startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
          dialog.dismiss();
        });

    // launching the dialog box
    dialog.show();
  }

  /** this function handles the results from the camera intent and the gallery intent */
  @Override
  protected void onActivityResult(int request_code, int result_code, Intent data) {
    super.onActivityResult(request_code, result_code, data);
    if (request_code == CAMERA_REQUEST && result_code == RESULT_OK) {
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
    } else if (request_code == GALLERY_REQUEST && result_code == RESULT_OK) {
      // getting data from gallery
      photoUri = data.getData();

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
}
