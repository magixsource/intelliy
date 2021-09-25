package com.github.magixsource.intelliy.setting

import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.FormBuilder
import org.jetbrains.annotations.NotNull
import javax.swing.JComponent
import javax.swing.JPanel


class SettingsComponent {
    var myMainPanel: JPanel? = null
    private val baseApi = JBTextField()
    private val privateToken = JBTextField()

    init {
        myMainPanel = FormBuilder.createFormBuilder()
            .addLabeledComponent(JBLabel("Base API: "), baseApi, 1, false)
            .addLabeledComponent(JBLabel("Private-Token: "), privateToken, 0, false)
            .addComponentFillVertically(JPanel(), 0)
            .panel
    }

    fun getPanel(): JPanel? {
        return myMainPanel
    }

    fun getPreferredFocusedComponent(): JComponent {
        return baseApi
    }

    @NotNull
    fun getBaseApi(): String {
        return baseApi.text.toString()
    }

    fun setBaseApi(@NotNull newBaseApi: String) {
        baseApi.text = newBaseApi
    }

    @NotNull
    fun getPrivateToken(): String {
        return privateToken.text
    }

    fun setPrivateToken(@NotNull newToken: String) {
        privateToken.text = newToken
    }


}

