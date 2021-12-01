package com.example.RDX.app;
import java.io.IOException;
import com.mongodb.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppApplication {

	public static void main(String[] args) {
		try {
			Document doc = Jsoup.connect("https://www.moneycontrol.com/").get();

			System.out.printf("Title: %s\n", doc.title());

			Elements pageElements = doc.select("a[href]");

			MongoClient mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("RDX");
			DBCollection collection = db.getCollection("ContentAndLinks");
			collection.remove(new BasicDBObject());

			BasicDBObject document = new BasicDBObject();

			for (Element e:pageElements) {
				document.put(e.text().replaceAll("[^a-zA-Z0-9]", " "), e.attr("href"));
				}

			collection.insert(document);//Saving Text & Links
			DBCursor cursor = collection.find();
			while(cursor.hasNext()) {
				System.out.println("Saved Text & Links: "+ cursor.next());
			}

			DBCollection collection2 = db.getCollection("Images");
			collection2.remove(new BasicDBObject());

			BasicDBObject document2 = new BasicDBObject();
			Elements images =doc.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
			for (Element image : images) {
				document2.put(image.attr("alt").replaceAll("[^a-zA-Z0-9]", " "), image.attr("src"));

			}

			collection2.insert(document2);//Saving Images' links
			cursor = collection2.find();
			while(cursor.hasNext()) {
				System.out.println("Saved Images' links: "+ cursor.next());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}