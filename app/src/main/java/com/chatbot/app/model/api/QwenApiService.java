package com.chatbot.app.model.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface QwenApiService {

    @POST("chat/completions")
    Call<QwenResponse> chat(
            @Header("Authorization") String authorization,
            @Body QwenRequest request
    );
}
