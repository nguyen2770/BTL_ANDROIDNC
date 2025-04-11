package com.example.btl_android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.example.btl_android.R;
import com.example.btl_android.viewmodel.AuthViewModel;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText edtEmail;
    private Button btnSendReset;
    private AuthViewModel authViewModel;
    private TextView tvToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        getSupportActionBar().hide();


        edtEmail = findViewById(R.id.edtEmailReset);
        btnSendReset = findViewById(R.id.btnSendReset);
        tvToLogin = findViewById(R.id.tvBackToLogin);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnSendReset.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }
            authViewModel.sendPasswordResetEmail(email);
        });

        authViewModel.getResetSuccess().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Không thể gửi email. Vui lòng kiểm tra lại!", Toast.LENGTH_SHORT).show();
            }
        });

        tvToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}