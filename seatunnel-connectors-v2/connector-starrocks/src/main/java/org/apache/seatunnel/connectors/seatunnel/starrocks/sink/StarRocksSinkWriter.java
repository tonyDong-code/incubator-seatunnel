package org.apache.seatunnel.connectors.seatunnel.starrocks.sink;

import com.starrocks.shade.org.apache.commons.codec.binary.Base64;
import com.starrocks.shade.org.apache.http.HttpHeaders;
import com.starrocks.shade.org.apache.http.client.methods.CloseableHttpResponse;
import com.starrocks.shade.org.apache.http.client.methods.HttpPut;
import com.starrocks.shade.org.apache.http.entity.StringEntity;
import com.starrocks.shade.org.apache.http.impl.client.CloseableHttpClient;
import com.starrocks.shade.org.apache.http.impl.client.DefaultRedirectStrategy;
import com.starrocks.shade.org.apache.http.impl.client.HttpClientBuilder;
import com.starrocks.shade.org.apache.http.impl.client.HttpClients;
import com.starrocks.shade.org.apache.http.util.EntityUtils;
import org.apache.seatunnel.api.sink.SinkWriter;
import org.apache.seatunnel.api.table.type.SeaTunnelRow;
import org.apache.seatunnel.api.table.type.SeaTunnelRowType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class StarRocksSinkWriter implements SinkWriter<SeaTunnelRow, Void, Void> {
    private final static String STARROCKS_HOST = "xxx.com";
    private final static String STARROCKS_DB = "test";
    private final static String STARROCKS_TABLE = "stream_test";
    private final static String STARROCKS_USER = "root";
    private final static String STARROCKS_PASSWORD = "xxx";
    private final static int STARROCKS_HTTP_PORT = 8030;

    @Override
    public void write(SeaTunnelRow element) throws IOException {
    }

    @Override
    public Optional<Void> prepareCommit() throws IOException {
        return Optional.empty();
    }

    @Override
    public void abortPrepare() {

    }

    @Override
    public void close() throws IOException {

    }

    private void sendData(String content) throws Exception {
        final String loadUrl = String.format("http://%s:%s/api/%s/%s/_stream_load",
                STARROCKS_HOST,
                STARROCKS_HTTP_PORT,
                STARROCKS_DB,
                STARROCKS_TABLE);

        final HttpClientBuilder httpClientBuilder = HttpClients
                .custom()
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    @Override
                    protected boolean isRedirectable(String method) {
                        return true;
                    }
                });

        try (CloseableHttpClient client = httpClientBuilder.build()) {
            HttpPut put = new HttpPut(loadUrl);
            StringEntity entity = new StringEntity(content, "UTF-8");
            put.setHeader(HttpHeaders.EXPECT, "100-continue");
            put.setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader(STARROCKS_USER, STARROCKS_PASSWORD));
            // the label header is optional, not necessary
            // use label header can ensure at most once semantics
            put.setHeader("label", "39c25a5c-7000-496e-a98e-348a264c81de");
            put.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(put)) {
                String loadResult = "";
                if (response.getEntity() != null) {
                    loadResult = EntityUtils.toString(response.getEntity());
                }
                final int statusCode = response.getStatusLine().getStatusCode();
                // statusCode 200 just indicates that starrocks be service is ok, not stream load
                // you should see the output content to find whether stream load is success
                if (statusCode != 200) {
                    throw new IOException(
                            String.format("Stream load failed, statusCode=%s load result=%s", statusCode, loadResult));
                }

                System.out.println(loadResult);
            }
        }
    }

    private String basicAuthHeader(String username, String password) {
        final String tobeEncode = username + ":" + password;
        byte[] encoded = Base64.encodeBase64(tobeEncode.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encoded);
    }
}
