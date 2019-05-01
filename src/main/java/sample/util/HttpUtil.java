package sample.util;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;


public class HttpUtil {


    public static Response sendRequest(String uri, HttpRequestMethod httpRequestMethod, Map<String, Object> param) throws IOException {
        String url = Constrant.serverAddres + uri;
        URL obj = null;
        HttpURLConnection con = null;
        if (httpRequestMethod.name().equals("POST")) {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            common_Post(httpRequestMethod, param, con);
        }
        if (httpRequestMethod.name().equals("GET")) {
            con = common_Get(param, url);
            con.setRequestMethod(httpRequestMethod.name());
            con.setRequestProperty("charset", "utf-8");
        }
        return getResponse(con);
    }

    private static void common_Post(HttpRequestMethod httpRequestMethod, Map<String, Object> param, HttpURLConnection con) throws IOException {
        String urlParameters = "";
        for (Map.Entry<String, Object> paramEntry : param.entrySet()) {
            urlParameters += paramEntry.getKey() + "=" + paramEntry.getValue() + "&";
        }
        if (!urlParameters.isEmpty()) {
            urlParameters.substring(0, urlParameters.length() - 1);
        }
        byte[] data = urlParameters.getBytes(StandardCharsets.UTF_8);
        con.setRequestMethod(httpRequestMethod.name());
        con.setRequestProperty("charset", "utf-8");
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.write(data);
        }
    }


    public static Response sendRequestVithHeader(String uri, HttpRequestMethod httpRequestMethod,
                                                 Map<String, Object> param, Map<String,String> headerKeyAndValu) throws IOException {
        String url = Constrant.serverAddres + uri;
        URL obj = null;
        HttpURLConnection con = null;
        if (httpRequestMethod.name().equals("POST") || httpRequestMethod.name().equals("DELETE") ) {
            obj = new URL(url);
            con = (HttpURLConnection) obj.openConnection();
            for (Map.Entry<String, String> stringStringEntry : headerKeyAndValu.entrySet()) {
                con.setRequestProperty(stringStringEntry.getKey(),stringStringEntry.getValue());
            }
            common_Post(httpRequestMethod, param, con);
        }
        if (httpRequestMethod.name().equals("GET")) {
            con= common_Get(param, url);
            for (Map.Entry<String, String> stringStringEntry : headerKeyAndValu.entrySet()) {
                con.setRequestProperty(stringStringEntry.getKey(),stringStringEntry.getValue());
            }
            con.setRequestMethod(httpRequestMethod.name());
            con.setRequestProperty("charset", "utf-8");
        }
        return getResponse(con);
    }

    private static HttpURLConnection common_Get(Map<String, Object> param, String url) throws IOException {
        URL obj;
        HttpURLConnection con;
        if (param.size() != 0) {
            url += "?";
        }
        for (Map.Entry<String, Object> paramEntry : param.entrySet()) {
            url += paramEntry.getKey() + "=" + paramEntry.getValue() + "&";
        }
        url = url.substring(0, url.length() - 1);
        obj = new URL(url);
        con = (HttpURLConnection) obj.openConnection();
        return con;
    }

    public static Response sendPostRequestWithBody(String uri, Map<String, Object> body) throws IOException {
        String url = Constrant.serverAddres + uri;
        URL object = new URL(url);
        HttpURLConnection con = (HttpURLConnection) object.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            String jsonString = "{";
            for (Map.Entry<String, Object> stringObjectEntry : body.entrySet()) {
                jsonString += "\"" + stringObjectEntry.getKey() + "\" : \"" + stringObjectEntry.getValue() + "\",";
            }
//            System.out.println(jsonString);
            jsonString = jsonString.substring(0, jsonString.length() - 1) + "}";
            System.out.println(jsonString);
            wr.write(jsonString.getBytes(StandardCharsets.UTF_8));
        }
        return getResponse(con);
    }


    private static Response getResponse(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        JSONObject myresponse = null;
        BufferedReader in = null;
        if (responseCode == 200) {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("server response "+response);
            myresponse = new JSONObject(response.toString());
        }
        if (in != null) {
            in.close();
        }
        con.disconnect();
        return Response.builder()
                .responseCode(responseCode)
                .data(myresponse)
                .build();
    }

}
