package org.grakovne.template.telegram.common

fun Boolean.ifTrue(action: () -> Any) {
    if (this) {
        action.invoke()
    }
}