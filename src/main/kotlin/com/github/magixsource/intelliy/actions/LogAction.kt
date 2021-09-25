package com.github.magixsource.intelliy.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class LogAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val group = ActionManager.getInstance().getAction("YunxiToolkitMenu")
        checkNotNull(group)
        check(group is ActionGroup)
        ActionManager.getInstance().createActionToolbar("", group, true)
    }
}