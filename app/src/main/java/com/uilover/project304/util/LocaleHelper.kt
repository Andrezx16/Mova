package com.uilover.project304.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    /**
     * Cambia el idioma de la app y recrea la Activity para aplicar los cambios.
     * @param activity La Activity actual
     * @param languageCode Código de idioma ISO 639-1 (ej: "en", "es")
     */
    fun setLocale(activity: Activity, languageCode: String) {
        saveLanguagePreference(activity, languageCode)
        applyLocale(activity, languageCode)
        activity.recreate()
    }

    /**
     * Aplica el locale guardado al contexto. Llamar desde Application.attachBaseContext()
     * o desde Activity.attachBaseContext().
     */
    fun applyLocaleFromPreferences(context: Context): Context {
        val lang = getSavedLanguage(context)
        return if (lang.isNotEmpty()) {
            applyLocale(context, lang)
        } else {
            context
        }
    }

    private fun applyLocale(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun saveLanguagePreference(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language", languageCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return prefs.getString("language", "") ?: ""
    }

    fun getCurrentLanguageCode(context: Context): String {
        val saved = getSavedLanguage(context)
        return if (saved.isNotEmpty()) saved else Locale.getDefault().language
    }
}
