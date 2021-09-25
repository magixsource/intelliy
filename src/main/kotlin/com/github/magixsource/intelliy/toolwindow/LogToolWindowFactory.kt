package com.github.magixsource.intelliy.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.ToolWindowType
import com.intellij.ui.content.ContentFactory

class LogToolWindowFactory : ToolWindowFactory {

//    private val context: HashMap<String, Any> = HashMap()
//
//    fun getCtx(): HashMap<String, Any> {
//        return this.context
//    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.setType(ToolWindowType.DOCKED, null)
//        val logView = LogToolWindowPanel()
//        val contentManager: ContentManager = toolWindow.contentManager
//        val content = contentManager.factory.createContent(
//            logView.component, "", false
//        )
//        contentManager.addContent(content);

//        val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).console
//        val content = contentManager.factory.createContent(consoleView.component, "", false)
//        contentManager.addContent(content)

//        consoleView.print("Hello from LogToolWindowFactory!", ConsoleViewContentType.NORMAL_OUTPUT);

        val logPanel = LogPanel(project)
        // context["log"] = logPanel
        // val echoWebSocketListener = EchoWebSocketListener()
        // echoWebSocketListener.logPanel = logPanel
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(logPanel.content, "", false)
        toolWindow.contentManager.addContent(content)
    }


}