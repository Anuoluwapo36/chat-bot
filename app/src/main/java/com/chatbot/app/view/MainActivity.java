package com.chatbot.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chatbot.app.ChatBotApp;
import com.chatbot.app.R;
import com.chatbot.app.adapter.ConversationAdapter;
import com.chatbot.app.contract.MainContract;
import com.chatbot.app.databinding.ActivityMainBinding;
import com.chatbot.app.model.SessionManager;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.entity.Conversation;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.presenter.MainPresenter;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ActivityMainBinding binding;
    private MainPresenter presenter;
    private ConversationAdapter adapter;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        drawerToggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        SessionManager sessionManager = ChatBotApp.getInstance().getSessionManager();
        DatabaseHelper db = ChatBotApp.getInstance().getDatabaseHelper();
        presenter = new MainPresenter(this, new ChatRepository(db), sessionManager);

        adapter = new ConversationAdapter(new ConversationAdapter.OnConversationClickListener() {
            @Override
            public void onConversationClick(Conversation conversation) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                presenter.openConversation(conversation.getId());
            }

            @Override
            public void onConversationLongClick(Conversation conversation) {
                showConversationOptions(conversation);
            }
        });

        binding.conversationRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.conversationRecycler.setAdapter(adapter);

        binding.newChatFab.setOnClickListener(v -> presenter.createNewConversation());
        binding.startChatButton.setOnClickListener(v -> presenter.createNewConversation());
        binding.drawerNewChat.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            presenter.createNewConversation();
        });
        binding.drawerSettings.setOnClickListener(v -> openSettings());

        View header = findViewById(R.id.navHeaderName).getRootView();
        header.findViewById(R.id.navHeaderName).setOnClickListener(v -> openSettings());

        updateHeader();
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeader();
        presenter.loadConversations();
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    private void updateHeader() {
        SessionManager sessionManager = ChatBotApp.getInstance().getSessionManager();
        TextView nameView = findViewById(R.id.navHeaderName);
        TextView emailView = findViewById(R.id.navHeaderEmail);
        if (nameView != null) {
            nameView.setText(sessionManager.getDisplayName());
        }
        if (emailView != null) {
            emailView.setText(sessionManager.getEmail());
        }
    }

    @Override
    public void showConversations(List<Conversation> conversations) {
        adapter.setConversations(conversations);
    }

    @Override
    public void showEmptyState(boolean empty) {
        binding.emptyState.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading(boolean loading) {
        binding.mainProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void openChat(long conversationId, String title) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_CONVERSATION_ID, conversationId);
        intent.putExtra(ChatActivity.EXTRA_CONVERSATION_TITLE, title);
        startActivity(intent);
    }

    @Override
    public void openNewChat(long conversationId) {
        openChat(conversationId, "New Chat");
    }

    private void showConversationOptions(Conversation conversation) {
        String[] options = {"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(conversation.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameDialog(conversation);
                    } else {
                        confirmDelete(conversation);
                    }
                })
                .show();
    }

    private void showRenameDialog(Conversation conversation) {
        EditText input = new EditText(this);
        input.setText(conversation.getTitle());
        input.setPadding(48, 32, 48, 32);
        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_chat)
                .setView(input)
                .setPositiveButton(R.string.save, (d, w) ->
                        presenter.renameConversation(conversation.getId(), input.getText().toString()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDelete(Conversation conversation) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_chat)
                .setMessage(R.string.delete_chat_message)
                .setPositiveButton(R.string.delete, (d, w) ->
                        presenter.deleteConversation(conversation.getId()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openSettings() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }
}