package com.github.magixsource.intelliy.actions

import com.github.magixsource.intelliy.setting.YxSettingsConfigurable
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil


class SettingAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        ShowSettingsUtil.getInstance().showSettingsDialog(e.project, YxSettingsConfigurable::class.java)
    }
}

