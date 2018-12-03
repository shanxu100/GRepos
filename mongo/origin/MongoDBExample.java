package com.example.boottest.demo.utils.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * 增删改查的代码可以参考此文档
 *
 * @author Guan
 * @date Created on 2018/11/23
 */
public class MongoDBExample {


    /**
     * 设置数据库连接地址
     */
    private final String CONN_HOST = "127.0.0.1";

    /**
     * 设置数据库连接端口号
     */
    private final int CONN_PORT = 27017;

    /**
     * MongoDB连接实例
     */
    public MongoClient mongoClient = null;

    /**
     * MongoDB数据库实例
     */
    public MongoDatabase mongoDatabase = null;

    /**
     * 构造方法
     * 获取数据库实例
     *
     * @param DB_Name
     */
    public MongoDBExample(String DB_Name) {
        this.mongoClient = new MongoClient(CONN_HOST, CONN_PORT);
        this.mongoDatabase = this.mongoClient.getDatabase(DB_Name);
    }

    /**
     * 创建数据库集合
     *
     * @param collName 数据库表名
     */
    public boolean createCollection(String collName) {
        try {
            this.mongoDatabase.createCollection(collName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取数据库集合
     *
     * @param collName
     * @return
     */
    public MongoCollection<Document> getCollection(String collName) {
        return this.mongoDatabase.getCollection(collName);
    }

    /**
     * 插入单个文档
     *
     * @param doc      Bson文档
     * @param collName 集合名称
     */
    public void insert(Document doc, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        coll.insertOne(doc);
    }

    /**
     * 批量插入文档
     *
     * @param list     List类型文档
     * @param collName 集合名称
     */
    public void insert(List<Document> list, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        coll.insertMany(list);
    }

    /**
     * 查找集合内所有Document
     *
     * @param collName
     * @return
     */
    public List<Document> findAll(String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        List<Document> result = new ArrayList<Document>();
        FindIterable<Document> findIterable = coll.find();
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }

    /**
     * 指定条件查找
     *
     * @param query
     * @param collName
     * @return
     */
    public List<Document> findAll(BasicDBObject query, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        List<Document> result = new ArrayList<Document>();
        FindIterable<Document> findIterable = coll.find(query);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }


    /**
     * 指定条件查找指定字段
     *
     * @param query
     * @param collName
     * @return
     */
    public List<Document> findAll(BasicDBObject query, BasicDBObject key, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        List<Document> result = new ArrayList<Document>();
        FindIterable<Document> findIterable = coll.find(query).projection(key);
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        while (mongoCursor.hasNext()) {
            result.add(mongoCursor.next());
        }
        return result;
    }


    /**
     * 查找一个
     *
     * @param query
     * @param collName
     * @return
     */
    public Document findOne(BasicDBObject query, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        Document result = new Document();
        FindIterable<Document> findIterable = coll.find(query).limit(1);
        result = findIterable.iterator().next();
        return result;
    }


    /**
     * 删除集合中的所有数据
     *
     * @param collName
     */
    public void deleteAll(String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        BasicDBObject delDbo = new BasicDBObject();
        delDbo.append("_id", -1);
        coll.deleteMany(Filters.not(delDbo));
    }


    /**
     * 删除指定的所有数据
     *
     * @param b
     * @param collName
     */
    public void deleteAll(Bson b, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        coll.deleteMany(b);
    }


    /**
     * 删除指定的一条数据
     *
     * @param b
     * @param collName
     */
    public void deleteOne(Bson b, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        coll.deleteOne(b);
    }


    //collection.updateMany(Filters.eq("likes", 100), new Document("$set", new Document("likes",200)) );

    /**
     * 按查询条件批量修改
     *
     * @param b
     * @param doc
     * @param collName
     */
    public void updateAll(Bson b, Document doc, String collName) {
        MongoCollection<Document> coll = this.mongoDatabase.getCollection(collName);
        coll.updateMany(b, doc);


    }


}
