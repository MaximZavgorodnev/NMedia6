package ru.netology.nmedia.error

import android.provider.Settings.Global.getString
import ru.netology.nmedia.R

sealed class AppError(var code: String): RuntimeException()
class ApiError(val status: Int, code: String): AppError(code)
object NetworkError : AppError((R.string.error_network).toString())
object UnknownError: AppError((R.string.error_unknown).toString())