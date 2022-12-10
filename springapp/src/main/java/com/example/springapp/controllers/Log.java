package com.example.springapp.controllers;

import com.example.springapp.ANSI;

/**
 * Defines a standard for debug messages for this application.
 */
public class Log {
    private static String headerColor = ANSI.YELLOW;

    /**
     * Change the color of the [Header] in debug messages
     * @param color ANSI escape code string
     */
    public static void setHeaderColor(String color) {
        headerColor = color;
    }

    /**
     * Send a message of the form [Header] Content
     * @param headerContent content inside the square brackets, excluding the brackets
     * @param bodyColor color of body content
     * @param bodyContent string of body content
     */
    public static void sendMessage(String headerContent, String bodyColor, String bodyContent) {
        System.out.println(headerColor + "["+headerContent+"] " + bodyColor + bodyContent + ANSI.RESET);
    }

    /**
     * Send an error that occurred with the websocket
     * @param subHeader [SOCKET SUBHEADER] optional sub header to indicate which method we are in
     * @param bodyContent message to sent
     */
    public static void sendSocketError(String subHeader, String bodyContent) {
        sendMessage("SOCKET " + subHeader, ANSI.RED, "ERROR: " +  bodyContent);
    }

    /**
     * Send a success that occurred with the websocket
     * @param subHeader [SOCKET SUBHEADER] optional sub header to indicate which method we are in
     * @param bodyContent message to sent
     */
    public static void sendSocketSuccess(String subHeader, String bodyContent) {
        sendMessage("SOCKET " + subHeader, ANSI.GREEN, "SUCCESS: " +  bodyContent);
    }

    /**
     * Send a general notification that something occurred with the websocket
     * @param subHeader [SOCKET SUBHEADER] optional sub header to indicate which method we are in
     * @param bodyContent message to sent
     */
    public static void sendSocketGeneral(String subHeader, String bodyContent) {
        sendMessage("SOCKET " + subHeader, ANSI.CYAN, bodyContent);
    }
}
