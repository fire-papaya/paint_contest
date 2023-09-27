package uz.warcom.contest.persistence.exception

open class LogicException(override val message: String): RuntimeException(message)

open class NotFoundException(override val message: String): LogicException("Object not found")

class ContestNotFoundException(): NotFoundException("Contest was not found")

class EntryNotFoundException(): NotFoundException("Entry was not found")

class UserNotFoundException() : NotFoundException("Requested user was not found")

class UserWithoutCommunityException(): LogicException("User is not assigned to any community")