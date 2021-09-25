package com.github.magixsource.intelliy.setting

import com.intellij.openapi.options.Configurable
import javax.swing.JComponent


class YxSettingsConfigurable : Configurable {

    private var mySettingsComponent: SettingsComponent? = null

    override fun createComponent(): JComponent? {
        mySettingsComponent = SettingsComponent()
        return mySettingsComponent!!.getPanel()
    }

    override fun isModified(): Boolean {
        val settings: YxSettings? = YxSettings.getInstance()
        var modified: Boolean = mySettingsComponent!!.getPrivateToken() != settings?.privateToken
        modified = modified or (mySettingsComponent!!.getBaseApi() !== settings?.baseApi)
        return modified
    }

    override fun apply() {
        val settings: YxSettings? = YxSettings.getInstance()
        settings?.baseApi = mySettingsComponent!!.getBaseApi()
        settings?.privateToken = mySettingsComponent!!.getPrivateToken()
    }

    override fun reset() {
        val settings: YxSettings? = YxSettings.getInstance()
        mySettingsComponent!!.setBaseApi(settings!!.baseApi)
        mySettingsComponent!!.setPrivateToken(settings.privateToken)
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }

    override fun getDisplayName(): String {
        return "Toolkit Settings"
    }
}