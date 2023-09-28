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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;

public class AddNewItem extends AppCompatActivity {

  private static final int CAMERA_REQUEST = 1000;
  private static final int PICK_IMAGE = 1001;

  Dialog dialog;

  ShapeableImageView item_image_view;
  String user_id;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_new_item);
    dialog = new Dialog(AddNewItem.this);

    // getting item id from recycler view
    user_id = getIntent().getStringExtra("item_id");

    item_image_view = findViewById(R.id.item_image_view);

    item_image_view.setOnClickListener(
        view -> {
          dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
          dialog.setCancelable(true);
          dialog.setContentView(R.layout.activity_get_image_dialog_box);
          dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

          Button choose_photo = dialog.findViewById(R.id.choose_photo_button);
          Button take_photo = dialog.findViewById(R.id.take_photo_button);

          take_photo.setOnClickListener(
              v -> {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
                dialog.dismiss();
              });

          choose_photo.setOnClickListener(
              v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                dialog.dismiss();
              });

          dialog.show();
        });

    //

  }

  @Override
  protected void onActivityResult(int request_code, int result_code, Intent data) {
    super.onActivityResult(request_code, result_code, data);

    if (request_code == CAMERA_REQUEST && request_code == RESULT_OK) {
      Bitmap photo = (Bitmap) data.getExtras().get("data");
      item_image_view.setImageBitmap(photo);
    } else if (request_code == PICK_IMAGE && result_code == RESULT_OK) {
      Uri photo = data.getData();
      if (photo != null) {
        item_image_view.setImageURI(photo);
      }
    }
  }
}
