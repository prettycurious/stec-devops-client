package com.stec

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

// 自定义 CookieJar，用于存储和加载 Cookie
class MyCookieJar : CookieJar {
    private val cookieStore = mutableMapOf<String, MutableList<Cookie>>()

    // 保存 cookies
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val domainKey = url.host  // 使用域名作为 key 保存 cookies
        val existingCookies = cookieStore[domainKey] ?: mutableListOf()

        // 将新 cookies 添加到存储中
        existingCookies.addAll(cookies)
        cookieStore[domainKey] = existingCookies

        // 输出调试信息
        println("Saving cookies for $domainKey: $cookies")
    }

    // 获取 cookies
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val domainKey = url.host  // 使用域名作为 key 来加载 cookies

        // 获取与该域名匹配的所有 cookies
        val cookies = cookieStore[domainKey] ?: mutableListOf()

        // 输出调试信息
        println("Loading cookies for $domainKey: $cookies")
        return cookies
    }

    // 优化 cookie 存储和匹配方法，确保路径匹配
    private fun matchDomain(cookie: Cookie, url: HttpUrl): Boolean {
        // 判断 cookie 的域名是否与请求的 URL 的域名匹配
        val cookieDomain = cookie.domain  // 使用 cookie.domain 来访问域名
        val urlDomain = url.host

        return cookieDomain == urlDomain
    }
}
