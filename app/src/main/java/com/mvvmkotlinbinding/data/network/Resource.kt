package com.mvvmkotlinbinding.data.network

class Resource<T> {
    val status: Status
    val message: String?
    val data: T?
    val code: Int
    val exception: Exception?
    val throwable: Throwable?

    constructor(
        status: Status, data: T, message: String, code: Int,
        e: Exception?
    ) {
        this.status = status
        this.data = data
        this.message = message
        this.code = code
        exception = e
        throwable = null
    }

    constructor(
        status: Status, data: T?, message: String?, code: Int,
        t: Throwable?
    ) {
        this.status = status
        this.data = data
        this.message = message
        this.code = code
        exception = null
        throwable = t
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        }
        if (o == null || javaClass != o.javaClass) {
            return false
        }
        val resource = o as Resource<*>
        if (status != resource.status) {
            return false
        }
        if (if (message != null) message != resource.message else resource.message != null) {
            return false
        }
        if (code != resource.code) {
            return false
        }
        return if (data != null) data == resource.data else resource.data == null
    }

    override fun hashCode(): Int {
        var result = status.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "Resource{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}'
    }

    companion object {
        private const val NO_CODE = -1
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null, NO_CODE, null)
        }

        fun <T> error(
            msg: String?, data: T?, code: Int,
            e: Exception?
        ): Resource<T> {
            return Resource(Status.ERROR, data, msg, code, e)
        }

        fun <T> error(
            msg: String?, data: T?, code: Int,
            t: Throwable?
        ): Resource<T> {
            return Resource(Status.ERROR, data, msg, code, t)
        }

        fun <T> loading(data: T?): Resource<T> {
            return Resource(Status.LOADING, data, null, NO_CODE, null)
        }
    }
}