/**
 * Created by арс on 24.07.2017.
 */

import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Recomendations {
    private static final String DATABASE = "first";
    private static final String COLLECTION = "f1";

    private static MongoClient mongoClient;
    private static DB database;
    private static DBCollection collection;

    public static void main(String[] args) {
        try {
            mongoClient = new MongoClient();
            database = mongoClient.getDB(DATABASE);
            collection = database.getCollection(COLLECTION);
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String command = scanner.nextLine();
                executeCommand(command);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    private static void executeCommand(String command) {
        String[] args = command.split(" ");
        switch (args[0]) {
            case "get": {
                int id = Integer.parseInt(args[1]);
                List<Integer> rec = getUserRecomendations(id);
                System.out.println("Recomendations for user " + id + ": " + rec);
                break;
            }
            case "delete": {
                int id = Integer.parseInt(args[1]);
                int delId = Integer.parseInt(args[2]);
                deleteRecomendation(id, delId);
                List<Integer> rec = getUserRecomendations(id);
                System.out.println("Recomendations for user " + id + ": " + rec);
                break;
            }
            default:
                System.out.println("No such command");
        }
    }

    private static void deleteRecomendation(int id, int delId) {
        DBObject query = new BasicDBObject("_id", id);
        DBCursor cursor = collection.find(query);
        List<Integer> rec = (ArrayList<Integer>) cursor.one().get("recomended");
        List<Integer> del = (ArrayList<Integer>) cursor.one().get("deleted");
        if (rec.contains(delId)) {
            rec.remove((Integer)delId);
            del.add(delId);
            DBObject updateQuery = new BasicDBObject("_id", id)
                    .append("recomended", rec)
                    .append("deleted", del);
            collection.update(query, updateQuery);
        }
    }

    private static List<Integer> getUserRecomendations(int id) {
        DBObject query = new BasicDBObject("_id", id);
        DBCursor cursor = collection.find(query);
        return (ArrayList<Integer>) cursor.one().get("recomended");
    }
}
