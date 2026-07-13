package com.chatbot.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.chatbot.app.ChatBotApp;
import com.chatbot.app.R;
import com.chatbot.app.contract.SettingsContract;
import com.chatbot.app.databinding.ActivitySettingsBinding;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.ThemeManager;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.model.repo.UserRepository;
import com.chatbot.app.presenter.SettingsPresenter;
import com.google.android.material.textfield.TextInputEditText;

public class SettingsActivity extends AppCompatActivity implements SettingsContract.View {

    private ActivitySettingsBinding binding;
    private SettingsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.settingsToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings);
        }

        DatabaseHelper db = ChatBotApp.getInstance().getDatabaseHelper();
        SessionManager sessionManager = ChatBotApp.getInstance().getSessionManager();
        ThemeManager themeManager = ChatBotApp.getInstance().getThemeManager();

        presenter = new SettingsPresenter(
                this,
                new UserRepository(db),
                new ChatRepository(db),
                sessionManager,
                themeManager
        );

        String[] models = {"qwen-turbo", "qwen-plus", "qwen-max"};
        binding.modelSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, models));

        binding.saveProfileButton.setOnClickListener(v ->
                presenter.saveDisplayName(binding.displayNameInput.getText().toString()));

        binding.saveModelButton.setOnClickListener(v ->
                presenter.saveModel(binding.modelSpinner.getSelectedItem().toString()));

        binding.themeToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == R.id.themeLight) {
                presenter.saveTheme(ThemeManager.THEME_LIGHT);
            } else if (checkedId == R.id.themeSystem) {
                presenter.saveTheme(ThemeManager.THEME_SYSTEM);
            }
        });

        binding.logoutButton.setOnClickListener(v -> presenter.logout());

        binding.clearChatsButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.clear_all_chats)
                        .setMessage(R.string.clear_all_chats_message)
                        .setPositiveButton(R.string.clear, (d, w) -> presenter.clearAllChats())
                        .setNegativeButton(R.string.cancel, null)
                        .show());

        binding.deleteAccountButton.setOnClickListener(v -> showDeleteAccountDialog());

        presenter.loadSettings();
    }

    private void showDeleteAccountDialog() {
        TextInputEditText passwordInput = new TextInputEditText(this);
        passwordInput.setHint(R.string.password);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        passwordInput.setPadding(padding, padding, padding, padding);

        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_message)
                .setView(passwordInput)
                .setPositiveButton(R.string.delete, (d, w) ->
                        presenter.deleteAccount(passwordInput.getText().toString()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showProfile(String displayName, String email, String model) {
        binding.displayNameInput.setText(displayName);
        binding.emailText.setText(email);

        String[] models = {"qwen-turbo", "qwen-plus", "qwen-max"};
        for (int i = 0; i < models.length; i++) {
            if (models[i].equals(model)) {
                binding.modelSpinner.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void showTheme(int themeMode) {
        if (themeMode == ThemeManager.THEME_LIGHT) {
            binding.themeToggleGroup.check(R.id.themeLight);
        } else {
            binding.themeToggleGroup.check(R.id.themeSystem);
        }
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }
}