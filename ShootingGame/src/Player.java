import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Player {
    private int x, y; // 위치
    private int health; // 체력
    private final Image image; // 이미지
    private final int speed = 5; // 이동 속도

    public Player(int startX, int startY, Image playerImage, int initialHealth) {
        this.x = startX;
        this.y = startY;
        this.image = playerImage;
        this.health = initialHealth;
    }

    public void moveLeft() { x -= speed; }
    public void moveRight() { x += speed; }
    public void moveUp() { y -= speed; }
    public void moveDown() { y += speed; }

    public void updatePosition(boolean[] keys, int widthLimit, int heightLimit) {
        if (keys[KeyEvent.VK_LEFT]) moveLeft();
        if (keys[KeyEvent.VK_RIGHT]) moveRight();
        if (keys[KeyEvent.VK_UP]) moveUp();
        if (keys[KeyEvent.VK_DOWN]) moveDown();

        // 경계 체크
        x = Math.max(0, Math.min(x, widthLimit - getWidth()));
        y = Math.max(0, Math.min(y, heightLimit - getHeight()));
    }
    
    public void clampPosition(int minX, int maxX, int minY, int maxY) {
        x = Math.max(x, minX);
        x = Math.min(x, maxX - image.getWidth(null));
        y = Math.max(y, minY);
        y = Math.min(y, maxY - image.getHeight(null));
    }

    public void decreaseHealth() { health--; }
    public void deadHealth() { health = 0; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return image.getWidth(null); }
    public int getHeight() { return image.getHeight(null); }
    public int getHealth() { return health; }
}
