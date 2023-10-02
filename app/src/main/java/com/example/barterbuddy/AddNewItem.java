package com.example.barterbuddy;


import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddNewItem extends AppCompatActivity {

    // request codes to allow result handler to find correct result
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;

    // names of collections documents and fields
    private static final String USER_NAME = "username";
    private static final String ITEM_TITLE = "title";
    private static final String ITEM_DESCRIPTION = "description";
    private static final String ITEM_ACTIVE_STATUS = "isActive";
    private static final String ITEM_IMAGE = "photo";

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
    Bitmap photo_bitmap;
    Uri photo_uri;

    // firebase variables
    private DocumentReference userDocumentReference;
    private final FirebaseFirestore dbUser = FirebaseFirestore.getInstance();
    private DocumentReference itemDocumentReference;
    private final FirebaseFirestore dbItem = FirebaseFirestore.getInstance();

    /**
     * this function is called when the activity is first loaded
     */
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
        save_button.setOnClickListener(view -> saveItem());
    }

    /**
     * This function saves teh data from the data fields and saves them to the database
     */
    protected void saveItem() {
        Item new_item = new Item();

        title = String.valueOf(titleEditText.getText());
        description = String.valueOf(descriptionEditText.getText());

        // checking for empty fields
        if ((TextUtils.isEmpty(title) && TextUtils.isEmpty(description) && !imageWasChanged)
                || (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) || ((TextUtils.isEmpty(title) && !imageWasChanged))
                || ((TextUtils.isEmpty(description) && !imageWasChanged))) {
            Toast.makeText(AddNewItem.this, "Missing information", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(title)) {
            Toast.makeText(AddNewItem.this, "Missing title", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(AddNewItem.this, "Missing description", Toast.LENGTH_SHORT).show();
        } else if (!imageWasChanged) {
            Toast.makeText(AddNewItem.this, "Missing picture", Toast.LENGTH_SHORT).show();
        } else {
            // saving data

            // initializing item to not active
            new_item.setActive(false);

            // getting item data from fields
            new_item.setTitle(title);
            new_item.setDescription(description);
            new_item.setUserName(userName);

            // creating user document
            userDocumentReference = dbUser.collection("users").document(email);
            Map<String, Object> user_name_to_store = new HashMap<>();
            user_name_to_store.put(USER_NAME, userName);
            userDocumentReference.set(user_name_to_store);

            // creating item document and filling it with data
            itemDocumentReference = dbItem.collection("users").document(email).collection("items").document(email + "-" + title);
            Map<String, Object> data_to_store = new HashMap<>();
            data_to_store.put(ITEM_TITLE, new_item.getTitle());
            data_to_store.put(ITEM_DESCRIPTION, new_item.getDescription());
            data_to_store.put(ITEM_ACTIVE_STATUS, new_item.getActive());
            data_to_store.put(ITEM_IMAGE, photo_bitmap);
            itemDocumentReference.set(data_to_store).addOnSuccessListener(unused -> {
                Toast.makeText(AddNewItem.this, "Added item", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(AddNewItem.this, "Failed to add item", Toast.LENGTH_SHORT).show());
        }

    }

    /**
     * this function generates and shows a custom dialog to give the user the choice between taking a picture
     * and choosing a picture from the gallery
     */
    protected void showCustomDialog() {
        // creating a dialog box
        Dialog dialog = new Dialog(AddNewItem.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.activity_get_image_dialog_box);
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

    /**
     * this function handles the results from the camera intent and the gallery intent
     */
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data) {
        super.onActivityResult(request_code, result_code, data);
        if (request_code == CAMERA_REQUEST && result_code == RESULT_OK) {
            // getting bitmap from camera
            photo_bitmap = (Bitmap) data.getExtras().get("data");
            // setting image view scale type
            itemImageView.setScaleType(ShapeableImageView.ScaleType.CENTER_CROP);
            // setting image view
            itemImageView.setImageBitmap(photo_bitmap);
            imageWasChanged = true;
        } else if (request_code == GALLERY_REQUEST && result_code == RESULT_OK) {
            // getting data from gallery
            photo_uri = data.getData();

            // checking if uri is null
            if (photo_uri != null) {

                try {
                    // convert uri to bitmap
                    photo_bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photo_uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // setting image view
                itemImageView.setImageURI(photo_uri);
                imageWasChanged = true;
            }
        }
    }
}
