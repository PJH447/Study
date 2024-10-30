package com.demo.lucky_platform.web.mail;

public interface MailService {

    void mailSend(String toMail, String title, String content);
}
