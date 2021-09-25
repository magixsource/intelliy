package com.github.magixsource.intelliy.setting

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil


@State(
    name = "com.github.magixsource.intelliy.setting.YxSettings",
    storages = [Storage("yunxi-toolkit-plugin.xml")]
)
class YxSettings : PersistentStateComponent<YxSettings> {

    var privateToken: String = "default token"
    var baseApi: String = "https://idp-api.dtyunxi.cn"


    override fun getState(): YxSettings {
        return this;
    }

    override fun loadState(state: YxSettings) {
        XmlSerializerUtil.copyBean(state, this);
    }

    companion object {
        @JvmStatic
        fun getInstance(): YxSettings? {
            return ApplicationManager.getApplication().getService(YxSettings::class.java)
        }
    }

}