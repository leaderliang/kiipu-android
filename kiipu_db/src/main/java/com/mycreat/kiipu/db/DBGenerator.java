package com.mycreat.kiipu.db;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

import java.io.File;

/**
 * Created by zhanghaihai on 2017/7/5.
 */

public class DBGenerator {

    public static void main(String[]args) throws Exception{
        int version = 1;
        String defaultBeanPackage = DBGenerator.class.getPackage().getName() + ".bookmark.model";
        String defaultPackage = DBGenerator.class.getPackage().getName() + ".bookmark";

        Schema schema = new Schema(version, defaultBeanPackage); //创建模式对象，指定版本号和bean对象包名
        addEntities(schema); //添加实体

        schema.setDefaultJavaPackageDao(defaultPackage); //指定自动生成的dao对象的包名
        String outDir= System.getProperty("user.dir") + "$kiipu_db$src$main$java-gen".replace("$", File.separator);
        System.out.println("DIR:" + System.getProperty("user.dir"));
        new DaoGenerator().generateAll(schema, outDir); //自动生成代码
    }

    private static void addEntities(Schema schema) {
        Entity template = schema.addEntity("Template"); //表对应的Model
        template.setDbName("template");
        template.addIdProperty().autoincrement().primaryKey().index(); //自增长主键
        template.addStringProperty("name").unique().notNull().index(); //模板名称
        template.addStringProperty("url").notNull(); //模板在线Url
        template.addStringProperty("local_path"); //模板本地存储地址
        template.addIntProperty("version_code"); //模板版本号
        template.addStringProperty("reserved"); //保留字段，以便后期扩展
    }

}
