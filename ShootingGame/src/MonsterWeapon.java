//MonsterWeapon.java
import java.awt.Image;
import java.awt.Rectangle;

public class MonsterWeapon {
    private int x, y; // 위치
    private final int speed; // 이동 속도
    private final Image image; // 이미지

    public MonsterWeapon(int x, int y, int speed, Image image) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.image = image;
    }

    public void move() {
        y += speed; // 아래로 이동
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Image getImage() { return image; }
}