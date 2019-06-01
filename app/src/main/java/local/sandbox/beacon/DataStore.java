package local.sandbox.beacon;

import java.util.concurrent.ConcurrentHashMap;

public class DataStore {
    private static final ConcurrentHashMap<String, DataEntity> db = new ConcurrentHashMap<>();

    public static void save(String userId, DataEntity entity) {
        db.put(userId, entity);
    }

    public static void regionExited(String userId) {
        db.get(userId).setState(Status.OUTSIDE);
    }

    public static DataEntity find(String userId) {
        return db.get(userId);
    }
}
