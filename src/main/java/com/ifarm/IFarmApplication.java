package com.ifarm;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
public class IFarmApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(IFarmApplication.class, args);
        Environment env = application.getEnvironment();

        String ip = InetAddress.getLocalHost().getHostAddress();
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path", "");

        // 检查微信配置状态
        String wechatConfigStatus = checkWechatConfig(env);

        log.info("\n----------------------------------------------------------\n\t" +
                        "🚀 iFarm 电子农场认养系统启动成功! 🚀\n\t" +
                        "应用名称: \t{}\n\t" +
                        "本地访问: \thttp://localhost:{}{}\n\t" +
                        "外部访问: \thttp://{}:{}{}\n\t" +
                        "环境配置: \t{}\n\t" +
                        "API文档地址:\n\t" +
                        "  • Swagger UI: \thttp://localhost:{}{}/swagger-ui/index.html\n\t" +
                        "  • Knife4j UI: \thttp://localhost:{}{}/doc.html\n\t" +
                        "  • OpenAPI JSON: \thttp://localhost:{}{}/v3/api-docs\n\t" +
                        "微信配置状态:\n{}\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                port, path,
                ip, port, path,
                env.getActiveProfiles().length == 0 ? "default" : String.join(",", env.getActiveProfiles()),
                port, path,
                port, path,
                port, path,
                wechatConfigStatus
        );
    }

    /**
     * 检查微信配置状态
     *
     * @param env 环境配置
     * @return 配置状态信息
     */
    private static String checkWechatConfig(Environment env) {
        StringBuilder status = new StringBuilder();

        // 检查微信小程序配置
        String appId = env.getProperty("wechat.miniapp.app-id");
        String appSecret = env.getProperty("wechat.miniapp.app-secret");

        status.append("\t  📱 微信小程序配置:\n");
        status.append("\t    • AppID: ").append(isConfigured(appId) ? "✅ 已配置" : "❌ 未配置").append("\n");
        status.append("\t    • AppSecret: ").append(isConfigured(appSecret) ? "✅ 已配置" : "❌ 未配置").append("\n");

        // 检查微信支付配置
        String mchId = env.getProperty("wechat.pay.mch-id");
        String certSerialNo = env.getProperty("wechat.pay.cert-serial-no");
        String apiV3Key = env.getProperty("wechat.pay.api-v3-key");
        String apiV2Key = env.getProperty("wechat.pay.api-v2-key");

        status.append("\t  💰 微信支付配置:\n");
        status.append("\t    • 商户号: ").append(isConfigured(mchId) ? "✅ 已配置" : "❌ 未配置").append("\n");
        status.append("\t    • 证书序列号: ").append(isConfigured(certSerialNo) ? "✅ 已配置" : "❌ 未配置").append("\n");
        status.append("\t    • APIv3密钥: ").append(isConfigured(apiV3Key) ? "✅ 已配置" : "❌ 未配置").append("\n");
        status.append("\t    • APIv2密钥: ").append(isConfigured(apiV2Key) ? "✅ 已配置" : "❌ 未配置").append("\n");

        // 整体状态判断
        boolean miniappConfigured = isConfigured(appId) && isConfigured(appSecret);
        boolean payConfigured = isConfigured(mchId) && isConfigured(certSerialNo) &&
                               isConfigured(apiV3Key) && isConfigured(apiV2Key);

        status.append("\t  🔧 整体状态: ");
        if (miniappConfigured && payConfigured) {
            status.append("✅ 微信配置完整，可正常使用");
        } else if (miniappConfigured) {
            status.append("⚠️  小程序配置完整，支付配置待完善");
        } else {
            status.append("❌ 微信配置不完整，请检查环境变量");
        }

        return status.toString();
    }

    /**
     * 检查配置项是否已设置
     *
     * @param value 配置值
     * @return 是否已配置
     */
    private static boolean isConfigured(String value) {
        return value != null &&
               !value.trim().isEmpty() &&
               !value.startsWith("your-") &&
               !value.equals("your-wechat-app-id") &&
               !value.equals("your-wechat-app-secret") &&
               !value.equals("your-mch-id") &&
               !value.equals("your-cert-serial-no") &&
               !value.equals("your-api-v3-key") &&
               !value.equals("your-api-v2-key");
    }
}
