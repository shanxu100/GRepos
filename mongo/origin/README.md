# 写在前面
- mongodb-driver 3.0以上版本
- MongoDBUtil封装了常用方法
- MongoDBExample文件列举了3.0 API的常用例子


# 对于NumberLong类型的数据转Json的异常
增加Json设置：
``` java
    /**
     * 将数据库中 NumberLong 类型的值转换成java可识别的long类型
     */
    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder().int64Converter(new Converter<Long>() {
        @Override
        public void convert(Long value, StrictJsonWriter writer) {
            writer.writeNumber(value + "");
        }
    }).build();

```
