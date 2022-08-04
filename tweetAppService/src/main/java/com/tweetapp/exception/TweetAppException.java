package com.tweetapp.exception;

import org.springframework.http.HttpStatus;

public class TweetAppException extends RuntimeException {

	private static final long serialVersionUID = 1558149957272449535L;
	private int statusCode;
	private HttpStatus status;
	private String data;

	public TweetAppException(int statusCode, HttpStatus status, String data) {
		this.statusCode = statusCode;
		this.status = status;
		this.data = data;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getData() {
		return data;
	}

}
