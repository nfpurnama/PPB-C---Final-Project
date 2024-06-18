package com.example.finalproject;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class HijaiyahRecognizer {
    private static Interpreter interpreter;
    private static List<String> labels;

    public static void init(Context context) {
        try {
            MappedByteBuffer tfliteModel = loadModelFile(context, "model.tflite");
            interpreter = new Interpreter(tfliteModel);
            labels = getHijaiyahLabels(context, "class_names.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private static List<String> getHijaiyahLabels(Context context, String txtFileName) throws IOException {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open(txtFileName)));
        String line;
        while ((line = reader.readLine()) != null) {
            labels.add(line);
        }
        reader.close();
        return labels;
    }

    public static String predictHijaiyah(Bitmap bitmap) {
        Bitmap preprocessedBitmap = preprocessImage(bitmap);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(preprocessedBitmap);
        float[][] output = new float[1][labels.size()];

        interpreter.run(inputBuffer, output);

        int maxIndex = -1;
        float maxScore = -Float.MAX_VALUE;
        for (int i = 0; i < output[0].length; i++) {
            if (output[0][i] > maxScore) {
                maxScore = output[0][i];
                maxIndex = i;
            }
        }
        return labels.get(maxIndex);
    }

    private static Bitmap preprocessImage(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 32, 32, true);
        Bitmap grayBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
        for (int i = 0; i < 32; i++) {
            for (int j = 0; j < 32; j++) {
                int pixel = resizedBitmap.getPixel(i, j);
                int gray = (Color.red(pixel) + Color.green(pixel) + Color.blue(pixel)) / 3;
                grayBitmap.setPixel(i, j, Color.rgb(gray, gray, gray));
            }
        }
//        Bitmap invertedBitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888);
//        for (int i = 0; i < 32; i++) {
//            for (int j = 0; j < 32; j++) {
//                int pixel = grayBitmap.getPixel(i, j);
//                int invertedGray = 255 - Color.red(pixel);
//                invertedBitmap.setPixel(i, j, Color.rgb(invertedGray, invertedGray, invertedGray));
//            }
//        }
//        return invertedBitmap;
        return grayBitmap;
    }

    private static ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 32 * 32 * 1);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[32 * 32];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < 32; ++i) {
            for (int j = 0; j < 32; ++j) {
                final int val = intValues[pixel++];
                byteBuffer.putFloat((val & 0xFF) / 255.0f);
            }
        }
        return byteBuffer;
    }
}