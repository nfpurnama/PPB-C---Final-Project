package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class DrawFragment extends Fragment {

    private DrawView drawView;
    private TextView predictionTextView;
    private Button eraseButton;
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_draw, container, false);

        drawView = view.findViewById(R.id.drawView);
        predictionTextView = view.findViewById(R.id.textView);
        eraseButton = view.findViewById(R.id.eraseBtn);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        eraseButton.setOnClickListener(v -> {
            drawView.erase();
            sharedViewModel.setDrawText(getString(R.string.unknown_letter));
            predictionTextView.setText(sharedViewModel.getDrawText());
        });

        view.setFocusableInTouchMode(true);
        view.requestFocus();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePredictionText();
    }

    private void updatePredictionText() {
        String prediction = HijaiyahRecognizer.predictHijaiyah(drawView.save());
        sharedViewModel.setDrawText(prediction);
        predictionTextView.setText(sharedViewModel.getDrawText());
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedViewModel.setDrawPath(drawView.getPath());
    }
}