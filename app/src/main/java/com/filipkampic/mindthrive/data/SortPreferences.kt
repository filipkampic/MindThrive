package com.filipkampic.mindthrive.data

import androidx.datastore.preferences.core.stringPreferencesKey

object SortPreferences {
    val SORT_OPTION = stringPreferencesKey("sort_option")
    val SORT_DIRECTION = stringPreferencesKey("sort_direction")
}
