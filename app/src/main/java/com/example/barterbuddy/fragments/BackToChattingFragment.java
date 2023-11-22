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
import com.example.barterbuddy.activities.ChatPage;
import com.example.barterbuddy.models.User;

public class BackToChattingFragment extends Fragment {
  private Button back_to_chatting;

  BackToChattingFragment() {
    super(R.layout.back_to_chatting_page);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.back_to_chatting_page, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getXmlElements();

    back_to_chatting.setOnClickListener(
        v -> {
          Intent intent = new Intent(requireContext(), ChatPage.class);
          User otherUser = new User();
          intent.putExtra("otherUser", otherUser);
          startActivity(intent);
        });
  }

  private void getXmlElements() {
    back_to_chatting = getActivity().findViewById(R.id.back_to_chatting);
  }
}
