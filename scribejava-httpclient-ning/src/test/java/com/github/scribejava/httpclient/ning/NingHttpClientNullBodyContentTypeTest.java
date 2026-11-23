package com.github.scribejava.httpclient.ning;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.model.Verb;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NingHttpClientNullBodyContentTypeTest {

    @Test
    public void postWithNullStringBodyOmitsDefaultContentType() throws Exception {
        assertNoDefaultContentType(new Post() {
            @Override
            public void send(NingHttpClient client, String url) throws Exception {
                client.execute(null, Collections.<String, String>emptyMap(), Verb.POST, url, (String) null).close();
            }
        });
    }

    @Test
    public void postWithNullByteBodyOmitsDefaultContentType() throws Exception {
        assertNoDefaultContentType(new Post() {
            @Override
            public void send(NingHttpClient client, String url) throws Exception {
                client.execute(null, Collections.<String, String>emptyMap(), Verb.POST, url, (byte[]) null).close();
            }
        });
    }

    @Test
    public void postWithNullBodyKeepsExplicitContentType() throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.start();
        try (NingHttpClient client = new NingHttpClient()) {
            final Map<String, String> headers = new HashMap<String, String>();
            headers.put(HttpClient.CONTENT_TYPE, "application/json");
            client.execute(null, headers, Verb.POST, server.url("/empty").toString(), (String) null).close();
            assertEquals("application/json", server.takeRequest().getHeader(HttpClient.CONTENT_TYPE));
        } finally {
            server.shutdown();
        }
    }

    @Test
    public void postWithStringBodyKeepsDefaultContentType() throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.start();
        try (NingHttpClient client = new NingHttpClient()) {
            client.execute(null, Collections.<String, String>emptyMap(), Verb.POST, server.url("/body").toString(),
                    "grant_type=client_credentials").close();
            final RecordedRequest recorded = server.takeRequest();
            assertEquals(HttpClient.DEFAULT_CONTENT_TYPE, recorded.getHeader(HttpClient.CONTENT_TYPE));
            assertEquals("grant_type=client_credentials", recorded.getBody().readUtf8());
        } finally {
            server.shutdown();
        }
    }

    private static void assertNoDefaultContentType(Post post) throws Exception {
        final MockWebServer server = new MockWebServer();
        server.enqueue(new MockResponse());
        server.start();
        try (NingHttpClient client = new NingHttpClient()) {
            post.send(client, server.url("/empty").toString());
            final RecordedRequest recorded = server.takeRequest();
            assertEquals("POST", recorded.getMethod());
            assertNull(recorded.getHeader(HttpClient.CONTENT_TYPE));
        } finally {
            server.shutdown();
        }
    }

    private interface Post {

        void send(NingHttpClient client, String url) throws Exception;
    }
}
