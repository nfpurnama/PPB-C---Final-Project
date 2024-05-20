package com.example.finalproject.ui.scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.finalproject.R;

public class CameraFragment extends Fragment {
    private ImageView previewImageView;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        previewImageView = view.findViewById(R.id.previewImageView);

        view.findViewById(R.id.captureButton).setOnClickListener(v -> openCamera());

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleCameraResult
        );

        return view;
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(cameraIntent);
        } else {
            Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCameraResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getExtras() != null) {
            Bundle extras = result.getData().getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            previewImageView.setImageBitmap(imageBitmap);
            // klasifikasi gambar di sini
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }
}