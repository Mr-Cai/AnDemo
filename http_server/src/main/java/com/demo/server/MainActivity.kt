package com.demo.server

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket

// 程序启动后, 手机和电脑需要保持同一局域网, 不要全局翻墙(代理), 否则无法收到响应
class MainActivity : AppCompatActivity() {
    private val serverPort = 8888
    lateinit var serverSocket: ServerSocket
    val msgLog = StringBuilder()    // 接收手机服务器发送的追加消息
    @Suppress("SpellCheckingInspection")
    private val ipAddress: String
        get() {
            val ip = StringBuilder()
            // 获取电脑所有IP, 相当于 ifconfig 命令
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) { // 当还有更多IP时继续扫描
                val innerAddress = interfaces.nextElement().inetAddresses  // 网络IP枚举
                while (innerAddress.hasMoreElements()) {    // 继续扫描, 直到满足条件
                    val address = innerAddress.nextElement()
                    when {  // 当出现本机局域网IP时，追加进变量
                        address.isSiteLocalAddress -> ip.append(address.hostAddress)
                    }
                }
            }
            return "$ip"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 根据显示出的IP, 在电脑浏览器输入 当前ip:当前port
        infoTxT.text = ("手机IP : $ipAddress:$serverPort")
        HttpServerThread().start()
    }

    override fun onDestroy() {
        super.onDestroy()
        serverSocket.close()
    }

    inner class HttpServerThread : Thread() {
        override fun run() {
            serverSocket = ServerSocket(serverPort)
            // 不停地连接服务器, 直到连接成功, 并接收数据
            while (true) ResponseThread(serverSocket.accept(), "${inputET.text}").start()
        }
    }

    inner class ResponseThread(private var socket: Socket, private var text: String) : Thread() {
        override fun run() {
            val printWriter = PrintWriter(socket.getOutputStream(), true) // 打印输出流, 并自动刷新缓存
            // 接收手机服务器输入的文字(输入流)
            val bufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val request = bufferedReader.readLine() // 逐行读取
            val response = """<html><meta charset="UTF-8">$text</html>""".trimIndent()
            with(printWriter) {
                print("HTTP\n\n$response") // HTTP是输出当前协议版本, 并不是网址开头, 换行是为了避免混淆
                flush()
            }
            socket.close()  // 接收完毕关闭套接字
            msgLog.append("请求${request}来自${socket.inetAddress}\n")
            this@MainActivity.runOnUiThread { msgTxT.text = "$msgLog".replace("/", " ") }
        }
    }
}
