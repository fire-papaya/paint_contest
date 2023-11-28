package uz.warcom.contest.persistence.exception

open class LogicException(override val message: String): RuntimeException(message)

open class NotFoundException(override val message: String): LogicException("Object not found")

class ContestNotFoundException(): NotFoundException("Contest was not found")

class EntryNotFoundException(): NotFoundException("Entry was not found")

class UserNotFoundException() : NotFoundException("Requested user was not found")

class UserWithoutCommunityException(): LogicException("User is not assigned to any community")

class CommunityNotFoundException(): NotFoundException("Community was not found")

class UserNotAdminException(): LogicException("User is not an admin")

class NotCommunityAdminException(): LogicException("User is not an admin of a community")

class DraftContestNotCreated(): NotFoundException("Draft contests were not found")

class NoPrimedImageException(): LogicException("Primed image were not received")

class NoPaintedImageException(): LogicException("Painted images were not received")