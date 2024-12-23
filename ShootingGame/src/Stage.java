//Stage.java
public class Stage {
    private int stageNumber; // 스테이지 번호  
    private int timeLimit; // 각 스테이지의 시간 제한 (초 단위)
    private boolean isCleared; // 스테이지 클리어 여부

    public Stage(int stageNumber, int timeLimit) {
        this.stageNumber = stageNumber;
        this.timeLimit = timeLimit;
        this.isCleared = false;
    }

    public int getStageNumber() {
        return stageNumber;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public boolean isCleared() {
        return isCleared;
    }

    public void setCleared(boolean cleared) {
        isCleared = cleared;
    }

    // 스테이지 시간 경과 체크
    public void updateStageTime(int elapsedTime) {
        if (elapsedTime >= timeLimit) {
            setCleared(true);
        }
    }
}