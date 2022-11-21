package com.nomanforhad.finalproject.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nomanforhad.finalproject.R;

import java.util.Arrays;

public class InputPhoneNumberActivity extends AppCompatActivity {

    private AutoCompleteTextView mEtPhoneNumber;
    private EditText mCountryCode;
    private EditText mEtCountryName;

    private String mPhoneNumber;
    private static final int COUNTRY_REQUEST = 1;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone_number);

        Toolbar mToolbar = findViewById(R.id.toolbar_input_number);
        mEtPhoneNumber = findViewById(R.id.et_phone_number);
        mCountryCode = findViewById(R.id.et_country_code);
        Button mNext = findViewById(R.id.bt_next_input_number);
        mEtCountryName = findViewById(R.id.et_country_name);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mEtPhoneNumber.requestFocus();
        mEtPhoneNumber.setAdapter(new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item,
                Arrays.asList(
                        "1710000001",
                        "1710000002",
                        "1710000003",
                        "1710000004",
                        "1710000005",
                        "1710000006",
                        "1710000007",
                        "1710000007"
                )));
        mEtPhoneNumber.setThreshold(1);

        mNext.setOnClickListener(view -> {
            mPhoneNumber = "+" + mCountryCode.getText().toString() + mEtPhoneNumber.getText().toString();
            if (mEtPhoneNumber.getText().toString().length() < 9) {
                new AlertDialog.Builder(InputPhoneNumberActivity.this)
                        .setMessage("The phone number you entered is too short for the country: "
                                + mEtCountryName.getText().toString()
                                + "\n\nInclude your area code if you haven't.")
                        .setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            } else {
                showProgressDialog();
            }
        });

        mProgressDialog = new ProgressDialog(this);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(InputPhoneNumberActivity.this);
        builder.setMessage("We will be verifying the phone number:" + "\n\n" + mPhoneNumber + "\n\nIs this OK, or would you like to edit the number?")
                .setPositiveButton("OK", (dialogInterface, i) -> {
                    Intent intent = new Intent(InputPhoneNumberActivity.this, PhoneVerifyActivity.class);
                    intent.putExtra("phonenumber", mPhoneNumber);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Edit", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input_phone_number, menu);
        return true;
    }

    private void showProgressDialog() {
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Connecting");
        mProgressDialog.show();

        new Thread(() -> {
            int loading = 0;
            while (loading < 100) {
                try {
                    Thread.sleep(150);
                    loading += 20;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            mProgressDialog.dismiss();
            InputPhoneNumberActivity.this.runOnUiThread(() -> showDialog());
        }).start();
    }

}
