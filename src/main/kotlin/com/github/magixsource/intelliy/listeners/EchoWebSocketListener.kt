package com.github.magixsource.intelliy.listeners

import com.github.magixsource.intelliy.toolwindow.LogPanel
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

/**
 * Echo web socket listener
 */
class EchoWebSocketListener(private val logPanel: LogPanel) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        println("onOpen 连接成功 : $response")
        super.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        println("onMessage: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // 输出到console
        logPanel.render(bytes.utf8())
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing 连接关闭 : $code $reason")
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        println("onClosing 连接已关闭 : $code $reason")
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        println("========error $t $response ${t.stackTrace}")
        super.onFailure(webSocket, t, response)
    }


}