        package com.globomantics.insecureapp.controller;
        
        import com.sun.net.httpserver.HttpExchange;
        import com.sun.net.httpserver.HttpHandler;
        import com.sun.net.httpserver.HttpServer;
        import org.springframework.http.HttpStatus;

        import java.io.IOException;
        import java.io.OutputStream;
        import java.net.InetSocketAddress;

        import java.util.concurrent.Executors;
        import java.util.concurrent.ThreadPoolExecutor;

        // XSS - JavaScript <script> injection
        public class InsecureApp {
            public static void main(String[] args) throws IOException {
                System.out.println("Starting banking website server");
                HttpServer appServer = HttpServer.create(new InetSocketAddress("localhost", 8001), 0);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
                appServer.setExecutor(threadPoolExecutor);

                appServer.createContext("/insecureapp", new RequestHandler());
                appServer.start();
        
                System.out.println(" Server started on port 8001");
            }
        
            public static class RequestHandler implements HttpHandler {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("GET".equals(exchange.getRequestMethod())
                            && exchange.getRequestURI().getQuery() != null
                            && exchange.getRequestURI().getQuery().contains("ItemSku")) {
                        handleResponse(exchange, getSearchPage(exchange.getRequestURI().getQuery()));
                    } else {
                        handleResponse(exchange, getDefaultPage());
                    }
                }
        
                private String getDefaultPage() {

                    return "<form action=\"\" method=\"get\">\n" +
                            " \n" + " <label for=\"name\">Item SKU: </label>\n" +
                            " <input size=\"100\" type=\"text\" name=\"ItemSku\" id=\"ItemSku\">\n" +
                            " <input type=\"submit\" value=\"Search\">\n" + " \n" + "</form>";
                }
        
                private String getSearchPage(String query) {
                    System.out.println("query: " + query);
                    query = query.substring(query.indexOf("=") + 1);
                    String searchParam = query.replace("+", " ");
                    return "<form action=\"search\" method=\"get\">\n" + " " +
                            searchParam + " ";
                }
        
                private void handleResponse(HttpExchange exchange, String responseStr) throws IOException {
                    OutputStream httpOutput = exchange.getResponseBody();
                    exchange.sendResponseHeaders(HttpStatus.OK.value(), responseStr.length());
                    httpOutput.write(responseStr.getBytes());
                    httpOutput.flush();
                    httpOutput.close();
                }
            }
        }