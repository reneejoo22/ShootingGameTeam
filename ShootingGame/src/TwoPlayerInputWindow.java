import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TwoPlayerInputWindow {
    public TwoPlayerInputWindow() {
        // JFrame 생성
        JFrame frame = new JFrame("Two Player Setup");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // 창을 닫을 때 종료되지 않도록
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);

        // 패널 생성
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2));  // 입력 창을 세로로 정렬

        // 아이디 입력 필드
        JLabel idLabel = new JLabel("Player ID:");
        JTextField idField = new JTextField();
        panel.add(idLabel);
        panel.add(idField);

        // IP 주소 입력 필드
        JLabel ipLabel = new JLabel("IP Address:");
        JTextField ipField = new JTextField();
        panel.add(ipLabel);
        panel.add(ipField);

        // 포트 번호 입력 필드
        JLabel portLabel = new JLabel("Port Number:");
        JTextField portField = new JTextField();
        panel.add(portLabel);
        panel.add(portField);

        // 확인 버튼
        JButton confirmButton = new JButton("Confirm");
        panel.add(new JLabel());  // 빈 공간
        panel.add(confirmButton);

        // 버튼 클릭 시 처리
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 입력된 값 가져오기
                String playerId = idField.getText();
                String ipAddress = ipField.getText();
                String portNumber = portField.getText();

                // 입력된 값 출력
                System.out.println("Player ID: " + playerId);
                System.out.println("IP Address: " + ipAddress);
                System.out.println("Port Number: " + portNumber);

                // 예시: 다음 창으로 이동하거나, 게임을 시작하는 로직 추가 가능
                frame.dispose(); // 창을 닫고 진행 (필요에 따라 변경)
            }
        });

        // 프레임에 패널 추가
        frame.add(panel);
        frame.setVisible(true); // 창을 화면에 표시
    }
}
