package com.upgrad.quora.service.type;

/**
 * Enum describing various various actions of the user for questions and answer
 * Based on the type of action specific exceptions will be thrown.
 */
public enum ActionType {
    VIEW, EDIT, DELETE, CREATE, ALL_QUESTION, ALL_FOR_USER;
}
