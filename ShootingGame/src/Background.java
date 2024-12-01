import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Background {
    private Image image; // 배경 이미지
    private int y; // Y 좌표

    public Background(String imagePath) {
        image = new ImageIcon(imagePath).getImage();
        y = 0; // 초기 Y 좌표
    }

    public void update() {
        y += 1; // 배경을 아래로 스크롤
        if (y > 0) {
            y = -image.getHeight(null) + 800; // 화면이 아래로 내려가면 다시 위로 설정
        }
    }

    public void draw(Graphics g) {
        g.drawImage(image, 0, y, null); // 배경 이미지 그리기
        g.drawImage(image, 0, y - image.getHeight(null), null); // 배경 이미지 반복 그리기
    }
}
