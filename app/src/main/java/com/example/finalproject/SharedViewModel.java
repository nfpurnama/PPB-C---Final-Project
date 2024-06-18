package com.example.finalproject;

import android.graphics.Path;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private boolean dialogState;

    private Drawable recognitionImage;
    private String recognitionText;

    private String drawText;
    private Path drawPath;

    public boolean getDialogState() {
        return dialogState;
    }

    public void setDialogState(boolean dialogState) {
        this.dialogState = dialogState;
    }

    public Drawable getRecognitionImage() {
        return recognitionImage;
    }

    public void setRecognitionImage(Drawable recognitionImage) {
        this.recognitionImage = recognitionImage;
    }

    public String getRecognitionText() {
        return recognitionText;
    }

    public void setRecognitionText(String recognitionText) {
        this.recognitionText = recognitionText;
    }

    public String getDrawText() {
        return drawText;
    }

    public void setDrawText(String drawText) {
        this.drawText = drawText;
    }

    public Path getDrawPath() {
        return drawPath;
    }

    public void setDrawPath(Path drawPath) {
        this.drawPath = drawPath;
    }
}