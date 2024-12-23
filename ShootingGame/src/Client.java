import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class Client {
    private String playerId;
    private String ipAddress;
    private int port;
    private JTextArea chatArea;
    private Socket socket;
    private PrintWriter output;
    private BufferedReader input;
    private int myScore;
    
    private MainFrame mainFrame; // MainFrame 인스턴스를 추가

    public Client(String playerId, String ipAddress, int port, JTextArea chatArea, MainFrame mainFrame) {
        this.playerId = playerId;
        this.ipAddress = ipAddress;
        this.port = port;
        this.chatArea = chatArea; // 게임 UI에서 사용하는 JTextArea
        this.mainFrame = mainFrame; // MainFrame을 전달받아 저장
    }



    public void start() {
        try {
            socket = new Socket(ipAddress, port);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            // 클라이언트 ID 전송
            output.println(playerId);

            // 서버로부터 메시지 수신
            String message;
            while ((message = input.readLine()) != null) {
                // UI 스레드에서 chatArea를 업데이트
                final String finalMessage = message;
                if (mainFrame != null) {
                    SwingUtilities.invokeLater(() -> mainFrame.appendChatMessage(finalMessage));
                }
                
                // 상대방의 게임 종료 메시지를 처리
                if (finalMessage.startsWith("OPPONENT_GAME_OVER:")) {
                    handleOpponentGameOver(finalMessage);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendGameOver(int score) { // 상대방의 게임 종료 알림
        try {
            // 자신의 점수 전송
            output.println("GAME_OVER:" + score);
            output.flush();

            // 서버 응답 대기
            String response = input.readLine(); // 서버에서 받은 결과 반환
            return response; // 예: "RESULT:WIN:100:80" 형식
        } catch (IOException e) {
            System.err.println("게임 결과 전송 중 오류 발생: " + e.getMessage());
            return "ERROR"; // 오류 발생 시 반환
        }
    }

    // 서버로부터 게임 오버 메시지를 받았을 때 호출되는 메소드
    public void handleOpponentGameOver(String message) {
        if (message.startsWith("OPPONENT_GAME_OVER:")) {
            String[] parts = message.split(":");
            String result = parts[1]; // WIN, LOSE, TIE
            int myScore = Integer.parseInt(parts[2]);
            int opponentScore = Integer.parseInt(parts[3]);

            // 게임 결과 처리
            showGameOver(result, myScore, opponentScore);
        }
    }
    

    
    private void showGameOver(String result, int myScore, int opponentScore) {
        // 게임 종료 팝업창 표시
        JOptionPane.showMessageDialog(null, 
            "Result: " + result + "\nYour Score: " + myScore + "\nOpponent Score: " + opponentScore, 
            "Game Over", 
            JOptionPane.INFORMATION_MESSAGE);
    }


    // 상대방에게 게임 오버 알리기
    public void sendOpponentGameOver(String result, int myScore, int opponentScore) {
        output.println("OPPONENT_GAME_OVER:" + result + ":" + myScore + ":" + opponentScore);
		output.flush();
    }


    // 메시지를 서버로 전송
    public void sendMessage(String message) {
        output.println(message);  // 서버로 메시지 전송
    }
}
