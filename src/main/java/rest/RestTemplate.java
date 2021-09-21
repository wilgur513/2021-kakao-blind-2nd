package rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class RestTemplate {
    private HttpURLConnection connection;
    private Object body;

    private RestTemplate(String uri) {
        try {
            connection = (HttpURLConnection)new URL(uri).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RestTemplate header(String property, String value) {
        connection.setRequestProperty(property, value);
        return this;
    }

    public RestTemplate method(HttpMethod method) {
        try {
            connection.setRequestMethod(method.name());
        } catch (ProtocolException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public RestTemplate body(Object body) {
        this.body = body;
        return this;
    }

    public <T> T getResponse(Class<T> cls) {
        return RestRequester.create(connection, body).getData(cls);
    }

    public static RestTemplate uri(String uri) {
        return new RestTemplate(uri);
    }
}
