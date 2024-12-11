import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    private JButton onePlayerButton;
    private JButton twoPlayerButton;

    public GameWindow() {
        // 기본 프레임 설정
        setTitle("Shooting Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new FlowLayout());

        // 버튼 생성
        onePlayerButton = new JButton("혼자 플레이하기");
        twoPlayerButton = new JButton("두 명 플레이하기");

        // 버튼 클릭 시 동작 설정
        onePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 혼자 플레이 버튼 클릭 시 ShootingGame1을 실행
                launchShootingGame1();
            }
        });

        twoPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 두 명 플레이 버튼 클릭 시, 두 명 플레이 입력 창을 띄운다.
                showTwoPlayerInputWindow();
            }
        });

        // 버튼을 프레임에 추가
        add(onePlayerButton);
        add(twoPlayerButton);

        setVisible(true);
    }

    // 두 명 플레이 입력 창을 띄우는 메서드
    private void showTwoPlayerInputWindow() {
        JFrame twoPlayerFrame = new JFrame("Two Player Setup");
        twoPlayerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        twoPlayerFrame.setSize(400, 300);
        twoPlayerFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));

        JLabel idLabel = new JLabel("Player ID:");
        JTextField idField = new JTextField();
        panel.add(idLabel);
        panel.add(idField);

        JLabel ipLabel = new JLabel("IP Address:");
        JTextField ipField = new JTextField();
        panel.add(ipLabel);
        panel.add(ipField);

        JLabel portLabel = new JLabel("Port Number:");
        JTextField portField = new JTextField();
        panel.add(portLabel);
        panel.add(portField);

        JButton confirmButton = new JButton("Confirm");
        panel.add(new JLabel());
        panel.add(confirmButton);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerId = idField.getText();
                String ipAddress = ipField.getText();
                String portNumber = portField.getText();

                System.out.println("Player ID: " + playerId);
                System.out.println("IP Address: " + ipAddress);
                System.out.println("Port Number: " + portNumber);

                // 예시: 입력 값을 처리하여 실제 게임을 시작하거나 서버와 연결하는 로직을 추가 가능
                twoPlayerFrame.dispose(); // 입력 창 닫기
            }
        });

        twoPlayerFrame.add(panel);
        twoPlayerFrame.setVisible(true);
    }

    // 혼자 플레이하기 버튼을 클릭하면 ShootingGame1을 실행하는 메서드
    private void launchShootingGame1() {
        JFrame frame = new JFrame("Shooting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ShootingGame1 gamePanel = new ShootingGame1();
        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
/*
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameWindow(); // 게임 창 실행
            }
        });
    }

 */
}
