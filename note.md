# 想到的各种点

## 安全

### 防止注入

- [^\%\-\<\>\(\)\;\*\#\"\']*  正则防止注入

### 借鉴Spring Security 中的安全措施

#### 响应头

```http
HTTP/1.1 200 OK

Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0                                  // 禁用缓存
X-Content-Type-Options: nosniff             // 禁止浏览器MIME 嗅探
Strict-Transport-Security: max-age=31536000 ; includeSubDomains ; preload // 使用https
X-Frame-Options: DENY                       // 禁止 iframe 标签
X-XSS-Protection: 1; mode=block             // 检测到XSS攻击 禁止加载页面
Content-Security-Policy: default-src 'self' // CPS 策略
Referrer-Policy: same-origin                // referrer 策略
Feature-Policy: geolocation 'self'
```

HeaderWriter

trawl