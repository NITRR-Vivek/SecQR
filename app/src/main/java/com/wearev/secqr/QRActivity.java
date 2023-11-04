package com.wearev.secqr;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import java.io.ByteArrayOutputStream;
import java.util.regex.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class QRActivity extends AppCompatActivity {

    private EditText addressText, modelNoText, registrationNumberEditText, ownerNameEditText, contactMobileEditText, contactEmailEditText;
    private EditText edtVehicleClass, regState, modelYear, fuelType, engineNo, chassisNo, edtFather, ownerCategory, edtDL, edtPurchase;
    private EditText edtINSCompany, edtINSFrom, edtINSUpto, edtINSPolicyNo, edtFinancier;
    private TextView hiddenQRTextView;
    private ImageView qrCodeImageView;
    private ImageView btnShareQR;
    private ProgressBar loader;
    private Bitmap qrCodeBitmap;
    private Dialog customDialog;
    byte[] QRCodeByte;
    
    private DatabaseHelper databaseHelper;

    ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            saveQRCode();
        } else {
            Toast.makeText(this, "Storage Permission Required!", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qractivity);

        Objects.requireNonNull(getSupportActionBar()).setTitle("QR Generate ");
        getSupportActionBar().setSubtitle("Generate QR for your Vehicle !");

        modelNoText = findViewById(R.id.modelNoEditText);
        registrationNumberEditText = findViewById(R.id.registrationNumberEditText);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        contactMobileEditText = findViewById(R.id.contactNumberEditText);
        contactEmailEditText = findViewById(R.id.contactEmailEditText);
        addressText = findViewById(R.id.addressEditText);

        edtVehicleClass =findViewById(R.id.edtVehicleClass);
        regState = findViewById(R.id.edtRegState);
        modelYear = findViewById(R.id.edtModelYear);
        fuelType = findViewById(R.id.edtFuelType);
        engineNo = findViewById(R.id.edtEngineNo);
        chassisNo = findViewById(R.id.edtChassisNo);
        edtFather = findViewById(R.id.ownerFatherEditText);
        ownerCategory = findViewById(R.id.ownerCategory);
        edtDL = findViewById(R.id.edtDLNo);
        edtPurchase = findViewById(R.id.purchaseEditText);
        edtINSCompany = findViewById(R.id.edtINSCompany);
        edtINSFrom = findViewById(R.id.edtINSFrom);
        edtINSUpto = findViewById(R.id.edtINSUpto);
        edtINSPolicyNo = findViewById(R.id.edtINSPolicyNo);
        edtFinancier = findViewById(R.id.edtFinancer);
        loader = findViewById(R.id.qrLoader);
        btnShareQR = findViewById(R.id.btnShareQR);
        ImageView btnSaveQR = findViewById(R.id.btnSaveQR);

        hiddenQRTextView = findViewById(R.id.hiddenQRTextView);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);
        Button generateQRButton = findViewById(R.id.generateQRButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        generateQRButton.setOnClickListener(v -> {
            hiddenQRTextView.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);


            String modelNo = modelNoText.getText().toString();
            String registrationNumber = registrationNumberEditText.getText().toString();
            String ownerName = ownerNameEditText.getText().toString();
            String mobileNumber = contactMobileEditText.getText().toString();
            String email = contactEmailEditText.getText().toString();
            String address = addressText.getText().toString();

            String VClass = edtVehicleClass.getText().toString();
            String state = regState.getText().toString();
            String year = modelYear.getText().toString();
            String fuel = fuelType.getText().toString();
            String engine = engineNo.getText().toString();
            String chassis = chassisNo.getText().toString();
            String father = edtFather.getText().toString();
            String oCategory = ownerCategory.getText().toString();
            String dl = edtDL.getText().toString();
            String purchase = edtPurchase.getText().toString();
            String INSCompany = edtINSCompany.getText().toString();
            String INSFrom = edtINSFrom.getText().toString();
            String INSUpto = edtINSUpto.getText().toString();
            String policyNo = edtINSPolicyNo.getText().toString();
            String financier = edtFinancier.getText().toString();

            String inputData3 = "Registration No: "+ registrationNumber+"\nState: "+state+"\nModal No: " + modelNo  + "\nModel Year: "+ year  +"\nVehicle Class: "+VClass+"\nFuel: "+fuel;
            String inputData2 = "Owner's Name: " + ownerName + "\nDriving Licence No: " +dl+"\nEmail: " + email + "\nInsurance Company: "+INSCompany+"\nInsurance From: "+INSFrom+"\nInsurance Upto:"+INSUpto+"\nPolicy No: "+policyNo;
            String inputData1 = "\nFather: "+father+"\nMobile No: "+mobileNumber+"\nAddress: " + address+"\nOwner Category: "+oCategory+"\nEngine No: "+engine+"\nChassis No: "+chassis+"\nPurchase: "+purchase+"\nFinancier: "+financier;


            // Encrypt the Data and then Sending it to QR
            try {
                View view = this.getCurrentFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                String encryptedText1 = EncryptDecrypt.encrypt(inputData1);
                String encryptedText2 = EncryptDecrypt.encrypt(inputData2);
                try {
                    qrCodeBitmap = generateQRCode(inputData3 + "\n$$" + encryptedText2+"$$"+encryptedText1);
                    new Handler().postDelayed(() -> {

                        qrCodeImageView.setImageBitmap(qrCodeBitmap);
                        showCustomDialog(qrCodeBitmap);
                        qrCodeImageView.setVisibility(View.VISIBLE);
                        btnShareQR.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                    }, 1000);
                } catch (WriterException e) {
                    e.printStackTrace();
                    Toast.makeText(QRActivity.this, "Error generating QR code", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            QRCodeByte = stream.toByteArray();
            DBDataModel vdModel = new DBDataModel();
            vdModel.setRegNo(registrationNumber);
            vdModel.setVehicleClass(VClass);
            vdModel.setQRBytes(QRCodeByte);
            long newRowId = databaseHelper.insertData(vdModel);

            if (newRowId != -1) {
                Toast.makeText(this, "Data saved in the History", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error saving data to the History", Toast.LENGTH_SHORT).show();
            }
        });

        btnShareQR.setOnClickListener(view -> shareQR(qrCodeBitmap));
        btnSaveQR.setOnClickListener(view->{
            if (ContextCompat.checkSelfPermission(this,  WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch( WRITE_EXTERNAL_STORAGE);
                if(isStoragePermissionGranted()){
                    saveQRCode();
                }else{
                    Toast.makeText(this, "Storage Permission Required !", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void shareQR(Bitmap qrCodeBitmap) {
        // Saving the QR code bitmap as a PNG image in the app's cache directory
        File cachePath = new File(getCacheDir(), "images");
        cachePath.mkdirs();
        File qrCodeFile = new File(cachePath, "qr_code.png");

        try (FileOutputStream outputStream = new FileOutputStream(qrCodeFile)) {
            qrCodeBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(QRActivity.this, "Error saving QR code image", Toast.LENGTH_SHORT).show();
        }

        Uri imageUri = FileProvider.getUriForFile(QRActivity.this, "com.wearev.secqr.fileprovider", qrCodeFile);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
    }

    public static boolean isValidDLLicenseNo(String str)
    {
        String regex = "^(([A-Z]{2}[0-9]{2})"
                + "( )|([A-Z]{2}-[0-9]"
                + "{2}))((19|20)[0-9]"
                + "[0-9])[0-9]{7}$";

        Pattern p = Pattern.compile(regex);
        if (str == null) {
            return false;
        }
        Matcher m = p.matcher(str);
        return m.matches();
    }
    // saving the QR code to the gallery
    private void saveQRCode() {

        String imageFileName = "QRCode_" + System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures");

        try {
            if (qrCodeBitmap != null) {
                ContentResolver contentResolver = getContentResolver();
                MediaStore.Images.Media.insertImage(contentResolver, qrCodeBitmap, imageFileName, "QR Code image");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "QR code saved to Gallery", Toast.LENGTH_SHORT).show();
    }

    private boolean isStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(this,  WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private Bitmap generateQRCode(String data) throws WriterException {
        int qrCodeSize = 512;
        int borderSize = 7;
        int borderRadius = 7;

        int teal700 = 0xFF018786;
        int backgroundColor = Color.WHITE;

        BitMatrix bitMatrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(qrBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(backgroundColor);
        canvas.drawRoundRect(0, 0, width, height, borderRadius, borderRadius, paint);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    qrBitmap.setPixel(x, y, teal700);
                }
            }
        }

        Bitmap borderBitmap = Bitmap.createBitmap(qrCodeSize + 2 * borderSize, qrCodeSize + 2 * borderSize, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(borderBitmap);
        paint.setColor(teal700);
        canvas.drawRoundRect(0, 0, borderBitmap.getWidth(), borderBitmap.getHeight(), borderRadius, borderRadius, paint);
        canvas.drawBitmap(qrBitmap, borderSize, borderSize, null);

        return borderBitmap;
    }

    private Bitmap insertIconIntoQRCode(Bitmap qrCodeBitmap ) {

        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lock2);

        int centerX = (qrCodeBitmap.getWidth() - iconBitmap.getWidth()) / 2;
        int centerY = (qrCodeBitmap.getHeight() - iconBitmap.getHeight()) / 2;

        Bitmap combinedBitmap = Bitmap.createBitmap(qrCodeBitmap.getWidth(), qrCodeBitmap.getHeight(), qrCodeBitmap.getConfig());
        android.graphics.Canvas canvas = new android.graphics.Canvas(combinedBitmap);
        canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
        canvas.drawBitmap(iconBitmap, centerX, centerY, null);

        return combinedBitmap;
    }
    private void showCustomDialog(Bitmap qrImage) {
        customDialog = new Dialog(this);
        customDialog.setContentView(R.layout.custom_dialogue_layout);
        ImageView qrCodeImageView = customDialog.findViewById(R.id.qrCodeImageView2);
        ImageView btnShare = customDialog.findViewById(R.id.btnShareQR2);
        Button buttonCall112 = customDialog.findViewById(R.id.buttonCall112);
        Button buttonCancel = customDialog.findViewById(R.id.buttonCancel);

        qrCodeImageView.setImageBitmap(qrImage);
        btnShare.setOnClickListener(v-> shareQR(qrImage));

        buttonCall112.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:112"));
            startActivity(intent);
        });

        buttonCancel.setOnClickListener(v -> customDialog.dismiss());
        customDialog.show();
    }
 }
