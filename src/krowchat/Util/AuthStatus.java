package krowchat.Util;

public enum AuthStatus
{
    EMPTY_FIELDS,       // username or password is empty
    INCORRECT_DETAILS,  // username or password is incorrect
    AUTH_OK,            // username and password is good
    SERVER_ERROR,       // server side error
    UNKNOWN_ERROR,      // unknown error
    DISCONNECTED,       // server closed connection
}
