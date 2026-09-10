package com.uilover.project304.util

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    /** App default when the user hasn't picked a language yet - Spanish, regardless of device locale. */
    private const val DEFAULT_LANGUAGE = "es"

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
        val lang = getSavedLanguage(context).ifEmpty { DEFAULT_LANGUAGE }
        return applyLocale(context, lang)
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
        return getSavedLanguage(context).ifEmpty { DEFAULT_LANGUAGE }
    }
}
