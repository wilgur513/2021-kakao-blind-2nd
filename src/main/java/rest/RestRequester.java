package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

public class RestRequester {
    private HttpURLConnection connection;
    private Object body;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> T getData(Class<T> cls) {
        if(body != null) {
            try {
                writeJsonData(objectMapper.writeValueAsString(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return mapping(request(), cls);
    }

    public static RestRequester create(HttpURLConnection connection, Object body){
        return new RestRequester(connection, body);
    }

    private RestRequester(HttpURLConnection connection, Object body) {
        this.connection = connection;
        this.body = body;
    }

    private <T> T mapping(String json, Class<T> cls){
        T result = null;

        try {
            result = objectMapper.readValue(json, cls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private void writeJsonData(String json){
        try {
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String request(){
        if (getResponseCode() != 200)
            throw new RuntimeException("Error code : " + getResponseCode());
        return getResponse();
    }

    private int getResponseCode(){
        int code = 0;

        try{
            code = connection.getResponseCode();
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        return code;
    }

    private String getResponse(){
        String result = "";

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                result += line;
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
