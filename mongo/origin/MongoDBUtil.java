package com.example.boottest.demo.utils.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * mongodb-driver 3.6 版本
 * MongoDB工具类 Mongo实例代表了一个数据库连接池，即使在多线程的环境中，一个Mongo实例对我们来说已经足够了
 * 注意Mongo已经实现了连接池，并且是线程安全的。
 * 设计为单例模式， 因 MongoDB的Java驱动是线程安全的，对于一般的应用，只要一个Mongo实例即可，
 * Mongo有个内置的连接池（默认为10个） 对于有大量写和读的环境中，为了确保在一个Session中使用同一个DB时，
 * DB和DBCollection是绝对线程安全的
 *
 * @author zhoulingfei
 * @date 2015-5-29 上午11:49:49
 */
public enum MongoDBUtil {

    /**
     * 定义一个枚举的元素，它代表此类的一个实例
     */
    instance;

    private MongoClient mongoClient;
    /**
     * 获取DB实例 - 指定DB
     */
    private MongoDatabase database;

    private static final Logger logger = LoggerFactory.getLogger("MongoDBUtil");

    static {
        CompositeConfiguration config = new CompositeConfiguration();
        try {
            config.addConfiguration(new PropertiesConfiguration("mongodb.properties"));
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        // 从配置文件中获取属性值
        String host = config.getString("mongodb.host");
        int port = Integer.parseInt(config.getString("mongodb.port").trim());
        String username = config.getString("mongodb.username");
        String password = config.getString("mongodb.password");
        String source = config.getString("mongodb.database");
        int connectTimeout = Integer.parseInt(config.getString("mongodb.options.connectTimeout").trim());
        int maxConnectionIdleTime = Integer.parseInt(config.getString("mongodb.options.maxConnectionIdleTime").trim());
        int maxWaitTime = Integer.parseInt(config.getString("mongodb.options.maxWaitTime").trim());

        ServerAddress serverAddress = new ServerAddress(host, port);

        //MongoCredential.createScramSha1Credential()三个参数分别为 用户名 数据库名称 密码
        MongoCredential credential = MongoCredential.createCredential(username, source, password.toCharArray());

        MongoClientOptions options = MongoClientOptions.builder()
                .connectTimeout(connectTimeout)
                .maxWaitTime(maxWaitTime)
                .maxConnectionIdleTime(maxConnectionIdleTime)
                .build();

        instance.mongoClient = new MongoClient(serverAddress, credential, options);
        instance.database = instance.mongoClient.getDatabase(source);
        logger.info("MongoDBUtil初始化成功...");

    }

    // ------------------------------------共用方法---------------------------------------------------


    /**
     * 获取collection对象 - 指定Collection
     *
     * @param collName
     * @return
     */
    public MongoCollection<Document> getCollection(String collName) {
        if (StringUtils.isEmpty(collName)) {
            return null;
        }
        return database.getCollection(collName);
    }

    /**
     * 删除表
     *
     * @param dbName
     * @param collName
     */
    public void dropCollection(String dbName, String collName) {
        database.getCollection(collName).drop();
    }

    /**
     * 查询DB下的所有表名
     */
    public List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = database.listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * 获取所有数据库名称列表
     *
     * @return
     */
    public MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * 删除一个数据库
     */
    public void dropDB(String dbName) {
        logger.error("危险操作：删除数据库" + dbName + "......想删库到跑路？该方法已被注释......");
//        database.drop();
    }

    /**
     * 查找对象 - 根据主键_id
     *
     * @param coll
     * @param id
     * @return
     */
    public Document findById(MongoCollection<Document> coll, String id) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Document myDoc = coll.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /**
     * 统计数
     */
    public int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }

    /**
     * 条件查询
     */
    public MongoCursor<Document> find(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }

    /**
     * 分页查询
     */
    public MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return coll.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }

    /**
     * 通过ID删除
     *
     * @param coll
     * @param id
     * @return
     */
    public int deleteById(MongoCollection<Document> coll, String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }


    /**
     * 关闭Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * 测试入口
     *
     * @param args
     */
    public static void main(String[] args) {

        String collName = "PushPage";
        MongoCollection<Document> coll = MongoDBUtil.instance.getCollection(collName);
//        FindIterable<Document> iterable = coll.find();
        MongoCursor<Document> mongoCursor = coll.find().iterator();
        while (mongoCursor.hasNext()) {
            System.out.print(mongoCursor.next());
        }
//        while (iterable.)

        // 插入多条
        // for (int i = 1; i <= 4; i++) {
        // Document doc = new Document();
        // doc.put("name", "zhoulf");
        // doc.put("school", "NEFU" + i);
        // Document interests = new Document();
        // interests.put("game", "game" + i);
        // interests.put("ball", "ball" + i);
        // doc.put("interests", interests);
        // coll.insertOne(doc);
        // }

        // // 根据ID查询
        // String id = "556925f34711371df0ddfd4b";
        // Document doc = MongoDBUtil2.instance.findById(coll, id);
        // System.out.println(doc);

        // 查询多个
        // MongoCursor<Document> cursor1 = coll.find(Filters.eq("name", "zhoulf")).iterator();
        // while (cursor1.hasNext()) {
        // org.bson.Document _doc = (Document) cursor1.next();
        // System.out.println(_doc.toString());
        // }
        // cursor1.close();

        // 查询多个
        // MongoCursor<Person> cursor2 = coll.find(Person.class).iterator();

        // 删除数据库
        // MongoDBUtil2.instance.dropDB("testdb");

        // 删除表
        // MongoDBUtil2.instance.dropCollection(dbName, collName);

        // 修改数据
        // String id = "556949504711371c60601b5a";
        // Document newdoc = new Document();
        // newdoc.put("name", "时候");
        // MongoDBUtil.instance.updateById(coll, id, newdoc);

        // 统计表
        // System.out.println(MongoDBUtil.instance.getCount(coll));

        // 查询所有
//        Bson filter = Filters.eq("count", 0);
//        MongoDBUtil.instance.find(coll, filter);

    }

}