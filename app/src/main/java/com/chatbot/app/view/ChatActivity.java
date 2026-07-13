package com.chatbot.app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatbot.app.ChatBotApp;
import com.chatbot.app.R;
import com.chatbot.app.adapter.MessageAdapter;
import com.chatbot.app.contract.ChatContract;
import com.chatbot.app.databinding.ActivityChatBinding;
import com.chatbot.app.model.entity.Message;
import com.chatbot.app.model.db.DatabaseHelper;
import com.chatbot.app.model.repo.ChatRepository;
import com.chatbot.app.presenter.ChatPresenter;

import java.util.List;

public class ChatActivity extends AppCompatActivity implements ChatContract.View {

    public static final String EXTRA_CONVERSATION_ID = "conversation_id";
    public static final String EXTRA_CONVERSATION_TITLE = "conversation_title";

    private ActivityChatBinding binding;
    private ChatPresenter presenter;
    private MessageAdapter adapter;
    private long conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        conversationId = getIntent().getLongExtra(EXTRA_CONVERSATION_ID, -1);
        String title = getIntent().getStringExtra(EXTRA_CONVERSATION_TITLE);

        setSupportActionBar(binding.chatToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title != null ? title : "Chat");
        }

        DatabaseHelper db = ChatBotApp.getInstance().getDatabaseHelper();
        long userId = ChatBotApp.getInstance().getSessionManager().getUserId();
        presenter = new ChatPresenter(this, new ChatRepository(db), conversationId, userId);

        adapter = new MessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        binding.messageRecycler.setLayoutManager(layoutManager);
        binding.messageRecycler.setAdapter(adapter);

        binding.sendButton.setOnClickListener(v ->
                presenter.sendMessage(binding.messageInput.getText().toString()));



        presenter.loadMessages();
    }

    private void sendSuggestion(String text) {
        binding.messageInput.setText(text);
        presenter.sendMessage(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_rename) {
            showRenameDialog();
            return true;
        } else if (id == R.id.action_delete) {
            confirmDelete();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showRenameDialog() {
        EditText input = new EditText(this);
        input.setText(getSupportActionBar() != null ? getSupportActionBar().getTitle().toString() : "");
        input.setPadding(48, 32, 48, 32);
        new AlertDialog.Builder(this)
                .setTitle(R.string.rename_chat)
                .setView(input)
                .setPositiveButton(R.string.save, (d, w) ->
                        presenter.renameConversation(input.getText().toString()))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_chat)
                .setMessage(R.string.delete_chat_message)
                .setPositiveButton(R.string.delete, (d, w) -> {
                    presenter.deleteConversation();
                    finish();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void showMessages(List<Message> messages) {
        adapter.setMessages(messages);
        scrollToBottom();

    }

    @Override
    public void addMessage(Message message) {
        adapter.addMessage(message);
        scrollToBottom();
    }

    @Override
    public void showTypingIndicator(boolean visible) {
        adapter.setTypingVisible(visible);
        scrollToBottom();
    }

    @Override
    public void showLoading(boolean loading) {
        binding.sendButton.setEnabled(!loading);
        binding.messageInput.setEnabled(!loading);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void clearInput() {
        binding.messageInput.setText("");
    }

    private void scrollToBottom() {
        binding.messageRecycler.post(() -> {
            if (adapter.getItemCount() > 0) {
                binding.messageRecycler.smoothScrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.destroy();
        }
    }
}
