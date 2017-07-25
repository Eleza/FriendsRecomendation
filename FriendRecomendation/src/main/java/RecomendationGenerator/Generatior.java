package RecomendationGenerator;

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by арс on 24.07.2017.
 */
public class Generatior {

    private static final String DATABASE = "recomendations";
    private static final String COLLECTION = "user";
    private static final int REC_SIZE = 10;

    private int count;
    private MongoClient mongoClient;
    private DB database;
    private DBCollection collection;
    private Random rnd;

    public Generatior() {
        rnd = new Random();
    }

    public void generate(int usersCount) {
        try {
            mongoClient = new MongoClient();
            database = mongoClient.getDB(DATABASE);
            collection = database.getCollection(COLLECTION);
            clearCollection();
            int size = Math.min(REC_SIZE, usersCount);
            for (int i = 0; i < usersCount; i++) {
                List<Integer> recomended = new ArrayList<Integer>();
                List<Integer> deleted = new ArrayList<Integer>();
                generateLists(recomended, deleted, usersCount, i + 1, size);
                insertRecom(i + 1, recomended, deleted);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void clearCollection() {
        DBObject query = new BasicDBObject();
        collection.remove(query);
    }

    private void insertRecom(int userId, List<Integer> rec, List<Integer> del) {
        DBObject userRec = new BasicDBObject("_id", userId)
                .append("recomended", rec)
                .append("deleted", del);
        collection.insert(userRec);
    }

    private void generateLists(List<Integer> rec, List<Integer> del, int userCount, int userId, int size) {
        Set<Integer> usedIds = new HashSet<Integer>();
        usedIds.add(userId);
        for (int i = 0; i < size; i++) {
            int id = rnd.nextInt(userCount) + 1;
            while (usedIds.contains(id)) {
                id = rnd.nextInt(userCount) + 1;
            }
            usedIds.add(id);
            rec.add(id);
        }
        int delCount = rnd.nextInt(userCount - size);
        for (int i = 0; i < delCount; i++) {
            int id = rnd.nextInt(userCount) + 1;
            while (usedIds.contains(id)) {
                id = rnd.nextInt(userCount) + 1;
            }
            usedIds.add(id);
            del.add(id);
        }
    }

    public static void main(String[] args) {
        new Generatior().generate(20);

    }


}
