CREATE DATABASE ifarm_dev DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci; 
USE ifarm_dev; 

-- ================================
-- 用户系统表
-- ================================

-- 用户表 
CREATE TABLE users ( 
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID', 
    openid VARCHAR(100) COMMENT '微信openid', 
    unionid VARCHAR(100) COMMENT '微信unionid', 
    username VARCHAR(100) NOT NULL UNIQUE  COMMENT '用户名', 
    password VARCHAR(255) NOT NULL COMMENT '密码', 
    nickname VARCHAR(50) COMMENT '用户昵称', 
    avatar VARCHAR(500) COMMENT '头像地址', 
    phone VARCHAR(20) COMMENT '手机号', 
    user_type TINYINT(1) NOT NULL DEFAULT 1 COMMENT '用户类型：1-普通用户，2-农场主，3-管理员', 
    gender TINYINT(1) COMMENT '性别：0-未知，1-男，2-女', 
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常', 
    last_login_time DATETIME COMMENT '最后登录时间', 
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除', 
 
    KEY idx_openid (openid), 
    KEY idx_phone (phone), 
    KEY idx_user_type (user_type), 
    KEY idx_status (status), 
    KEY idx_create_time (create_time) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表'; 

-- 收货地址表 
CREATE TABLE user_addresses ( 
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID', 
    user_id BIGINT NOT NULL COMMENT '用户ID', 
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人姓名', 
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话', 
    province VARCHAR(50) NOT NULL COMMENT '省份', 
    city VARCHAR(50) NOT NULL COMMENT '城市', 
    district VARCHAR(50) NOT NULL COMMENT '区县', 
    detail_address VARCHAR(200) NOT NULL COMMENT '详细地址', 
    is_default TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是', 
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间', 
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间', 
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除', 
 
    KEY idx_user_id (user_id), 
    KEY idx_is_default (is_default), 
    FOREIGN KEY (user_id) REFERENCES users(id) 
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收货地址表';

-- ================================
-- 系统配置表
-- ================================

-- 分类表
CREATE TABLE categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0表示顶级分类',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) COMMENT '分类编码',
    icon VARCHAR(200) COMMENT '分类图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_parent_id (parent_id),
    KEY idx_code (code),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 系统配置表
CREATE TABLE system_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    config_desc VARCHAR(200) COMMENT '配置描述',
    config_type VARCHAR(20) DEFAULT 'string' COMMENT '配置类型：string,number,boolean,json',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    KEY idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- ================================
-- 作物系统表
-- ================================

-- 作物品种表
CREATE TABLE crops (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '作物ID',
    category_id BIGINT COMMENT '作物分类ID',
    name VARCHAR(50) NOT NULL COMMENT '作物名称',
    variety VARCHAR(50) COMMENT '品种',
    description TEXT COMMENT '作物描述',
    growth_cycle INT COMMENT '生长周期（天）',
    planting_season VARCHAR(50) COMMENT '种植季节',
    harvest_season VARCHAR(50) COMMENT '收获季节',
    yield_per_unit DECIMAL(10,2) COMMENT '单位产量（kg）',
    nutrition_info TEXT COMMENT '营养信息（JSON格式）',
    planting_guide TEXT COMMENT '种植指南',
    cover_image VARCHAR(500) COMMENT '封面图片',
    images TEXT COMMENT '作物图片（JSON数组）',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_category_id (category_id),
    KEY idx_name (name),
    KEY idx_status (status),
    FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作物品种表';

-- ================================
-- 农场系统表
-- ================================

-- 农场表
CREATE TABLE farms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '农场ID',
    owner_id BIGINT NOT NULL COMMENT '农场主用户ID',
    name VARCHAR(100) NOT NULL COMMENT '农场名称',
    description TEXT COMMENT '农场描述',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    address VARCHAR(200) NOT NULL COMMENT '详细地址',
    latitude DECIMAL(10,6) COMMENT '纬度',
    longitude DECIMAL(10,6) COMMENT '经度',
    total_area DECIMAL(10,2) COMMENT '总面积（亩）',
    cover_image VARCHAR(500) COMMENT '封面图片',
    images TEXT COMMENT '农场图片（JSON数组）',
    license_number VARCHAR(100) COMMENT '营业执照号',
    certification TEXT COMMENT '认证信息（JSON格式）',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    business_hours VARCHAR(100) COMMENT '营业时间',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常，2-审核中',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_owner_id (owner_id),
    KEY idx_location (province, city, district),
    KEY idx_status (status),
    KEY idx_create_time (create_time),
    FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农场表';

-- 农场地块表
CREATE TABLE farm_plots (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '地块ID',
    farm_id BIGINT NOT NULL COMMENT '农场ID',
    name VARCHAR(100) NOT NULL COMMENT '地块名称',
    description TEXT COMMENT '地块描述',
    area DECIMAL(10,2) NOT NULL COMMENT '地块面积（平方米）',
    soil_type VARCHAR(50) COMMENT '土壤类型',
    irrigation_type VARCHAR(50) COMMENT '灌溉方式',
    location_info TEXT COMMENT '位置信息（JSON格式）',
    images TEXT COMMENT '地块图片（JSON数组）',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-可用，2-使用中',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_farm_id (farm_id),
    KEY idx_status (status),
    FOREIGN KEY (farm_id) REFERENCES farms(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='农场地块表';

-- 认养项目表
CREATE TABLE adoption_projects (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    plot_id BIGINT NOT NULL COMMENT '地块ID',
    crop_id BIGINT NOT NULL COMMENT '作物ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    description TEXT COMMENT '项目描述',
    total_units INT NOT NULL COMMENT '总单元数',
    available_units INT NOT NULL COMMENT '可认养单元数',
    unit_area DECIMAL(10,2) NOT NULL COMMENT '单元面积（平方米）',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单元认养价格',
    expected_yield DECIMAL(10,2) COMMENT '预期单元产量（kg）',
    planting_date DATE COMMENT '种植日期',
    expected_harvest_date DATE COMMENT '预期收获日期',
    project_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '项目状态：1-筹备中，2-认养中，3-种植中，4-收获中，5-已完成，6-已取消',
    cover_image VARCHAR(500) COMMENT '项目封面图',
    images TEXT COMMENT '项目图片（JSON数组）',
    planting_plan TEXT COMMENT '种植计划',
    care_instructions TEXT COMMENT '养护说明',
    harvest_instructions TEXT COMMENT '收获说明',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_plot_id (plot_id),
    KEY idx_crop_id (crop_id),
    KEY idx_project_status (project_status),
    KEY idx_planting_date (planting_date),
    KEY idx_create_time (create_time),
    FOREIGN KEY (plot_id) REFERENCES farm_plots(id),
    FOREIGN KEY (crop_id) REFERENCES crops(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认养项目表';

-- 项目单元表
CREATE TABLE project_units (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '单元ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    unit_number VARCHAR(20) NOT NULL COMMENT '单元编号',
    unit_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '单元状态：1-可认养，2-已认养，3-种植中，4-待收获，5-已收获',
    location_info TEXT COMMENT '单元位置信息（JSON格式）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    UNIQUE KEY uk_project_unit (project_id, unit_number),
    KEY idx_project_id (project_id),
    KEY idx_unit_status (unit_status),
    FOREIGN KEY (project_id) REFERENCES adoption_projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目单元表';

-- ================================
-- 认养订单系统表
-- ================================

-- 认养订单表
CREATE TABLE adoption_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    unit_count INT NOT NULL COMMENT '认养单元数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单元价格',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    discount_amount DECIMAL(10,2) DEFAULT 0 COMMENT '优惠金额',
    actual_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
    order_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '订单状态：1-待支付，2-已支付，3-已完成，4-已取消，5-已退款',
    payment_method VARCHAR(20) COMMENT '支付方式：wechat,alipay',
    payment_time DATETIME COMMENT '支付时间',
    payment_no VARCHAR(64) COMMENT '支付流水号',
    remark TEXT COMMENT '订单备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id),
    KEY idx_order_status (order_status),
    KEY idx_payment_time (payment_time),
    KEY idx_create_time (create_time),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES adoption_projects(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认养订单表';

-- 用户认养记录表
CREATE TABLE adoption_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '认养记录ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    unit_id BIGINT NOT NULL COMMENT '单元ID',
    adoption_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '认养状态：1-已认养，2-种植中，3-待收获，4-已收获，5-已完成',
    adoption_date DATETIME NOT NULL COMMENT '认养日期',
    planting_date DATETIME COMMENT '种植日期',
    harvest_date DATETIME COMMENT '收获日期',
    actual_yield DECIMAL(10,2) COMMENT '实际产量（kg）',
    quality_grade VARCHAR(20) COMMENT '品质等级',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_order_id (order_id),
    KEY idx_user_id (user_id),
    KEY idx_project_id (project_id),
    KEY idx_unit_id (unit_id),
    KEY idx_adoption_status (adoption_status),
    KEY idx_adoption_date (adoption_date),
    FOREIGN KEY (order_id) REFERENCES adoption_orders(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (project_id) REFERENCES adoption_projects(id),
    FOREIGN KEY (unit_id) REFERENCES project_units(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户认养记录表';

-- ================================
-- 种植管理系统表
-- ================================

-- 生长记录表
CREATE TABLE growth_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    growth_stage VARCHAR(50) COMMENT '生长阶段',
    growth_status VARCHAR(50) COMMENT '生长状态',
    height DECIMAL(8,2) COMMENT '植株高度（cm）',
    weather VARCHAR(50) COMMENT '天气情况',
    temperature_high INT COMMENT '最高温度（℃）',
    temperature_low INT COMMENT '最低温度（℃）',
    humidity INT COMMENT '湿度（%）',
    watering_amount DECIMAL(8,2) COMMENT '浇水量（L）',
    fertilizer_type VARCHAR(50) COMMENT '施肥类型',
    fertilizer_amount DECIMAL(8,2) COMMENT '施肥量（kg）',
    pest_control TEXT COMMENT '病虫害防治',
    care_activities TEXT COMMENT '养护活动',
    notes TEXT COMMENT '备注说明',
    images TEXT COMMENT '记录图片（JSON数组）',
    videos TEXT COMMENT '记录视频（JSON数组）',
    recorder_id BIGINT COMMENT '记录人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_project_id (project_id),
    KEY idx_record_date (record_date),
    KEY idx_growth_stage (growth_stage),
    KEY idx_recorder_id (recorder_id),
    FOREIGN KEY (project_id) REFERENCES adoption_projects(id),
    FOREIGN KEY (recorder_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生长记录表';

-- 收获记录表
CREATE TABLE harvest_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收获记录ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    unit_id BIGINT COMMENT '单元ID（可选，整体收获时为空）',
    harvest_date DATE NOT NULL COMMENT '收获日期',
    harvest_quantity DECIMAL(10,2) NOT NULL COMMENT '收获数量（kg）',
    quality_grade VARCHAR(20) COMMENT '品质等级',
    quality_score DECIMAL(3,1) COMMENT '品质评分',
    harvest_method VARCHAR(50) COMMENT '收获方式',
    storage_method VARCHAR(50) COMMENT '储存方式',
    packaging_type VARCHAR(50) COMMENT '包装类型',
    harvest_notes TEXT COMMENT '收获备注',
    images TEXT COMMENT '收获图片（JSON数组）',
    videos TEXT COMMENT '收获视频（JSON数组）',
    harvester_id BIGINT COMMENT '收获人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_project_id (project_id),
    KEY idx_unit_id (unit_id),
    KEY idx_harvest_date (harvest_date),
    KEY idx_quality_grade (quality_grade),
    KEY idx_harvester_id (harvester_id),
    FOREIGN KEY (project_id) REFERENCES adoption_projects(id),
    FOREIGN KEY (unit_id) REFERENCES project_units(id),
    FOREIGN KEY (harvester_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收获记录表';

-- ================================
-- 物流配送系统表
-- ================================

-- 配送订单表
CREATE TABLE delivery_orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配送订单ID',
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '配送单号',
    adoption_record_id BIGINT NOT NULL COMMENT '认养记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    address_id BIGINT NOT NULL COMMENT '收货地址ID',
    contact_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    contact_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    delivery_address TEXT NOT NULL COMMENT '收货地址',
    product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
    product_quantity DECIMAL(10,2) NOT NULL COMMENT '商品数量（kg）',
    package_count INT DEFAULT 1 COMMENT '包裹数量',
    delivery_type TINYINT(1) DEFAULT 1 COMMENT '配送类型：1-普通配送，2-冷链配送，3-特殊配送',
    delivery_fee DECIMAL(10,2) DEFAULT 0 COMMENT '配送费用',
    delivery_status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '配送状态：1-待发货，2-已发货，3-运输中，4-派送中，5-已签收，6-配送异常',
    logistics_company VARCHAR(50) COMMENT '物流公司',
    tracking_number VARCHAR(50) COMMENT '物流单号',
    shipped_time DATETIME COMMENT '发货时间',
    delivered_time DATETIME COMMENT '签收时间',
    estimated_delivery_time DATETIME COMMENT '预计送达时间',
    delivery_notes TEXT COMMENT '配送备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_order_no (order_no),
    KEY idx_adoption_record_id (adoption_record_id),
    KEY idx_user_id (user_id),
    KEY idx_delivery_status (delivery_status),
    KEY idx_tracking_number (tracking_number),
    KEY idx_shipped_time (shipped_time),
    KEY idx_create_time (create_time),
    FOREIGN KEY (adoption_record_id) REFERENCES adoption_records(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (address_id) REFERENCES user_addresses(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配送订单表';

-- 物流跟踪表
CREATE TABLE delivery_tracking (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '跟踪记录ID',
    delivery_order_id BIGINT NOT NULL COMMENT '配送订单ID',
    tracking_status VARCHAR(50) NOT NULL COMMENT '物流状态',
    tracking_info TEXT COMMENT '物流信息',
    location VARCHAR(100) COMMENT '当前位置',
    operator VARCHAR(50) COMMENT '操作人',
    tracking_time DATETIME NOT NULL COMMENT '跟踪时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    KEY idx_delivery_order_id (delivery_order_id),
    KEY idx_tracking_time (tracking_time),
    FOREIGN KEY (delivery_order_id) REFERENCES delivery_orders(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物流跟踪表';

-- ================================
-- 媒体资源表
-- ================================

-- 媒体文件表
CREATE TABLE media_files (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文件ID',
    file_name VARCHAR(200) NOT NULL COMMENT '文件名',
    original_name VARCHAR(200) COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_url VARCHAR(500) COMMENT '访问URL',
    file_size BIGINT COMMENT '文件大小（字节）',
    file_type VARCHAR(50) COMMENT '文件类型：image,video,document',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    file_extension VARCHAR(10) COMMENT '文件扩展名',
    width INT COMMENT '图片/视频宽度',
    height INT COMMENT '图片/视频高度',
    duration INT COMMENT '视频时长（秒）',
    related_type VARCHAR(50) COMMENT '关联类型：user,farm,project,growth,harvest',
    related_id BIGINT COMMENT '关联ID',
    uploader_id BIGINT COMMENT '上传者ID',
    status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',

    KEY idx_file_type (file_type),
    KEY idx_related (related_type, related_id),
    KEY idx_uploader_id (uploader_id),
    KEY idx_create_time (create_time),
    FOREIGN KEY (uploader_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='媒体文件表';

-- ================================
-- 初始化数据
-- ================================

-- 插入作物分类数据
INSERT INTO categories (name, code, icon, sort_order) VALUES
('蔬菜类', 'vegetables', '/icons/vegetables.png', 1),
('水果类', 'fruits', '/icons/fruits.png', 2),
('粮食作物', 'grains', '/icons/grains.png', 3),
('中草药', 'herbs', '/icons/herbs.png', 4),
('花卉类', 'flowers', '/icons/flowers.png', 5);

-- 插入常见作物数据
INSERT INTO crops (category_id, name, variety, description, growth_cycle, planting_season, harvest_season, yield_per_unit, cover_image) VALUES
(1, '番茄', '大红番茄', '新鲜有机番茄，口感酸甜，营养丰富', 90, '春季', '夏季', 5.00, '/images/crops/tomato.jpg'),
(1, '黄瓜', '水果黄瓜', '脆嫩多汁的有机黄瓜，适合生食', 60, '春季', '夏季', 8.00, '/images/crops/cucumber.jpg'),
(1, '白菜', '大白菜', '新鲜有机大白菜，叶片厚实', 75, '秋季', '冬季', 12.00, '/images/crops/cabbage.jpg'),
(1, '萝卜', '白萝卜', '有机白萝卜，清脆甘甜', 80, '秋季', '冬季', 10.00, '/images/crops/radish.jpg'),
(2, '草莓', '奶油草莓', '香甜可口的有机草莓', 120, '秋季', '春季', 2.50, '/images/crops/strawberry.jpg'),
(2, '西瓜', '无籽西瓜', '清甜多汁的有机西瓜', 100, '春季', '夏季', 25.00, '/images/crops/watermelon.jpg'),
(3, '玉米', '甜玉米', '香甜可口的有机玉米', 85, '春季', '夏季', 15.00, '/images/crops/corn.jpg'),
(3, '水稻', '优质稻', '有机优质大米', 150, '春季', '秋季', 20.00, '/images/crops/rice.jpg');

-- 插入系统配置数据
INSERT INTO system_configs (config_key, config_value, config_desc, config_type) VALUES
('site_name', 'iFarm智慧农场', '网站名称', 'string'),
('site_logo', '/images/logo.png', '网站Logo', 'string'),
('default_delivery_fee', '15.00', '默认配送费用', 'number'),
('free_delivery_amount', '100.00', '免配送费金额', 'number'),
('wechat_appid', '', '微信小程序AppID', 'string'),
('wechat_secret', '', '微信小程序Secret', 'string'),
('alipay_appid', '', '支付宝AppID', 'string'),
('growth_record_reminder', '1', '生长记录提醒开关', 'boolean'),
('harvest_notification', '1', '收获通知开关', 'boolean'),
('delivery_tracking', '1', '物流跟踪开关', 'boolean');

-- ================================
-- 创建视图
-- ================================

-- 用户认养项目视图
CREATE VIEW v_user_adoptions AS
SELECT
    ar.id as adoption_id,
    ar.user_id,
    u.nickname as user_nickname,
    ar.project_id,
    ap.name as project_name,
    ap.cover_image as project_image,
    ar.unit_id,
    pu.unit_number,
    ar.adoption_status,
    ar.adoption_date,
    ar.planting_date,
    ar.harvest_date,
    ar.actual_yield,
    c.name as crop_name,
    f.name as farm_name,
    f.province,
    f.city,
    f.district
FROM adoption_records ar
LEFT JOIN users u ON ar.user_id = u.id
LEFT JOIN adoption_projects ap ON ar.project_id = ap.id
LEFT JOIN project_units pu ON ar.unit_id = pu.id
LEFT JOIN crops c ON ap.crop_id = c.id
LEFT JOIN farm_plots fp ON ap.plot_id = fp.id
LEFT JOIN farms f ON fp.farm_id = f.id
WHERE ar.deleted = 0;

-- 项目统计视图
CREATE VIEW v_project_stats AS
SELECT
    ap.id as project_id,
    ap.name as project_name,
    ap.total_units,
    ap.available_units,
    (ap.total_units - ap.available_units) as adopted_units,
    ap.unit_price,
    COUNT(ar.id) as adoption_count,
    SUM(CASE WHEN ar.adoption_status = 4 THEN ar.actual_yield ELSE 0 END) as total_harvest,
    AVG(CASE WHEN ar.adoption_status = 4 THEN ar.actual_yield ELSE NULL END) as avg_yield_per_unit
FROM adoption_projects ap
LEFT JOIN adoption_records ar ON ap.id = ar.project_id AND ar.deleted = 0
WHERE ap.deleted = 0
GROUP BY ap.id;

-- ================================
-- 创建存储过程
-- ================================

DELIMITER //

-- 创建认养订单的存储过程
CREATE PROCEDURE CreateAdoptionOrder(
    IN p_user_id BIGINT,
    IN p_project_id BIGINT,
    IN p_unit_count INT,
    OUT p_order_id BIGINT,
    OUT p_result_code INT,
    OUT p_result_msg VARCHAR(200)
)
BEGIN
    DECLARE v_available_units INT DEFAULT 0;
    DECLARE v_unit_price DECIMAL(10,2) DEFAULT 0;
    DECLARE v_total_amount DECIMAL(10,2) DEFAULT 0;
    DECLARE v_order_no VARCHAR(32);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_result_code = -1;
        SET p_result_msg = '创建订单失败';
    END;

    START TRANSACTION;

    -- 检查项目可用单元数
    SELECT available_units, unit_price INTO v_available_units, v_unit_price
    FROM adoption_projects
    WHERE id = p_project_id AND project_status = 2 AND deleted = 0;

    IF v_available_units < p_unit_count THEN
        SET p_result_code = -2;
        SET p_result_msg = '可认养单元数不足';
        ROLLBACK;
    ELSE
        -- 生成订单号
        SET v_order_no = CONCAT('AD', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(FLOOR(RAND() * 999999), 6, '0'));
        SET v_total_amount = v_unit_price * p_unit_count;

        -- 创建订单
        INSERT INTO adoption_orders (order_no, user_id, project_id, unit_count, unit_price, total_amount, actual_amount)
        VALUES (v_order_no, p_user_id, p_project_id, p_unit_count, v_unit_price, v_total_amount, v_total_amount);

        SET p_order_id = LAST_INSERT_ID();
        SET p_result_code = 0;
        SET p_result_msg = '订单创建成功';

        COMMIT;
    END IF;
END //

DELIMITER ;

-- ================================
-- 创建触发器
-- ================================

-- 订单支付成功后自动分配单元的触发器
DELIMITER //

CREATE TRIGGER tr_adoption_order_paid
    AFTER UPDATE ON adoption_orders
    FOR EACH ROW
BEGIN
    DECLARE v_unit_id BIGINT;
    DECLARE v_counter INT DEFAULT 0;
    DECLARE done INT DEFAULT FALSE;
    DECLARE unit_cursor CURSOR FOR
        SELECT pu.id FROM project_units pu
        WHERE pu.project_id = NEW.project_id AND pu.unit_status = 1
        ORDER BY pu.id LIMIT NEW.unit_count;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    -- 当订单状态从待支付变为已支付时
    IF OLD.order_status = 1 AND NEW.order_status = 2 THEN
        OPEN unit_cursor;
        read_loop: LOOP
            FETCH unit_cursor INTO v_unit_id;
            IF done THEN
                LEAVE read_loop;
            END IF;

            -- 更新单元状态为已认养
            UPDATE project_units SET unit_status = 2 WHERE id = v_unit_id;

            -- 创建认养记录
            INSERT INTO adoption_records (order_id, user_id, project_id, unit_id, adoption_status, adoption_date)
            VALUES (NEW.id, NEW.user_id, NEW.project_id, v_unit_id, 1, NEW.payment_time);

            SET v_counter = v_counter + 1;
        END LOOP;
        CLOSE unit_cursor;

        -- 更新项目可用单元数
        UPDATE adoption_projects
        SET available_units = available_units - NEW.unit_count
        WHERE id = NEW.project_id;
    END IF;
END //

DELIMITER ;