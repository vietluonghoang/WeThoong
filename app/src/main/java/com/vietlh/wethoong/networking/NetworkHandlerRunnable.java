package com.vietlh.wethoong.networking;

import com.vietlh.wethoong.entities.Phantich;
import com.vietlh.wethoong.entities.interfaces.CallbackActivity;
import com.vietlh.wethoong.utils.GeneralSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;

public class NetworkHandlerRunnable implements Runnable{
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_DELETE = "DELETE";
    public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
    public static final String CONTENT_TYPE_APPLICATION_XWWW_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String MIME_TYPE_APPLICATION_JSON = "application/json";
    public static final String ACTION_CASE_CHECK_CONNECTION = "checkConnection";

    private int connectionTimeout = 30000;
    private CallbackActivity parent;
    private MessageContainer messages;
    private String url;
    private String method;
    private String contentType;
    private String mimeType;
    private HashMap<String, String> payload;
    private String callbackActionCase;

    public NetworkHandlerRunnable(String url, String callbackActionCase, int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        this.url = url;
        this.method = METHOD_GET;
        this.callbackActionCase = callbackActionCase;
    }

    public NetworkHandlerRunnable(CallbackActivity parent, String url, String method, String mimeType, HashMap<String, String> payload, String callbackActionCase) {
        this.parent = parent;
        this.url = url;
        this.method = method;
        this.mimeType = mimeType;
        this.payload = payload;
        this.callbackActionCase = callbackActionCase;
        this.connectionTimeout = GeneralSettings.defaultHttpRequestConnectionTimeout;
    }

    public NetworkHandlerRunnable(CallbackActivity parent, String url, String method, String contentType, String mimeType, HashMap<String, String> payload, String callbackActionCase) {
        this.parent = parent;
        this.url = url;
        this.method = method;
        this.contentType = contentType;
        this.mimeType = mimeType;
        this.payload = payload;
        this.callbackActionCase = callbackActionCase;
        this.connectionTimeout = GeneralSettings.defaultHttpRequestConnectionTimeout;
    }

    public NetworkHandlerRunnable(CallbackActivity parent, String url, String method, String contentType, String mimeType, HashMap<String, String> payload, String callbackActionCase, int connectionTimeout) {
        this.parent = parent;
        this.url = url;
        this.method = method;
        this.contentType = contentType;
        this.mimeType = mimeType;
        this.payload = payload;
        this.callbackActionCase = callbackActionCase;
        this.connectionTimeout = connectionTimeout;
    }

    public MessageContainer getMessages() {
        return messages;
    }

    public void requestData(String url, String mimeType) throws IOException {
        System.out.println("############ Sending request to " + url);
        URL endpoint = new URL(url);
        System.out.println(endpoint.getPath());
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setConnectTimeout(connectionTimeout);
        initResponseData(connection);
        connection.disconnect();
        System.out.println("############ Finished sending request to '" + url + "'\n\twith message: \n" + messages.getValue(MessageContainer.MESSAGE));
    }

    public void sendData(String url, String method, String contentType, HashMap<String, String> data) throws IOException {
        System.out.println("############ Sending data to " + url);
        URL endpoint = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();

        conn.setConnectTimeout(connectionTimeout);
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", contentType);
        conn.setRequestProperty("Accept", contentType);
        conn.setDoOutput(true);
        conn.setDoInput(true);

        JSONObject jsonParam = new JSONObject();
        for (String key :
                data.keySet()) {
            try {
                jsonParam.put(key, data.get(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        System.out.println("JSON: payload\n" + jsonParam.toString());
        DataOutputStream os = new DataOutputStream(conn.getOutputStream());
//            os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
        os.writeBytes(jsonParam.toString());

        os.flush();
        os.close();

        System.out.println("STATUS: " + String.valueOf(conn.getResponseCode()));
        System.out.println("MSG: " + conn.getResponseMessage());
//            initResponseData(conn);
        initResponseData(conn);
        conn.disconnect();
        System.out.println("############ Finished sending data '" + url + "'\n\twith message: \n" + messages.getValue(MessageContainer.MESSAGE));
    }

    private void initResponseData(HttpURLConnection connection) throws IOException {
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                responseBody.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        messages.setValue(MessageContainer.ERROR, "");
        messages.setValue(MessageContainer.MESSAGE, connection.getResponseMessage());
        messages.setValue(MessageContainer.DATA, sb.toString());
    }

    public void parseAppConfigData() {

        try {
            JSONArray jsonArray = new JSONArray((String) messages.getValue(MessageContainer.DATA));

            JSONObject jsonParam = new JSONObject();
            for (int i = 0; i < jsonArray.length(); i++) {
                String key = jsonArray.getJSONObject(i).getString("configname");
                String value = jsonArray.getJSONObject(i).getString("configvalue");
                jsonParam.put(key, value);
            }

            System.out.println("JSON: AppConfigData\n" + jsonParam.toString());

            messages.setValue(MessageContainer.DATA, jsonParam);

        } catch (Exception e) {
            e.printStackTrace();
            messages.setValue(MessageContainer.DATA, null);
        }
    }

    public void parsePhantichData() {
        HashMap<String, Phantich> phantichList = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray((String) messages.getValue(MessageContainer.DATA));
            for (int i = 0; i < jsonArray.length(); i++) {
                HashMap<String, String> phantichDetailsHash = new HashMap<>();
                phantichDetailsHash.put("id_key", jsonArray.getJSONObject(i).getString("id_key"));
                phantichDetailsHash.put("author", jsonArray.getJSONObject(i).getString("author"));
                phantichDetailsHash.put("title", jsonArray.getJSONObject(i).getString("title"));
                phantichDetailsHash.put("shortdescription", jsonArray.getJSONObject(i).getString("shortdescription"));
                phantichDetailsHash.put("source", jsonArray.getJSONObject(i).getString("source"));
                phantichDetailsHash.put("source_inapp", jsonArray.getJSONObject(i).getString("source_inapp"));
                phantichDetailsHash.put("revision", jsonArray.getJSONObject(i).getString("revision"));
                phantichDetailsHash.put("contentorder", jsonArray.getJSONObject(i).getString("contentorder"));
                phantichDetailsHash.put("content", jsonArray.getJSONObject(i).getString("content"));
                phantichDetailsHash.put("minhhoa", jsonArray.getJSONObject(i).getString("minhhoa"));
                phantichDetailsHash.put("minhhoatype", jsonArray.getJSONObject(i).getString("minhhoatype"));

                if (phantichList.get(phantichDetailsHash.get("id_key")) == null) {
                    Phantich phantichRaw = new Phantich(phantichDetailsHash.get("id_key"), phantichDetailsHash.get("author"), phantichDetailsHash.get("title"), phantichDetailsHash.get("shortdescription"), phantichDetailsHash.get("source"), phantichDetailsHash.get("source_inapp"), phantichDetailsHash.get("revision"), phantichDetailsHash);
                    phantichList.put(phantichDetailsHash.get("id_key"), phantichRaw);
                } else {
                    phantichList.get(phantichDetailsHash.get("id_key")).updateRawContentDetailed(phantichDetailsHash);
                }
            }

            System.out.println("JSON: PhantichData\n" + phantichList.toString());
            messages.setValue(MessageContainer.DATA, phantichList);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            messages.setValue(MessageContainer.DATA, null);
        }
    }

    public void parseResultStatusData() {
        HashMap<String, String> status = new HashMap<>();

        try {
            JSONObject jObj = new JSONObject((String) messages.getValue(MessageContainer.DATA));
            status.put("status", jObj.getString("status"));


            System.out.println("JSON: ResultStatusData\n" + status.toString());
            messages.setValue(MessageContainer.DATA, status);
        } catch (Exception e) {
            e.printStackTrace();
            messages.setValue(MessageContainer.DATA, null);
        }
    }

    public void updateResult(){
        System.out.println("############ onPostExecute....\n" + messages);
        if (ACTION_CASE_CHECK_CONNECTION.equals(callbackActionCase)) {
            if (messages.getValue(MessageContainer.DATA) != null) {
                System.out.println("===== Internet connection available ");
                GeneralSettings.wasConnectedToInternet = true;
            } else {
                System.out.println("===== No internet connection");
                GeneralSettings.wasConnectedToInternet = false;
            }
        } else {
            if (messages != null && GeneralSettings.wasConnectedToInternet) {
                parent.triggerCallbackAction(callbackActionCase);
            } else {
                System.out.println("############ onPostExecute: Unable to trigger callback action....\n" + messages.getValue(MessageContainer.ERROR) + " : " + messages.getValue(MessageContainer.MESSAGE));
            }
        }
    }

    @Override
    public void run() {
        messages = new MessageContainer();
        if (GeneralSettings.wasConnectedToInternet || ACTION_CASE_CHECK_CONNECTION.equals(callbackActionCase)) {
            try {
                switch (this.method) {
                    case METHOD_GET:
                        requestData(url, mimeType);
                        break;
                    case METHOD_POST:
                        sendData(url, method, contentType, payload);
                        break;
                    default:
                        messages.setValue(MessageContainer.ERROR, "Invalid request method");
                        messages.setValue(MessageContainer.MESSAGE, "Method was set incorrectly as: '" + this.method + "'.");
                }
            } catch (SocketTimeoutException toEx) {
                System.out.println("----Resetting connection status....");
                GeneralSettings.wasConnectedToInternet = false;
                System.out.println("----Extend connection timeout....");
                if (connectionTimeout <= GeneralSettings.defaultHttpRequestConnectionTimeout * 2) {
                    connectionTimeout += 10000;
                }
                toEx.printStackTrace();
                messages.setValue(MessageContainer.ERROR, toEx.getClass().toString());
                messages.setValue(MessageContainer.MESSAGE, toEx.getMessage());
            } catch (IOException e) {
                System.out.println("----Resetting connection status....");
                GeneralSettings.wasConnectedToInternet = false;
                e.printStackTrace();
                messages.setValue(MessageContainer.ERROR, e.getClass().toString());
                messages.setValue(MessageContainer.MESSAGE, e.getMessage());
            }
        } else {
            messages.setValue(MessageContainer.ERROR, "No internet connection");
            messages.setValue(MessageContainer.MESSAGE, "Internet Connection is interrupted");
        }
    }


}
