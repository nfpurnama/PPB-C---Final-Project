package com.example.finalproject.ui.scan.upload;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadFragment extends Fragment {
    private ImageView previewImageView;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        previewImageView = view.findViewById(R.id.previewImageView);

        view.findViewById(R.id.uploadButton).setOnClickListener(v -> openFileChooser());

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleGalleryResult
        );

        return view;
    }

    private void openFileChooser() {
        Intent intent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        } else {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
        }
        galleryLauncher.launch(intent);
    }

    private void handleGalleryResult(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Uri imageUri = result.getData().getData();
            previewImageView.setImageURI(imageUri);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                String base64Image = encodeImageToBase64(bitmap);
                sendImageToServer(base64Image);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show();
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
