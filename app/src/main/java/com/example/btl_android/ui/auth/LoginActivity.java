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
import com.example.btl_android.ui.main.MainActivity;
import com.example.btl_android.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvToRegister;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvToRegister = findViewById(R.id.toRegister);

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty()) {
                edtEmail.setError("Vui lòng nhập email");
                return;
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                return;
            }

            if (password.isEmpty()) {
                edtPassword.setError("Vui lòng nhập mật khẩu");
                return;
            }

            if (password.length() < 6) {
                edtPassword.setError("Mật khẩu phải từ 6 ký tự");
                return;
            }

            // Gọi hàm đăng nhập từ ViewModel
            authViewModel.loginWithEmail(email, password);
        });

        tvForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });

        // Quan sát đối tượng FirebaseUser sau khi đăng nhập thành công
        authViewModel.getUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                authViewModel.fetchUserData(uid); // Lấy dữ liệu User từ Firestore
            }
        });

        // Quan sát dữ liệu User lấy từ Firestore
        authViewModel.getUserData().observe(this, user -> {
            if (user != null) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("role", user.getRole());
                startActivity(intent);
                finish(); // Kết thúc LoginActivity
            }
        });

        // Quan sát thông báo lỗi từ ViewModel
        authViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        tvToRegister.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
