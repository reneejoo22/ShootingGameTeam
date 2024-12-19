//MainFrame.java
import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private ShootingGame1 gamePanel; // 게임 화면
    private JPanel chatPanel; // 채팅 패널
    private JTextArea chatArea; // 채팅 기록을 표시할 영역
    private JTextField chatInput; // 채팅 입력창
    private JButton sendButton; // 메시지 전송 버튼
    
    private boolean isChatting = false; // 채팅 모드 여부
    
    private Clip backgroundMusic; // 음악 클립 객체
    private boolean isMusicOn = true; // 음악이 켜져 있는지 확인하는 변수
    private JMenuItem musicMenuItem; // 메뉴 항목

    public MainFrame() {
        setTitle("Shooting Game with Chat");
        setSize(700, 800); // 전체 창 크기 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 게임 패널 초기화
        gamePanel = new ShootingGame1(this);
        gamePanel.setFocusable(true); // 게임 패널에서 키 입력 받기
        
        // 채팅 패널 초기화
        chatPanel = new JPanel();
        chatPanel.setLayout(new BorderLayout());

        // 채팅 영역 설정
        chatArea = new JTextArea();
        chatArea.setEditable(false); // 읽기 전용
        JScrollPane scrollPane = new JScrollPane(chatArea); // 스크롤 추가
        chatPanel.add(scrollPane, BorderLayout.CENTER);

        // 채팅 입력창과 버튼 설정
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        chatInput = new JTextField();
        sendButton = new JButton("Send");
        inputPanel.add(chatInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);

        // 버튼 클릭 이벤트 처리
        sendButton.addActionListener(e -> sendMessage());
        
        // 버튼 클릭 이벤트 처리
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // 채팅 입력창에 포커스 이벤트 추가
        chatInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isChatting = true; // 채팅 모드 활성화
            }

            @Override
            public void focusLost(FocusEvent e) {
                isChatting = false; // 채팅 모드 비활성화
            }
        });

        // JSplitPane을 사용해 게임 패널과 채팅 패널 나란히 배치
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, gamePanel, chatPanel);
        splitPane.setDividerLocation(400); // 초기 분할 위치 설정
        splitPane.setResizeWeight(0.7); // 게임 패널 비율을 더 크게 설정

        add(splitPane, BorderLayout.CENTER);

        // 게임 화면 클릭 시 자동으로 포커스가 게임 화면으로 가도록 수정
        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 게임 화면을 클릭하면 자동으로 게임 화면으로 돌아가기
                gamePanel.requestFocusInWindow(); // 게임 화면에 포커스를 설정
            }
        });

        // 채팅 입력창에 포커스가 있을 때만 키 이벤트 처리
        chatInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isChatting = true; // 채팅 모드 활성화
            }

            @Override
            public void focusLost(FocusEvent e) {
                isChatting = false; // 채팅 모드 비활성화
            }
        });

        // 게임 화면에서 특정 키 입력 시 채팅 모드 종료
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { // ESC 키를 누르면 채팅 모드 종료
                    if (isChatting) {
                        sendMessage(); // 채팅 메시지를 보내고
                        chatInput.setFocusable(false); // 채팅 입력창 비활성화
                        gamePanel.requestFocusInWindow(); // 게임 화면에 포커스 설정
                        isChatting = false; // 채팅 종료
                    }
                }
            }
        });
        
        // 게임 패널이 포커스를 받을 수 있도록 명시적으로 설정
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow(); // 처음에 포커스를 게임 화면으로 설정
        
        // 메뉴바 생성
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("음악");

        // 음악 메뉴 항목 생성
        musicMenuItem = new JMenuItem("음악 끄기");
        musicMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMusic();
            }
        });

        gameMenu.add(musicMenuItem);
        menuBar.add(gameMenu);
        setJMenuBar(menuBar);

        // 화면이 켜지자마자 배경 음악을 재생
        playBackgroundMusic();
        
        setVisible(true);
    }

    // 음악 켜기/끄기
    private void toggleMusic() {
        if (isMusicOn) {
            stopBackgroundMusic();
            musicMenuItem.setText("음악 켜기"); // 메뉴 항목 텍스트 변경
        } else {
            playBackgroundMusic();
            musicMenuItem.setText("음악 끄기"); // 메뉴 항목 텍스트 변경
        }
        isMusicOn = !isMusicOn; // 음악 상태 토글
    }

    // 배경 음악 재생
    private void playBackgroundMusic() {
        try {
            File musicFile = new File("music/bgMusic.wav"); // 음악 파일 경로
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-35.0f); // 볼륨을 줄임
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY); // 반복 재생
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // 배경 음악 중지
    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    // 메시지 전송 메서드
    private void sendMessage() {
        String message = chatInput.getText().trim();
        if (!message.isEmpty()) {
            chatArea.append("Player: " + message + "\n");
            chatInput.setText(""); // 입력창 초기화
        }
    }

    // MainFrame 클래스에 메시지 출력 메서드 추가
    public void appendChatMessage(String message) {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}