package com.vietlh.wethoong.networking;

import android.os.AsyncTask;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class NetworkHandler {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_DELETE = "DELETE";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_XWWW_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String MIME_TYPE_APPLICATION_JSON = "application/json";

    private MessageContainer messages = new MessageContainer();

    public MessageContainer getMessages() {
        return messages;
    }

    public void requestData(final String url, String mimeType) {
        System.out.println("############ Sending request to " + url);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL endpoint = new URL(url);
                    System.out.println(endpoint.getPath());
                    HttpsURLConnection connection = (HttpsURLConnection) endpoint.openConnection();
                    int responseCode = connection.getResponseCode();
                    if (responseCode < 200 || responseCode > 299) {
                        messages.setValue(MessageContainer.ERROR, connection.getResponseMessage());
                        messages.setValue(MessageContainer.MESSAGE, "Response error");
                        return;
                    }
                    InputStream responseBody = connection.getInputStream();
                    if (responseBody == null) {
                        messages.setValue(MessageContainer.ERROR, connection.getResponseMessage());
                        messages.setValue(MessageContainer.MESSAGE, "Null response data");
                        return;
                    }
                    InputStreamReader reader = new InputStreamReader(responseBody, "UTF-8");
                    JsonReader json = new JsonReader(reader);
                    messages.setValue(MessageContainer.ERROR, "");
                    messages.setValue(MessageContainer.MESSAGE, connection.getResponseMessage());

                    connection.disconnect();
                    HashMap<String, String> appConfig = new HashMap<>();

                    try {
                        json.beginArray();
                        while (json.hasNext()) {
                            json.beginObject();
                            String key = "";
                            String value = "";
                            while (json.hasNext()) {
                                if ("configname".equals(json.nextName())) {
                                    key = json.nextString();
                                } else {
                                    value = json.nextString();
                                }
                            }
                            appConfig.put(key, value);
                            json.endObject();
                        }
                        json.endArray();
                        json.close();
                        JSONObject jsonParam = new JSONObject();
                        for (String key :
                                appConfig.keySet()) {
                            jsonParam.put(key, appConfig.get(key));
                        }

                        System.out.println("JSON: " + jsonParam.toString());
                        messages.setValue(MessageContainer.DATA, jsonParam);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    messages.setValue(MessageContainer.ERROR, e);
                    messages.setValue(MessageContainer.MESSAGE, e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    messages.setValue(MessageContainer.ERROR, e);
                    messages.setValue(MessageContainer.MESSAGE, e.getMessage());
                }
                System.out.println("############ Finished sending request with message: " + messages.getValue(MessageContainer.MESSAGE));
            }

        });

    }

    public void sendData(final String url, final String method, final String contentType, final HashMap<String, String> data) {
        System.out.println("############ Sending data to " + url);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL endpoint = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                    conn.setRequestMethod(method);
                    conn.setRequestProperty("Content-Type", contentType);
                    conn.setRequestProperty("Accept", contentType);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    JSONObject jsonParam = new JSONObject();
                    for (String key :
                            data.keySet()) {
                        jsonParam.put(key, data.get(key));
                    }

                    System.out.println("JSON: " + jsonParam.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                    os.writeBytes(jsonParam.toString());

                    os.flush();
                    os.close();

                    System.out.println("STATUS: " + String.valueOf(conn.getResponseCode()));
                    System.out.println("MSG: " + conn.getResponseMessage());
                    messages.setValue(MessageContainer.MESSAGE, String.valueOf(conn.getResponseCode()) + " : " + conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("############ Finished sending data with message: " + messages.getValue(MessageContainer.MESSAGE));
            }
        });
    }
}
