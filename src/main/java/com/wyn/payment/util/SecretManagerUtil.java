package com.wyn.payment.util;
 
import org.springframework.stereotype.Component;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
// import org.json.JSONObject;
 
@Component
public class SecretManagerUtil {
 
    public static String getSecret(String projectId, String secretId, String version) {
        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, version);
            AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Error accessing secret: " + e.getMessage(), e);
        }
    }
 
    // public static void main(String[] args) {
    //     String projectId = "secret-manager-448804";
    //     String secretId = "PAYMENT_SYSTEM_DEV_DB";
    //     String version = "1";
 
    //     String secretJson = getSecret(projectId, secretId, version);
 
    //     try {
    //         // Try parsing as JSON
    //         JSONObject secret = new JSONObject(secretJson);
    //         String password = secret.getString("password");
    //         System.out.println("Password: " + password);
    //     } catch (Exception e) {
    //         // If JSON parsing fails, treat it as a plain string
    //         System.out.println("Secret Value (Not JSON): " + secretJson);
    //     }
    // }
}