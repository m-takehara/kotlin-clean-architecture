package me.takehara.port.user

class UserNotFoundException(message: String): RuntimeException(message)

class UserAuthRegistrationFailedException(message: String): RuntimeException(message)
class UserProfileRegistrationFailedException(message: String): RuntimeException(message)
