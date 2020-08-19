package game.model.logger;

public class Logger {
    private long viewStart;
    private long modelStart;

    public void logTimeBetweenModelStarts(long startNext) {
        if (modelStart != 0) {
            long dif = startNext - modelStart;
            if (dif > 20) {
                System.out.println("Model: millis between loop starts: " + dif);
            }
        }
        modelStart = startNext;
    }

    public void logTimeOfModelLoopDuration(long dif) {
        if (dif > 20) {
            System.out.println("Model: millis loop duration: " + dif);
        }
    }

    public void logTimeBetweenViewStarts() {
        long startNext = System.currentTimeMillis();
        if (viewStart != 0) {
            long dif = startNext - viewStart;
            if (dif > 30) {
                System.out.println("View: millis between loop starts: " + dif);
            }
        }
        viewStart = startNext;
    }

    public void logTimeOfViewLoopDuration() {
        long end = System.currentTimeMillis();
        if (viewStart != 0) {
            long dif = end - viewStart;
            if (dif > 20) {
                System.out.println("View: millis loop duration: " + dif);
            }
        }
    }
}
