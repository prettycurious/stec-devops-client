package com.stec

import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

// 自定义拦截器，用于捕获重定向并手动处理 Set-Cookie
class RedirectInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 获取原始请求的响应
        val response = chain.proceed(chain.request())
        if (response.code == 302) {
            // 在重定向之前获取 Set-Cookie
            val cookies = response.headers("Set-Cookie")
            cookies.forEach {
                println("Redirect Cookie: $it")
            }
        }
        return response
    }
}

class DeployService(private val consoleView: ConsoleView) {
    private val client: OkHttpClient

    init {
        val cookieJar = MyCookieJar()  // 使用自定义的 MyCookieJar

        // 添加自定义拦截器来处理重定向响应
        client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)  // 连接超时设置为 60 秒
            .readTimeout(60, TimeUnit.SECONDS)     // 读取超时设置为 60 秒
            .writeTimeout(60, TimeUnit.SECONDS)    // 写入超时设置为 60 秒
            .cookieJar(cookieJar)  // 将 MyCookieJar 添加到 OkHttpClient
            .addInterceptor(RedirectInterceptor())  // 添加重定向拦截器
            .followRedirects(false)  // 停止 OkHttp 自动处理重定向
            .build()
    }

    // 登录方法，获取 Cookie
    fun login(host: String, username: String, password: String): String {
        consoleView.print("Logging in...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val url = "$host/login"

        // 使用 FormBody 构建表单数据
        val body = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        // 创建请求，设置 Accept 请求头为 text/html
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Accept", "text/html")  // 设置 Accept 请求头为 text/html
            .build()

        val response = client.newCall(request).execute()

        // 检查响应是否成功
        if (!response.isSuccessful && response.code != 302) {
            consoleView.print("Login failed: ${response.message}\n", ConsoleViewContentType.ERROR_OUTPUT)
            throw Exception("Login failed")
        }

        // 处理 302 重定向的情况
        if (response.code == 302) {
            consoleView.print("Redirected after login\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }

        // 获取 Cookie
        val cookies = response.headers("Set-Cookie")
        consoleView.print("Login successful!\n", ConsoleViewContentType.NORMAL_OUTPUT)

        // 判断是否成功获取到 cookie
        if (cookies.isEmpty()) {
            consoleView.print("No cookies received in the response!\n", ConsoleViewContentType.ERROR_OUTPUT)
            throw Exception("No cookies received in the response")
        }

        // 返回第一个 Cookie（如果有多个）
        return cookies.firstOrNull() ?: ""
    }

    // 停止应用方法
    fun stopApplication(host: String, application: String, environment: String, cookie: String) {
        consoleView.print("Stopping application $application...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val url = "$host/program/kill"
        val body = JSONObject()
            .put("folder", "/data/apps_jar/$application/$environment")
            .toString()

        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookie)  // 将 Cookie 添加到请求头
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            consoleView.print(
                "Failed to stop application $application: ${response.message}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
        } else {
            consoleView.print("Application $application stopped successfully\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    // 上传应用文件方法
    fun uploadApplication(projectRootPath:String, host: String, application: String, cookie: String) {
        consoleView.print("Uploading application $application...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val url = "$host/source/deploy"

        // 获取项目根目录路径
//        val projectRoot = System.getProperty("user.dir")
//        val projectRoot = "/Users/tianwenyuan/SUIT/Projects/stec-promis/stec-promis-parent"
        val filePath = "$projectRootPath/$application/target/$application.jar"  // 根据实际路径调整
        val file = File(filePath)

        println("File path: ${file.absolutePath}")  // 输出绝对路径

        if (!file.exists()) {
            consoleView.print("Error: File not found at $filePath\n", ConsoleViewContentType.ERROR_OUTPUT)
            throw FileNotFoundException("File not found: $filePath")
        } else {
            println("File found: $filePath")
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("application/java-archive".toMediaType()))
            .build()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Cookie", cookie)  // 将 Cookie 添加到请求头
            .build()

        val response = client.newCall(request).execute()

        println(response.body?.string())

        if (!response.isSuccessful) {
            consoleView.print(
                "Failed to upload application $application: ${response.message}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
        } else {
            consoleView.print("Application $application uploaded successfully\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    // 查询列表，获取
    fun searchApplication(host: String, application: String, cookie: String): String? {
        consoleView.print("Searching application $application...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val url = "$host/source/search"
        val body = JSONObject()
            .put("currPage", 1)
            .put("pageSize", 20)
            .put("dir", "target/sources/$application")
            .toString()

        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookie)  // 将 Cookie 添加到请求头
            .build()

        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val jsonResponse = JSONObject(response.body?.string())
            val resultList = jsonResponse.getJSONObject("result").getJSONArray("list")
            val sourceInfoFile = resultList.getJSONObject(0).getString("sourceInfoFile")

            consoleView.print("sourceInfoFile: $sourceInfoFile\n", ConsoleViewContentType.NORMAL_OUTPUT)

            if (sourceInfoFile != null) {
                consoleView.print("Application $application searched successfully\n", ConsoleViewContentType.NORMAL_OUTPUT)
                return sourceInfoFile;
            }
            consoleView.print(
                "Failed to search application $application: ${response.message}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
            return null
        } else {
            consoleView.print(
                "Failed to search application $application: ${response.message}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
            return null
        }
    }

    // 发布应用方法
    fun releaseApplication(host: String, application: String, environment: String, sourceInfoFile: String, cookie: String) {
        consoleView.print("Releasing application $application...\n", ConsoleViewContentType.NORMAL_OUTPUT)
        val url = "$host/source/releaseDirStart"
        val body = JSONObject()
            .put("path", "target/sources/$application")
            .put("dirname", environment)
            .put("open", true)
            .put("sourceInfoName", sourceInfoFile)
            .toString()

        val request = Request.Builder()
            .url(url)
            .post(body.toRequestBody("application/json".toMediaType()))
            .addHeader("Cookie", cookie)  // 将 Cookie 添加到请求头
            .build()

        val response = client.newCall(request).execute()

        println(response.body?.string())

        if (!response.isSuccessful) {
            consoleView.print(
                "Failed to release application $application: ${response.message}\n",
                ConsoleViewContentType.ERROR_OUTPUT
            )
        } else {
            consoleView.print("Application $application released successfully\n", ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }
}
