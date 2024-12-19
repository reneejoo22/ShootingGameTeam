import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Player {
    private int x, y; // 위치
    private int health; // 체력 
    private final Image image; // 이미지
    private final int speed = 5; // 이동 속도
    private int ammo = 6; // 총알 개수
    private boolean reloading = false; // 재장전 여부
    private long reloadStartTime; // 재장전 시작 시간

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

        // 재장전 상태일 때 재장전 완료 여부 확인
        if (reloading && System.currentTimeMillis() - reloadStartTime >= 2000) {
            reloading = false;
            ammo = 6;
        }
    }

    public void clampPosition(int minX, int maxX, int minY, int maxY) {
        x = Math.max(x, minX);
        x = Math.min(x, maxX - image.getWidth(null));
        y = Math.max(y, minY);
        y = Math.min(y, maxY - image.getHeight(null));
    }

    public void decreaseHealth() { 
        health--; 
        playSound("music/플레이어 신음.wav", -10.0f); // Reduce volume by 10 decibels
    }

    public void deadHealth() { health = 0; }
    public void restoreHealth() { health = 3; }

    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return image.getWidth(null); }
    public int getHeight() { return image.getHeight(null); }
    public int getHealth() { return health; }
    public int getAmmo() { return ammo; }
    public boolean isReloading() { return reloading; }

    public boolean shoot() {
        if (ammo > 0 && !reloading) {
            ammo--;
            playSound("music/총소리.wav", -10.0f); // Reduce volume by 10 decibels
            if (ammo == 0) {
                startReloading(); // 재장전 시작
            }
            return true;
        } else if (ammo == 0) {
            startReloading(); // 재장전 시작
        }
        return false;
    }

    public void startReloading() {
        reloading = true;
        reloadStartTime = System.currentTimeMillis();
        playSound("music/재장전.wav", -10.0f); // 재장전 시작할 때 즉시 소리 재생
    }

    private void playSound(String filePath, float volume) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(new File(filePath)));
            
            // Adjust the volume
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volume); // volume is a value in decibels (negative values reduce the volume)

            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}