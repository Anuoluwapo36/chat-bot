package com.chatbot.app.model.api;

import com.chatbot.app.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QwenClient {

    private static QwenApiService apiService;

    public static QwenApiService getService() {
        if (apiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.QWEN_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(QwenApiService.class);
        }
        return apiService;
    }

    public static String getAuthHeader() {
        return "Bearer " + BuildConfig.QWEN_API_KEY;
    }

    public static boolean isApiKeyConfigured() {
        return BuildConfig.QWEN_API_KEY != null && !BuildConfig.QWEN_API_KEY.trim().isEmpty();
    }
}
