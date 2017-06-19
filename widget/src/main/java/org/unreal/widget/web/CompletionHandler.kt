package org.unreal.widget.web

/**
 * Created by du on 16/12/31.
 */

interface CompletionHandler {
    fun complete(retValue: String)
    fun complete()
    fun setProgressData(value: String)
}
