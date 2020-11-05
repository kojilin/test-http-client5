import okhttp3.Protocol;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.hc.client5.http.async.AsyncExecCallback;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.ChainElement;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.H2AsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.cache.CachingH2AsyncClientBuilder;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncDataConsumer;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.*;

public class HttpClientTest {

    @Test
    public void testAsync() throws Exception {
        final AtomicInteger successCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();

        final MockWebServer server = new MockWebServer();
        server.start();

        server.enqueue(new MockResponse());
        final CloseableHttpAsyncClient client =
                HttpAsyncClientBuilder
                        .create()
                        .addExecInterceptorBefore(ChainElement.PROTOCOL.name(),
                                "test", (request, entityProducer,
                                         scope, chain, asyncExecCallback) -> chain.proceed(request, entityProducer, scope, new AsyncExecCallback() {
                                    @Override
                                    public AsyncDataConsumer handleResponse(HttpResponse response, EntityDetails entityDetails) throws HttpException, IOException {
                                        return asyncExecCallback.handleResponse(response, entityDetails);
                                    }

                                    @Override
                                    public void handleInformationResponse(HttpResponse response) throws HttpException, IOException {
                                        asyncExecCallback.handleInformationResponse(response);
                                    }

                                    @Override
                                    public void completed() {
                                        successCount.incrementAndGet();
                                        asyncExecCallback.completed();
                                    }

                                    @Override
                                    public void failed(Exception cause) {
                                        failCount.incrementAndGet();
                                        asyncExecCallback.failed(cause);
                                    }
                                }))
                        .disableAutomaticRetries().build();
        client.start();
        final Future<SimpleHttpResponse> future = client.execute(
                new SimpleHttpRequest("GET",
                        new HttpHost("localhost", server.getPort()), "/test"),
                new FutureCallback<>() {
                    @Override
                    public void completed(SimpleHttpResponse result) {
                    }

                    @Override
                    public void failed(Exception ex) {
                    }

                    @Override
                    public void cancelled() {
                    }
                });

        assertEquals(200, future.get().getCode());
        assertNotNull(future.get().getBody());
        await().until(() -> successCount.get() == 1);
        await().until(() -> failCount.get() == 0);
    }

    @Test
    public void testH2Async() throws Exception {
        final AtomicInteger successCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();
        final MockWebServer server = new MockWebServer();
        server.setProtocols(List.of(Protocol.H2_PRIOR_KNOWLEDGE));
        server.start();
        server.enqueue(new MockResponse());
        final CloseableHttpAsyncClient client =
                H2AsyncClientBuilder
                        .create()
                        .addExecInterceptorBefore(ChainElement.PROTOCOL.name(),
                                "test", (request, entityProducer,
                                         scope, chain, asyncExecCallback) -> chain.proceed(request, entityProducer, scope, new AsyncExecCallback() {
                                    @Override
                                    public AsyncDataConsumer handleResponse(HttpResponse response, EntityDetails entityDetails) throws HttpException, IOException {
                                        return asyncExecCallback.handleResponse(response, entityDetails);
                                    }

                                    @Override
                                    public void handleInformationResponse(HttpResponse response) throws HttpException, IOException {
                                        asyncExecCallback.handleInformationResponse(response);
                                    }

                                    @Override
                                    public void completed() {
                                        successCount.incrementAndGet();
                                        asyncExecCallback.completed();
                                    }

                                    @Override
                                    public void failed(Exception cause) {
                                        failCount.incrementAndGet();
                                        asyncExecCallback.failed(cause);
                                    }
                                }))
                        .disableAutomaticRetries().build();
        client.start();
        final Future<SimpleHttpResponse> future = client.execute(
                new SimpleHttpRequest("GET",
                        new HttpHost("localhost", server.getPort()), "/test"),
                new FutureCallback<>() {
                    @Override
                    public void completed(SimpleHttpResponse result) {
                    }

                    @Override
                    public void failed(Exception ex) {
                    }

                    @Override
                    public void cancelled() {
                    }
                });

        assertEquals(200, future.get().getCode());
        // Compare to Async, this becomes null
        assertNull(future.get().getBody());
        await().until(() -> successCount.get() == 1);
        await().until(() -> failCount.get() == 0);
    }

    @Test
    public void testCachingH2Async() throws Exception {
        final AtomicInteger successCount = new AtomicInteger();
        final AtomicInteger failCount = new AtomicInteger();
        final MockWebServer server = new MockWebServer();
        server.setProtocols(List.of(Protocol.H2_PRIOR_KNOWLEDGE));
        server.start();
        server.enqueue(new MockResponse());
        final CloseableHttpAsyncClient client =
                CachingH2AsyncClientBuilder
                        .create()
                        .addExecInterceptorBefore(ChainElement.PROTOCOL.name(),
                                "test", (request, entityProducer,
                                         scope, chain, asyncExecCallback) -> chain.proceed(request, entityProducer, scope, new AsyncExecCallback() {
                                    @Override
                                    public AsyncDataConsumer handleResponse(HttpResponse response, EntityDetails entityDetails) throws HttpException, IOException {
                                        return asyncExecCallback.handleResponse(response, entityDetails);
                                    }

                                    @Override
                                    public void handleInformationResponse(HttpResponse response) throws HttpException, IOException {
                                        asyncExecCallback.handleInformationResponse(response);
                                    }

                                    @Override
                                    public void completed() {
                                        successCount.incrementAndGet();
                                        asyncExecCallback.completed();
                                    }

                                    @Override
                                    public void failed(Exception cause) {
                                        failCount.incrementAndGet();
                                        asyncExecCallback.failed(cause);
                                    }
                                }))
                        .disableAutomaticRetries().build();
        client.start();
        final Future<SimpleHttpResponse> future = client.execute(
                new SimpleHttpRequest("GET",
                        new HttpHost("localhost", server.getPort()), "/test"),
                new FutureCallback<>() {
                    @Override
                    public void completed(SimpleHttpResponse result) {
                    }

                    @Override
                    public void failed(Exception ex) {
                    }

                    @Override
                    public void cancelled() {
                    }
                });

        assertEquals(200, future.get().getCode());
        // Still null
        assertNull(future.get().getBody());

        // Compare to non-caching one, the fail count is 1.
        await().until(() -> failCount.get() == 1);
        await().until(() -> successCount.get() == 0);
    }
}
