package org.example;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class FastAutoGeneratorTest {
    @Test
    public void test() {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create(new DataSourceConfig.Builder("jdbc:mysql://localhost:3306/scott?serverTimezone=GMT%2B8", "root", "root").
                        dbQuery(new MySqlQuery()).keyWordsHandler(new MySqlKeyWordsHandler()).databaseQueryClass(SQLQuery.class).
                        typeConvert(new MySqlTypeConvertExt()))
                .globalConfig(builder -> {
                    builder.author("final") // 设置作者
                            .disableOpenDir()
                            .outputDir("src/main/java/")
                            .commentDate("yyyy-MM-dd HH:mm:ss").
                            dateType(DateType.TIME_PACK);
                })
                .packageConfig(builder -> {
                    builder.parent("org.example.repository") // 设置父包名
                            .entity("po") // 设置实体类包名
                            .mapper("dao") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .xml("mapper").
                            pathInfo(Collections.singletonMap(OutputFile.xml, "src/main/resources/mapper")); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude("t_instruction_log", "t_instruction_log_ext").addTablePrefix("t_");// 设置需要生成的表名


                    builder.controllerBuilder().disable();

                    builder.entityBuilder().tableFieldAnnotationHandler(new DefaultTableFieldAnnotationHandlerExt())
                            .enableLombok().idType(IdType.AUTO).formatFileName("%sPO")
                            .addTableFills(new Column("create_time", FieldFill.INSERT))
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)).enableFileOverride();

                    builder.serviceBuilder().enableFileOverride();

                    builder.mapperBuilder().enableFileOverride().enableBaseResultMap();

                    //.enableRestStyle(); // 启用 REST 风格
                })
                .templateEngine(new VelocityTemplateEngine())
                .templateConfig(builder -> {
                    builder.disable(TemplateType.CONTROLLER);
                })// 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
