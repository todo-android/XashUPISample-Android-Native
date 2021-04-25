package app.xash.upisample.modal

sealed class ResponseState {
    data class Success(val reqCode: String, val msg: String) : ResponseState()
    data class Error(val msg: String) : ResponseState()
}