# JWT认证测试脚本

Write-Host "=== JWT认证测试 ===" -ForegroundColor Green

# 1. 登录获取Token
Write-Host "1. 正在登录..." -ForegroundColor Yellow
$loginBody = @{
    username = "admin"
    password = "123456"
    rememberMe = $false
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/auth/login" -Method POST -ContentType "application/json" -Body $loginBody
    $token = $loginResponse.data.accessToken
    Write-Host "✅ 登录成功！Token长度: $($token.Length)" -ForegroundColor Green
    Write-Host "Token前20个字符: $($token.Substring(0, 20))..." -ForegroundColor Cyan
} catch {
    Write-Host "❌ 登录失败: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# 2. 使用Token访问受保护的接口
Write-Host "`n2. 正在测试受保护接口..." -ForegroundColor Yellow
$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

try {
    $apiResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/farm-plots?current=1&size=10" -Method GET -Headers $headers
    Write-Host "✅ 接口访问成功！" -ForegroundColor Green
    Write-Host "响应代码: $($apiResponse.code)" -ForegroundColor Cyan
    Write-Host "响应消息: $($apiResponse.message)" -ForegroundColor Cyan
    if ($apiResponse.data) {
        Write-Host "数据记录数: $($apiResponse.data.records.Count)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "❌ 接口访问失败: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "状态码: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
