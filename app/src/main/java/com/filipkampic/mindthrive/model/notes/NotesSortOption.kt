package com.filipkampic.mindthrive.model.notes

enum class NotesSortOption(val label: String) {
    BY_DATE_DESC("Newest first"),
    BY_DATE_ASC("Oldest first"),
    BY_TITLE_ASC("Title A-Z"),
    BY_TITLE_DESC("Title Z-A")
}