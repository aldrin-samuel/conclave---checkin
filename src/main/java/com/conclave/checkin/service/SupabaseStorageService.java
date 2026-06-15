package com.conclave.checkin.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-key}")
    private String serviceKey;

    @Value("${supabase.bucket}")
    private String bucket;


    public String uploadPass(File file, String uid) throws Exception {

        byte[] bytes = Files.readAllBytes(file.toPath());

        String fileName = uid + ".png";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        supabaseUrl +
                                "/storage/v1/object/" +
                                bucket +
                                "/" +
                                fileName
                ))
                .header("apikey", serviceKey)
                .header("Authorization", "Bearer " + serviceKey)
                .header("Content-Type", "image/png")
                .POST(HttpRequest.BodyPublishers.ofByteArray(bytes))
                .build();

        HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        return supabaseUrl +
                "/storage/v1/object/public/" +
                bucket +
                "/" +
                fileName;
    }
}