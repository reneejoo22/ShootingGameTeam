//Monster.java
import java.awt.Image;
import java.awt.Rectangle;

public class Monster {
    private Rectangle bounds; // 몬스터의 위치와 크기를 나타내는 Rectangle
    private int health; // 몬스터의 체력 
    private int speedX; // 몬스터의 X 이동 속도
    private int speedY; // 몬스터의 Y 이동 속도
    private int directionX; // X 이동 방향
    private int directionY; // Y 이동 방향
    private Image image; // 몬스터 이미지
    private boolean isAlive; // 몬스터의 생사 여부

    // 생성자
    public Monster(int x, int y, int width, int height, int initialHealth, int speedX, int speedY, Image image) {
        this.bounds = new Rectangle(x, y, width, height);
        this.health = initialHealth;
        this.speedX = speedX;
        this.speedY = speedY;
        this.directionX = 1;
        this.directionY = 1;
        this.image = image;
        this.isAlive = true;
    }

    // 몬스터 좌우로만 위치를 업데이트하는 메소드
    public void updatePosition(int screenWidth) {
        if (isAlive) {
            bounds.x += speedX * directionX;

            // 화면 경계를 넘어가면 방향 전환
            if (bounds.x <= 0 || bounds.x + bounds.width >= screenWidth) {
                directionX *= -1; // 방향 반전
            }
        }
    }

    // 몬스터의 위치를 업데이트하는 메서드 (상하좌우 규칙적)
    public void updatePosition2(int screenWidth, int screenHeight) {
        if (isAlive) {
            // 몬스터의 X와 Y 위치 업데이트
            bounds.x += speedX * directionX;
            bounds.y += speedY * directionY;

            // 화면 경계에 도달하면 방향 전환
            if (bounds.x <= 0 || bounds.x + bounds.width >= screenWidth) {
                directionX *= -1; // X 방향 반전
            }
            if (bounds.y <= 0 || bounds.y + bounds.height >= screenHeight) {
                directionY *= -1; // Y 방향 반전
            }
        }
    }

 // 몬스터의 위치를 업데이트하는 메서드 (상하좌우 불규칙적)
    public void updatePosition3(int screenWidth, int screenHeight) {
        if (isAlive) {
            // 몬스터의 X와 Y 위치 업데이트
            bounds.x += speedX * directionX;
            bounds.y += speedY * directionY;

            // 화면 경계에 도달하면 방향 전환
            if (bounds.x <= 0 || bounds.x + bounds.width >= screenWidth) {
                directionX *= -1; // X 방향 반전
            }
            if (bounds.y <= 0 || bounds.y + bounds.height >= screenHeight) {
                directionY *= -1; // Y 방향 반전
            }

            // 일정 확률로 랜덤한 방향으로 변경
            if (Math.random() < 0.05) { // 5% 확률로 방향 변경
                directionX = (Math.random() < 0.5) ? 1 : -1; // 랜덤 X 방향
                directionY = (Math.random() < 0.5) ? 1 : -1; // 랜덤 Y 방향
            }

            // 추가로 불규칙한 움직임을 위해 속도 변경
            if (Math.random() < 0.05) { // 5% 확률로 속도 변경
                speedX = (int) (Math.random() * 5) + 1; // 1부터 5까지 랜덤 속도
                speedY = (int) (Math.random() * 5) + 1; // 1부터 5까지 랜덤 속도
            }
        }
    }

    // 몬스터와 미사일의 충돌 검사
    public boolean checkCollision(Rectangle missile) {
        boolean collision = bounds.intersects(missile);
        System.out.println("Checking collision: " + collision + " with missile: " + missile); // 디버깅 정보 추가
        return collision;
    }

    // 몬스터 체력 감소
    public void decreaseHealth() {
        if (health > 0) {
            health--;
        }
        // 체력이 0 이하일 때 몬스터 죽음 처리
        isAlive = health > 0; // health가 0 이하면 isAlive는 false
    }

    // 몬스터가 살아있는지 확인
    public boolean isAlive() {
        return isAlive; // 단순히 isAlive 반환
    }

    // 몬스터가 살아있는지 확인
    public boolean killMonster() {
        return isAlive = false; // 단순히 isAlive 반환
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public Image getImage() {
        return image;
    }
}