package com.xiao.xoj.service.email;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.UnicodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * @author 肖恩
 * @create 2023/6/10 21:06
 * @description: 邮件服务
 */
@Slf4j
@Component
public class EmailService {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private String ssl;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String auth;

    @Value("${spring.mail.properties.mail.smtp.starttls.required}")
    private String starttlsRequire;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String starttlsEnable;


    private JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setDefaultEncoding("UTF-8");
        sender.setUsername(username);
        sender.setPassword(password);

        Properties p = new Properties();
        p.setProperty("mail.smtp.ssl.enable", ssl);
        p.setProperty("mail.smtp.auth", auth);
        p.setProperty("mail.smtp.starttls.require", starttlsRequire);
        p.setProperty("mail.smtp.starttls.enable", starttlsEnable);
        sender.setJavaMailProperties(p);
        return sender;
    }


    @Async
    public void sendCode(String email, String number) {
        DateTime expireTime = DateUtil.offsetMinute(new Date(), 10);
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String emailContent = "注册码: " + number + "</br>过期时间为: " + expireTime.toString() + "</br>请及时注册！";

            // 设置邮件标题
            mimeMessageHelper.setSubject(UnicodeUtil.toString("XOJ的注册邮件"));
            mimeMessageHelper.setText(emailContent, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(username);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("用户注册的邮件任务发生异常------------>{}", e.getMessage());
        }
    }

    public void sendChangeEmailCode(String email, String code) {
        DateTime expireTime = DateUtil.offsetMinute(new Date(), 10);
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            String emailContent = "验证码码: " + code + "</br>过期时间为: " + expireTime.toString() + "</br>请及时修改！";

            // 设置邮件标题
            mimeMessageHelper.setSubject(UnicodeUtil.toString("XOJ的修改邮箱邮件"));
            mimeMessageHelper.setText(emailContent, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(username);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("用户注册的邮件任务发生异常------------>{}", e.getMessage());
        }
    }

    public boolean sendContestCorrectionInfo(String email, String content) {
        JavaMailSenderImpl mailSender = getMailSender();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        boolean isSendOk = true; // 是否发送成功
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            // 设置邮件标题
            mimeMessageHelper.setSubject(UnicodeUtil.toString("磐石工作室的考核通知"));
            mimeMessageHelper.setText(content, true);
            // 收件人
            mimeMessageHelper.setTo(email);
            // 发送人
            mimeMessageHelper.setFrom(username);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("用户考核通知的邮件任务发生异常------------>{}", e.getMessage());
            isSendOk = false;
        }
        return isSendOk;
    }
}
