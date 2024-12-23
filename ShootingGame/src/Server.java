import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private Set<ClientHandler> clients = new HashSet<>();
    private List<PrintWriter> clientOutputs = new ArrayList<>();  // 클라이언트 출력 스트림을 저장할 리스트
    private Map<String, Integer> playerScores = new HashMap<>();	//각 점수 저장

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("서버가 시작되었습니다. 포트: " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("새 클라이언트 연결됨.");
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {  // 자신에게 메시지를 보내지 않도록 처리
                client.sendMessage(message);
            }
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter output;
        private String playerId;
        private int score;
        private int clientIndex;
        
        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                output = new PrintWriter(socket.getOutputStream(), true);  // 출력 스트림 초기화
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try (
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                // 클라이언트로부터 ID 수신
                playerId = input.readLine();
                System.out.println(playerId + "님이 입장했습니다.");
                broadcast(playerId + "님이 입장했습니다.", this);
  

                // 메시지 수신 및 브로드캐스트
                String message;
                while ((message = input.readLine()) != null) {
                    System.out.println(playerId + ": " + message);
                    broadcast(playerId + ": " + message, this);
                    
                    if (message.startsWith("GAME_OVER:")) {
                        int score = Integer.parseInt(message.substring(10)); // 점수 추출
                        playerScores.put(playerId, score); // 점수 저장
                        
                        // 상대방 점수 확인
                        String opponentId = findOpponentId(playerId);
                        if (opponentId != null && playerScores.containsKey(opponentId)) {
                            int opponentScore = playerScores.get(opponentId);

                            // 점수 비교 및 결과 전송
                            String result = score > opponentScore ? "WIN" : (score < opponentScore ? "LOSE" : "TIE");

                            // 두 클라이언트에게 게임 오버 결과 전송
                            sendToClient(this, "RESULT:" + result + ":" + score + ":" + opponentScore);
                            sendToClient(findClient(opponentId), "RESULT:" + (result.equals("WIN") ? "LOSE" : "WIN") + ":" + opponentScore + ":" + score);

                            // 게임 오버를 모든 클라이언트에 브로드캐스트
                            broadcast("OPPONENT_GAME_OVER:" + result + ":" + score + ":" + opponentScore, this);
                        }
                    }

                }
            } catch (IOException e) {
                System.err.println("클라이언트 연결 끊김: " + playerId);
            } 
            finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                clients.remove(this);
                broadcast(playerId + "님이 퇴장했습니다.", this);
            }
        }

        public void sendMessage(String message) {
            if (output != null) {
                output.println(message);
            }
        }
        
        private String findOpponentId(String playerId) {
            for (ClientHandler client : clients) {
                if (!client.playerId.equals(playerId)) {
                    return client.playerId;
                }
            }
            return null; // 상대방이 없으면 null 반환
        }
        
        private ClientHandler findClient(String playerId) {
            for (ClientHandler client : clients) {
                if (client.playerId.equals(playerId)) {
                    return client;
                }
            }
            return null;
        }
        
        // 추가된 sendToClient 메서드
        private void sendToClient(ClientHandler client, String message) {
            if (client != null) {
                client.sendMessage(message);
            }
        }
        
    }
}