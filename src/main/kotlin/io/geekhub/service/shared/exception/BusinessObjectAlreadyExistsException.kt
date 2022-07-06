package io.geekhub.service.shared.exception

class BusinessObjectAlreadyExistsException: RuntimeException {

    var id: String? = null

    constructor(msg: String): super(msg)
    constructor(msg: String, id: String): this(msg) {
        this.id = id
    }
}