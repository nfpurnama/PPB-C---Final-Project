package com.example.finalproject.ui.scan;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentScanBinding;

public class ScanFragment extends Fragment {
    private FragmentScanBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.openCameraButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_scanFragment_to_cameraFragment);
        });

        binding.uploadImageButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_scanFragment_to_uploadFragment);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}