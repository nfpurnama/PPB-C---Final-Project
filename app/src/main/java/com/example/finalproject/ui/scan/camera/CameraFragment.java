package com.example.finalproject.ui.scan.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.example.finalproject.ui.scan.ApiService;
import com.example.finalproject.ui.scan.ImageRequest;
import com.example.finalproject.ui.scan.ImageResponse;

import java.io.ByteArrayOutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

            if (imageBitmap != null) {
                String base64Image = encodeImageToBase64(imageBitmap);
                sendImageToServer(base64Image);
            } else {
                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void sendImageToServer(String base64Image) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://server-ip:5000/") // Ganti dengan URL server Flask
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        ImageRequest imageRequest = new ImageRequest(base64Image);
        Call<ImageResponse> call = apiService.predictBatik(imageRequest);
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String prediction = response.body().getPrediction();
                    Toast.makeText(requireContext(), "Prediction: " + prediction, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(requireContext(), "Failed to get prediction", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
