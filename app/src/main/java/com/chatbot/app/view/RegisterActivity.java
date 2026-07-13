package com.chatbot.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chatbot.app.ChatBotApp;
import com.chatbot.app.contract.RegisterContract;
import com.chatbot.app.databinding.ActivityRegisterBinding;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.presenter.RegisterPresenter;

public class RegisterActivity extends AppCompatActivity implements RegisterContract.View {

    private ActivityRegisterBinding binding;
    private RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseHelper db = ChatBotApp.getInstance().getDatabaseHelper();
        SessionManager sessionManager = ChatBotApp.getInstance().getSessionManager();
        presenter = new RegisterPresenter(this, new UserRepository(db), sessionManager);

        binding.registerButton.setOnClickListener(v ->
                presenter.register(
                        binding.nameInput.getText().toString(),
                        binding.emailInput.getText().toString(),
                        binding.passwordInput.getText().toString(),
                        binding.confirmPasswordInput.getText().toString()
                ));

        binding.loginLink.setOnClickListener(v -> finish());
    }

    @Override
    public void showLoading(boolean loading) {
        binding.registerProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.registerButton.setEnabled(!loading);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }
}
