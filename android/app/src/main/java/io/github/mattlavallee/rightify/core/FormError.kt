package io.github.mattlavallee.rightify.core

class FormError {
    var missing: ArrayList<String> = ArrayList()

    fun hasErrors(): Boolean {
        return missing.count() > 0
    }

    fun generateErrorMessage(): String {
        return "Please provide values for: " + missing.joinToString(", ")
    }
}