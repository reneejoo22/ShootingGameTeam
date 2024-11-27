import java.awt.Image;
import java.awt.Rectangle;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;

public class Monster {
    private Rectangle bounds; // 몬스터의 위치와 크기를 나타내는 Rectangle
    private int health; // 몬스터의 체력 
    private int speedX; // 몬스터의 X 이동 속도
    private int speedY; // 몬스터의 Y 이동 속도
    private int directionX; // X 이동 방향
    private int directionY; // Y 이동 방향
    private Image image; // 몬스터 이미지
    private boolean isAlive; // 몬스터의 생사 여부
    
    //private List<Rectangle> weapons; // 몬스터의 공격 무기 리스트

    // 생성자
    public Monster(int x, int y, int width, int height, int initialHealth, int speedX, int speedY, 
    		Image image) {
    	
    	this.bounds = new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
        this.health = initialHealth;
        //this.monsterAlive = monsterAlive;
        this.speedX = speedX;
        this.speedY = speedY;
        this.directionX = 1;
        this.directionY = 1;
        //this.weapons = new ArrayList<>();
        
        this.image = image;
        this.isAlive = true;
    
    }
/*
    // 무기 발사 메소드
    public void spawnWeapon(Image monsterWeaponImage) {
        Rectangle weapon = new Rectangle(bounds.x + bounds.width / 2 - monsterWeaponImage.getWidth(null) / 2,
                                         bounds.y + bounds.height,
                                         monsterWeaponImage.getWidth(null),
                                         monsterWeaponImage.getHeight(null));
        weapons.add(weapon);
    }

    /*
    // 무기 이동 메소드
    public void updateWeapons(int screenHeight) {
        for (Iterator<Rectangle> it = weapons.iterator(); it.hasNext();) {
            Rectangle weapon = it.next();
            weapon.y += 5; // 무기 속도
            if (weapon.y > screenHeight) {
                it.remove(); // 화면 밖으로 나가면 제거
            }
        }
    }
    */
 
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
    
    // 몬스터의 위치를 업데이트하는 메서드 (상하좌우)
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

            // 일정 확률로 랜덤한 방향으로 변경
            if (Math.random() < 0.01) { // 1% 확률로 방향 변경
                directionX = (Math.random() < 0.5) ? 1 : -1; // 랜덤 X 방향
                directionY = (Math.random() < 0.5) ? 1 : -1; // 랜덤 Y 방향
            }
        }
    }
    
    // 몬스터와 미사일의 충돌 검사
    public boolean checkCollision(Rectangle missile) {
        return bounds.intersects(missile);
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

/*
    // 몬스터가 죽었는지 여부 반환
    public boolean isDead() {
        return health <= 0;
    }
*/
    public Rectangle getBounds() {
        return bounds;
    }

    /*
    public List<Rectangle> getWeapons() {
        return weapons;
    }
    */
}

