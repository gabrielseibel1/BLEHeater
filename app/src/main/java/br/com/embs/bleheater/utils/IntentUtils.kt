package br.com.embs.bleheater.utils

import android.app.Activity
import android.content.Intent

inline fun <reified T : Activity> Activity.launchActivity(block: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    intent.run(block)
    this.startActivity(intent)
}