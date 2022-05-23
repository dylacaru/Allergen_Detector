package com.example.allergendetector;

import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ScannerActivity extends AppCompatActivity {

    ImageView ivCapturedImg;
    TextView tvDetectedText;
    Bitmap imageBitmap;


    public static List<FoodAllergy> FOOD_ALLERGY_LIST = new ArrayList();

    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        tvDetectedText = findViewById(R.id.tvDetectedText);
        ivCapturedImg = findViewById(R.id.ivCapturedImg);
        readAllergenData();

        captureImage();

    }

    //read from file
    private void readAllergenData() {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

        String line = "";
        try {
            //Step over headers
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");

                FoodAllergy allergy = new FoodAllergy();
                allergy.setFood(tokens[0]);
                allergy.setAllergy(tokens[1]);
                FOOD_ALLERGY_LIST.add(allergy);
            }
        } catch (IOException e) {
            Log.wtf("ScannerActivity", "Error reading file on line " + line, e);
            e.printStackTrace();
        }
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        int PERMISSION_CODE = 200;
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PERMISSION_CODE);
    }

    private void captureImage() {
//        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

//        if (takePicture.resolveActivity(getPackageManager()) != null) {

        CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).start(ScannerActivity.this);


//          startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraPermission) {
                Toast.makeText(this, "Permissions Granted", Toast.LENGTH_SHORT).show();
                captureImage();
            } else {
                Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

//            Bundle extras = data.getExtras();
//            imageBitmap = (Bitmap) extras.get("data");
            ivCapturedImg.setImageBitmap(imageBitmap);
            detectText();
        }
    }

    private String checkForAllergens(String text) {

        List<String> detectedAllergen = new ArrayList<>();

        for (int i = 0; i < FOOD_ALLERGY_LIST.size(); i++) {
            String allergen = FOOD_ALLERGY_LIST.get(i).getFood();
            allergen = allergen.toLowerCase(Locale.ROOT);
            text = text.toLowerCase(Locale.ROOT);
            if (text.contains(allergen)) {

                detectedAllergen.add(allergen);
                text = text.replaceAll(allergen, "<font color='red'>" + allergen + "</font>");

                Set<String> set = new HashSet<>(detectedAllergen);
                detectedAllergen.clear();
                detectedAllergen.addAll(set);

                tvDetectedText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ScannerActivity.this, AllergenDetailsActivity.class);
                        intent.putStringArrayListExtra("allergen", (ArrayList<String>) detectedAllergen);
                        startActivity(intent);
                    }
                });
            }
        }
        return text;
    }

    private void detectText() {
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result = recognizer.process(image).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                StringBuilder result = new StringBuilder();
                for (Text.TextBlock block : text.getTextBlocks()) {
                    String blockText = block.getText();
                    for (Text.Line line : block.getLines()) {
                        for (Text.Element element : line.getElements()) {
                            String elementText = element.getText();
                            result.append(elementText);
                        }
                        tvDetectedText.setText(Html.fromHtml(checkForAllergens(blockText)));
                        tvDetectedText.setMovementMethod(new ScrollingMovementMethod());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScannerActivity.this, "Failed to detect Ingredients from Image" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}