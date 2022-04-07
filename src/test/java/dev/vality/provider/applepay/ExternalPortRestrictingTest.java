package dev.vality.provider.applepay;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static dev.vality.provider.applepay.config.ApplicationConfig.HEALTH;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"server.rest.port=65434", "management.server.port=8023"})
public class ExternalPortRestrictingTest {

    private static final String FAKE_REST_PATH = "/you-not-found";
    private static final String MAPPED_REST_ENDPATH = "/session";

    @Value("${server.rest.port}")
    private int restPort;

    @Value("${management.server.port}")
    private int managementPort;

    @Value("/${server.rest.endpoint}")
    private String restEndpoint;

    @Test
    public void test() throws IOException {
        String baseUrl = "http://localhost:" + restPort;
        String restUrl = baseUrl + restEndpoint;
        HttpGet httpGetTransaction = new HttpGet(restUrl + MAPPED_REST_ENDPATH);
        HttpGet httpGetHealth = new HttpGet("http://localhost:" + managementPort + HEALTH);
        HttpGet httpGetWrongAddress = new HttpGet(baseUrl + FAKE_REST_PATH);

        assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED,
                getHttpClient().execute(httpGetTransaction).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_OK, getHttpClient().execute(httpGetHealth).getStatusLine().getStatusCode());
        assertEquals(HttpStatus.SC_NOT_FOUND,
                getHttpClient().execute(httpGetWrongAddress).getStatusLine().getStatusCode());
    }

    private CloseableHttpClient getHttpClient() {
        return HttpClientBuilder.create().build();
    }
}
