package com.jcodes.mongodb;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/**
 * Java MongoDB
 * 
 * http://blog.csdn.net/hx_uestc/article/details/7620938
 */
public class MongodbAppExample {

	public static void insertDocumentTest() {
		try {
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("test");
			// get a single collection
			DBCollection collection = db.getCollection("dummyColl");

			// BasicDBObject example
			System.out.println("BasicDBObject example...");
			BasicDBObject document = new BasicDBObject();
			document.put("database", "mkyongDB");
			document.put("table", "hosting");
			BasicDBObject documentDetail = new BasicDBObject();
			documentDetail.put("records", "99");
			documentDetail.put("index", "vps_index1");
			documentDetail.put("active", "true");
			document.put("detail", documentDetail);
			collection.insert(document);
			DBCursor cursorDoc = collection.find();
			while (cursorDoc.hasNext()) {
				System.out.println(cursorDoc.next());
			}
			collection.remove(new BasicDBObject());

			// BasicDBObjectBuilder example
			System.out.println("BasicDBObjectBuilder example...");
			BasicDBObjectBuilder documentBuilder = BasicDBObjectBuilder.start().add("database", "mkyongDB").add("table", "hosting");
			BasicDBObjectBuilder documentBuilderDetail = BasicDBObjectBuilder.start().add("records", "99").add("index", "vps_index1").add("active", "true");
			documentBuilder.add("detail", documentBuilderDetail.get());
			collection.insert(documentBuilder.get());
			DBCursor cursorDocBuilder = collection.find();
			while (cursorDocBuilder.hasNext()) {
				System.out.println(cursorDocBuilder.next());
			}
			collection.remove(new BasicDBObject());

			// Map example
			System.out.println("Map example...");
			Map documentMap = new HashMap();
			documentMap.put("database", "mkyongDB");
			documentMap.put("table", "hosting");
			Map documentMapDetail = new HashMap();
			documentMapDetail.put("records", "99");
			documentMapDetail.put("index", "vps_index1");
			documentMapDetail.put("active", "true");
			documentMap.put("detail", documentMapDetail);
			collection.insert(new BasicDBObject(documentMap));
			DBCursor cursorDocMap = collection.find();
			while (cursorDocMap.hasNext()) {
				System.out.println(cursorDocMap.next());
			}
			collection.remove(new BasicDBObject());

			// JSON parse example
			System.out.println("JSON parse example...");
			String json = "{'database' : 'mkyongDB','table' : 'hosting'," + "'detail' : {'records' : 99, 'index' : 'vps_index1', 'active' : 'true'}}}";
			DBObject dbObject = (DBObject) JSON.parse(json);
			collection.insert(dbObject);
			DBCursor cursorDocJSON = collection.find();
			while (cursorDocJSON.hasNext()) {
				System.out.println(cursorDocJSON.next());
			}
			collection.remove(new BasicDBObject());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public static void printAllDocuments(DBCollection collection) {
		DBCursor cursor = collection.find();
		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
	}

	public static void removeAllDocuments(DBCollection collection) {
		collection.remove(new BasicDBObject());
	}

	public static void insertDummyDocuments(DBCollection collection) {
		BasicDBObject document = new BasicDBObject();
		document.put("hosting", "hostA");
		document.put("type", "vps");
		document.put("clients", 1000);
		BasicDBObject document2 = new BasicDBObject();
		document2.put("hosting", "hostB");
		document2.put("type", "dedicated server");
		document2.put("clients", 100);
		BasicDBObject document3 = new BasicDBObject();
		document3.put("hosting", "hostC");
		document3.put("type", "vps");
		document3.put("clients", 900);
		collection.insert(document);
		collection.insert(document2);
		collection.insert(document3);
	}

	public static void updateDocumentTest() {
		try {
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("yourdb");
			DBCollection collection = db.getCollection("dummyColl");

			System.out.println("Testing 1...");
			insertDummyDocuments(collection);
			// find hosting = hostB, and update it with new document
			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("hosting", "hostB");
			newDocument.put("type", "shared host");
			newDocument.put("clients", 111);
			collection.update(new BasicDBObject().append("hosting", "hostB"), newDocument);
			printAllDocuments(collection);
			removeAllDocuments(collection);

			System.out.println("Testing 2...");
			insertDummyDocuments(collection);
			BasicDBObject newDocument2 = new BasicDBObject().append("$inc", new BasicDBObject().append("clients", 99));
			collection.update(new BasicDBObject().append("hosting", "hostB"), newDocument2);
			printAllDocuments(collection);
			removeAllDocuments(collection);

			System.out.println("Testing 3...");
			insertDummyDocuments(collection);
			BasicDBObject newDocument3 = new BasicDBObject().append("$set", new BasicDBObject().append("type", "dedicated server"));
			collection.update(new BasicDBObject().append("hosting", "hostA"), newDocument3);
			printAllDocuments(collection);
			removeAllDocuments(collection);

			System.out.println("Testing 4...");
			insertDummyDocuments(collection);
			BasicDBObject updateQuery = new BasicDBObject().append("$set", new BasicDBObject().append("clients", "888"));
			collection.update(new BasicDBObject().append("type", "vps"), updateQuery, false, true);
			printAllDocuments(collection);
			removeAllDocuments(collection);
			System.out.println("Done");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		insertDocumentTest();
		updateDocumentTest();
	}

}