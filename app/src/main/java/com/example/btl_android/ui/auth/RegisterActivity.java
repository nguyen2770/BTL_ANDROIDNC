package com.example.btl_android.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.btl_android.R;
import com.example.btl_android.data.model.User;
import com.example.btl_android.ui.main.MainActivity;
import com.example.btl_android.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtName, edtPhone, edtEmail, edtAddress, edtPassword;
    private RadioGroup roleGroup;
    private Button btnRegister;
    private TextView tvToLogin;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        roleGroup = findViewById(R.id.radioGroupRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvToLogin = findViewById(R.id.tvBackToLogin);

        btnRegister.setOnClickListener(v -> handleRegister());

        observeViewModel();

        tvToLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void handleRegister() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        int selectedId = roleGroup.getCheckedRadioButtonId();

        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập họ tên");
            edtName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        } else if (!phone.matches("^0[0-9]{9}$")) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            edtPhone.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }


        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        } else if (password.length() < 6) {
            edtPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (selectedId == -1) {
            Toast.makeText(this, "Vui lòng chọn vai trò", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.registerWithEmail(email, password);
    }

    private void observeViewModel() {
        // Thành công: lưu thông tin vào Firestore và chuyển màn hình
        authViewModel.getUser().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                RadioButton selectedRadioButton = findViewById(roleGroup.getCheckedRadioButtonId());
                String role = selectedRadioButton.getTag().toString();

                User newUser = new User(
                        edtName.getText().toString().trim(),
                        edtPhone.getText().toString().trim(),
                        edtEmail.getText().toString().trim(),
                        role
                );
                authViewModel.saveUserToFirestore(newUser);

                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        });

        // Thất bại: hiển thị lỗi
        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();

            }
        });
    }
}
