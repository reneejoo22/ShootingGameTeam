//GameWindow.java
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
        setSize(700, 800);
        setLocationRelativeTo(null);

        // 배경 이미지 설정
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout()); // 배경 패널에 GridBagLayout 설정

        // 버튼 생성
        onePlayerButton = new JButton("혼자 플레이하기");
        twoPlayerButton = new JButton("두 명 플레이하기");

        // GridBagConstraints 설정
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(45, 10, 10, 10); // 위쪽 여백을 줄여서 버튼을 위로 이동
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER; // 중앙 정렬

        // 버튼을 배경 패널에 추가
        backgroundPanel.add(onePlayerButton, gbc);

        gbc.gridy = 1;
        backgroundPanel.add(twoPlayerButton, gbc);

        // 버튼 클릭 시 동작 설정
        onePlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 혼자 플레이 버튼 클릭 시 ShootingGame1을 실행
                launchShootingGame1("1", "0", 0);
            }
        });

        twoPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 두 명 플레이 버튼 클릭 시, 두 명 플레이 입력 창을 띄운다.
                showTwoPlayerInputWindow();
            }
        });

        // 배경 패널을 프레임의 콘텐츠 패널로 설정
        setContentPane(backgroundPanel);

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

        JButton confirmButton = new JButton("연결");
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

                try {
                    // 서버 생성 및 실행
                    int port = Integer.parseInt(portNumber);
                    Server server = new Server(port);
                    new Thread(server::start).start(); // 서버 비동기로 시작

                    // 서버가 준비될 시간을 약간 기다림 (선택적)
                    Thread.sleep(500);

                    // 슈팅 게임 화면 시작
                    launchShootingGame1(playerId, ipAddress, port);

                } catch (NumberFormatException ex) {
                    System.err.println("유효하지 않은 포트 번호입니다.");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                twoPlayerFrame.dispose(); // 입력 창 닫기
            }
        });

        twoPlayerFrame.add(panel);
        twoPlayerFrame.setVisible(true);
    }

    // 게임 시작 메서드
    private void launchShootingGame1(String playerId, String ipAddress, int port) {
        dispose(); // 현재 창을 닫고
        SwingUtilities.invokeLater(() -> new MainFrame(playerId, ipAddress, port)); // MainFrame에 클라이언트 정보 전달
    }

    // 배경 이미지를 표시하는 패널
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            backgroundImage = new ImageIcon("images/title.png").getImage(); // 이미지 경로 설정
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // 배경 이미지 그리기
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameWindow()); // 게임 창 실행
    }
}
