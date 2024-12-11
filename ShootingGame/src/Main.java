import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // 게임 화면 실행
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameWindow();  // 게임 창을 시작하는 객체
            }
        });
    }
}
