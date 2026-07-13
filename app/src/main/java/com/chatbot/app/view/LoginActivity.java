package com.chatbot.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chatbot.app.ChatBotApp;
import com.chatbot.app.R;
import com.chatbot.app.contract.LoginContract;
import com.chatbot.app.databinding.ActivityLoginBinding;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.presenter.LoginPresenter;

public class LoginActivity extends AppCompatActivity implements LoginContract.View {

    private ActivityLoginBinding binding;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DatabaseHelper db = ChatBotApp.getInstance().getDatabaseHelper();
        SessionManager sessionManager = ChatBotApp.getInstance().getSessionManager();
        presenter = new LoginPresenter(this, new UserRepository(db), sessionManager);

        binding.loginButton.setOnClickListener(v ->
                presenter.login(
                        binding.emailInput.getText().toString(),
                        binding.passwordInput.getText().toString()
                ));

        binding.registerLink.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    @Override
    public void showLoading(boolean loading) {
        binding.loginProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.loginButton.setEnabled(!loading);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSuccess() {
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
