package com.ifarm.generator;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * MyBatis Plus 代码生成器
 * 
 * @author ifarm
 * @since 2025-01-19
 */
public class CodeGenerator {

    /**
     * 数据库连接配置
     */
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ifarm_dev?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";

    /**
     * 项目路径配置
     */
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String JAVA_PATH = PROJECT_PATH + "/src/main/java";
    private static final String MAPPER_XML_PATH = PROJECT_PATH + "/src/main/resources/mapper";

    /**
     * 包名配置
     */
    private static final String PARENT_PACKAGE = "com.ifarm";
    private static final String AUTHOR = "ifarm";

    /**
     * 生成系统配置相关表
     */
    @Test
    public void generateSystemTables() {
        String[] tableNames = {"categories", "system_configs"};
        generateCode(tableNames, "system");
    }

    /**
     * 生成作物相关表
     */
    @Test
    public void generateCropTables() {
        String[] tableNames = {"crops"};
        generateCode(tableNames, "crop");
    }

    /**
     * 生成农场相关表
     */
    @Test
    public void generateFarmTables() {
        String[] tableNames = {"farms", "farm_plots", "adoption_projects", "project_units"};
        generateCode(tableNames, "farm");
    }

    /**
     * 生成认养订单相关表
     */
    @Test
    public void generateAdoptionTables() {
        String[] tableNames = {"adoption_orders", "adoption_records"};
        generateCode(tableNames, "adoption");
    }

    /**
     * 生成种植管理相关表
     */
    @Test
    public void generateGrowthTables() {
        String[] tableNames = {"growth_records", "harvest_records"};
        generateCode(tableNames, "growth");
    }

    /**
     * 生成物流配送相关表
     */
    @Test
    public void generateDeliveryTables() {
        String[] tableNames = {"delivery_orders", "delivery_tracking"};
        generateCode(tableNames, "delivery");
    }

    /**
     * 生成媒体文件相关表
     */
    @Test
    public void generateMediaTables() {
        String[] tableNames = {"media_files"};
        generateCode(tableNames, "media");
    }

    /**
     * 生成所有业务表（一键生成）
     */
    @Test
    public void generateAllBusinessTables() {
        String[] tableNames = {
            "categories", "system_configs", "crops", 
            "farms", "farm_plots", "adoption_projects", "project_units",
            "adoption_orders", "adoption_records",
            "growth_records", "harvest_records",
            "delivery_orders", "delivery_tracking",
            "media_files"
        };
        generateCode(tableNames, "business");
    }

    /**
     * 代码生成核心方法
     * 
     * @param tableNames 表名数组
     * @param moduleName 模块名称
     */
    private void generateCode(String[] tableNames, String moduleName) {
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author(AUTHOR)                    // 设置作者
                            .enableSwagger()                  // 开启 swagger 模式
                            .outputDir(JAVA_PATH)            // 指定输出目录
                            .dateType(DateType.TIME_PACK)    // 时间策略
                            .commentDate("yyyy-MM-dd");      // 注释日期
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent(PARENT_PACKAGE)           // 设置父包名
                            .moduleName(moduleName)          // 设置父包模块名
                            .entity("entity")                // Entity 包名
                            .service("service")              // Service 包名
                            .serviceImpl("service.impl")     // ServiceImpl 包名
                            .mapper("mapper")                // Mapper 包名
                            .controller("controller")        // Controller 包名
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml, MAPPER_XML_PATH)); // 设置mapperXml生成路径
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(tableNames)           // 设置需要生成的表名
                            .addTablePrefix("t_", "c_")      // 设置过滤表前缀
                            
                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok()                  // 开启 lombok 模型
                            .enableTableFieldAnnotation()   // 开启生成实体时生成字段注解
                            .enableRemoveIsPrefix()          // 开启 Boolean 类型字段移除 is 前缀
                            .enableFileOverride()            // 覆盖已生成文件
                            .idType(IdType.AUTO)            // 主键策略
                            .naming(NamingStrategy.underline_to_camel)        // 数据库表映射到实体的命名策略
                            .columnNaming(NamingStrategy.underline_to_camel)  // 数据库表字段映射到实体的命名策略
                            .addTableFills(new Column("create_time", FieldFill.INSERT))      // 添加表字段填充
                            .addTableFills(new Column("update_time", FieldFill.INSERT_UPDATE)) // 添加表字段填充
                            .logicDeleteColumnName("deleted") // 逻辑删除字段名
                            
                            // Controller 策略配置
                            .controllerBuilder()
                            .enableRestStyle()               // 开启生成@RestController 控制器
                            .enableHyphenStyle()             // 开启驼峰转连字符
                            .enableFileOverride()            // 覆盖已生成文件
                            
                            // Service 策略配置
                            .serviceBuilder()
                            .formatServiceFileName("I%sService")     // 格式化 service 接口文件名称
                            .formatServiceImplFileName("%sServiceImpl") // 格式化 service 实现类文件名称
                            .enableFileOverride()            // 覆盖已生成文件
                            
                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableMapperAnnotation()        // 开启 @Mapper 注解
                            .enableBaseResultMap()           // 启用 BaseResultMap 生成
                            .enableBaseColumnList()          // 启用 BaseColumnList
                            .enableFileOverride();           // 覆盖已生成文件
                })
                // 模板引擎配置，使用 Freemarker 引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
        
        System.out.println("=== 代码生成完成 ===");
        System.out.println("模块: " + moduleName);
        System.out.println("表名: " + String.join(", ", tableNames));
        System.out.println("输出目录: " + JAVA_PATH);
    }
}
