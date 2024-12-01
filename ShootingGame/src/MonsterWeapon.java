import java.awt.Rectangle;

public class MonsterWeapon {
    private Rectangle bounds; // 무기의 위치와 크기
    private final int speed; // 무기 속도

    public MonsterWeapon(int x, int y, int width, int height, int speed) {
        this.bounds = new Rectangle(x, y, width, height);
        this.speed = speed;
    }

    public void updatePosition() {
        bounds.y += speed; // 아래로 이동
    }

    public Rectangle getBounds() {
        return bounds;
    }
    
    public boolean isOffScreen(int height) {
        return bounds.y > height; // 화면 아래로 나갔는지 확인
    }
}
