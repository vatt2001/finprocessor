package models.db

class DatabaseException(message: String = null, reason: Throwable = null) extends RuntimeException(message, reason)

class ObjectNotFoundException(message: String = null, reason: Throwable = null) extends DatabaseException(message, reason)
