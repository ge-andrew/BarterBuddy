package com.example.barterbuddy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.barterbuddy.R;
import com.example.barterbuddy.activities.BarterPage;

public class BackToBarteringFragment extends Fragment {
  private Button back_to_bartering;

  BackToBarteringFragment() {
    super(R.layout.back_to_bartering_page);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.back_to_bartering_page, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getXmlElements();

    back_to_bartering.setOnClickListener(
            v -> {
              Intent intent = new Intent(requireContext(), BarterPage.class);
              startActivity(intent);
            });
  }

  private void getXmlElements() {
    back_to_bartering = getActivity().findViewById(R.id.back_to_bartering);
  }
}
