package com.xiao.xoj.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiao.xoj.common.result.ResultData;
import com.xiao.xoj.pojo.dto.ChatAskDTO;
import com.xiao.xoj.pojo.entity.wxqf.Chat;
import com.xiao.xoj.pojo.entity.wxqf.ChatInfo;
import com.xiao.xoj.service.ChatInfoService;
import com.xiao.xoj.service.ChatService;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @author 肖恩
 * @create 2023/8/5 19:21
 * @description: 处理用户相关请求
 */
@RestController
@RequestMapping("/xoj/front-chat")
public class FrontChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatInfoService chatInfoService;

    /**
     * @des: 获取对话列表
     *
     * @return
     */
    @GetMapping("get-chat-list")
    @RequiresAuthentication
    public ResultData<List<Chat>> getChatList() {
        List<Chat> chatList = chatService.getChatList();
        return ResultData.success(chatList);
    }

    /**
     * @des： 添加对话
     *
     * @param title
     * @return
     */
    @PostMapping("add-chat")
    @RequiresAuthentication
    public ResultData<Void> addChat(@RequestParam(value = "title") String title) {
        boolean isOk = chatService.addChat(title);
        if (!isOk) {
            return ResultData.fail("添加失败");
        }
        return ResultData.success("添加成功");
    }

    @DeleteMapping("delete-chat")
    @RequiresAuthentication
    public ResultData<Void> deleteChat(@RequestParam(value = "chatId") Integer chatId) {
        boolean isOk = chatService.deleteChat(chatId);
        if (!isOk) {
            return ResultData.fail("删除失败！");
        }
        return ResultData.success("删除成功");
    }

    /**
     * @des: 获取对话记录
     *
     * @param chatId
     * @return
     */
    @GetMapping("get-chat-record")
    @RequiresAuthentication
    public ResultData<List<ChatInfo>> getChatRecord(@RequestParam(value = "chatId") Integer chatId) {
        List<ChatInfo> chatInfos = chatService.getChatRecord(chatId);
        return ResultData.success(chatInfos);
    }


    /**
     * @des: 发起提问
     *
     * @param chatAskDTO
     * @return
     */
    /*@PostMapping("send-ask")
    @RequiresAuthentication
    public ResultData<ChatInfo> sendAsk(@RequestBody ChatAskDTO chatAskDTO) {
        ChatInfo chatInfo = chatService.sendAsk(chatAskDTO);

        // todo 以流式对象返回

        return ResultData.success(chatInfo);
    }*/

    @PostMapping("send-ask")
    //@RequiresAuthentication
    public ResultData<ChatInfo> sendAsk(@RequestBody ChatAskDTO chatAskDTO) {
        System.out.println("hello");
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = new okhttp3.RequestBody() {
            @Override
            public MediaType contentType() {
                return mediaType;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                //String jsonTemplate = "{\n    \"model\": \"gpt-3.5\",\n    \"messages\": [\n        {\n            \"role\": \"user\",\n            \"content\": \"%s\"\n        }\n    ]\n}";
                String jsonTemplate = "{\n" +
                        "    \"model\": \"mixtral-8x7b-32768\",\n" +
                        "    \"messages\": [\n" +
                        "        {\n" +
                        "            \"role\": \"user\",\n" +
                        "            \"content\": \"%s\"\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"max_tokens\": 4096,\n" +
                        "    \"stream\": false\n" +
                        "}";
                String jsonRequest = String.format(jsonTemplate, chatAskDTO.getContent());
                sink.writeUtf8(jsonRequest);
            }
        };
        Request request = new Request.Builder()
                .url("https://ollama.yamazing.cn/v1/chat/completions")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer M3UqqyTBhMy85WwoZ_IMtz0XQsfoYLX_VJi0uZN5WMID")
                .build();

        try {
            Response response = client.newCall(request).execute();
            // 解析响应数据
            String responseData = response.body().string();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseData);

            // ChatInfo chatInfo = new ChatInfo();
            ChatInfo chatInfo = chatService.sendAsk(chatAskDTO);
            chatInfo.setAnswer(jsonNode.get("choices").get(0).get("message").get("content").asText());
//            chatInfo.setQuestion(jsonNode.get("choices").get(0).get("message").get("role").asText());
            String content = chatAskDTO.getContent();
            chatInfo.setQuestion(chatAskDTO.getContent());
            System.out.println("content:"+content);
            String answer = "";
            int index = content.indexOf("项目介绍");
            int index1 = content.indexOf("项目成员");
            if (index != -1) {
                answer = "《基于微服务架构的深入学习在线评测与自然语言处理问答平台》是一款专为高校大学生设计的在线算法学习与交流系统，旨在为广大计" +
                        "算机专业新生提供一个学习算法、提升编程能力的平台，并促进学生之间的相互学习和帮助。在我国，计算机科学与技术专业虽" +
                        "然发展迅速，但许多学生在初入大学时对算法学习缺乏了解和兴趣。为了解决这一问题，我们团队开发了这个面向学校的算法学习与交" +
                        "流平台。";
                chatInfo.setAnswer(answer);
            }
            if(index1!=-1){
                answer = "《基于微服务架构的深入学习在线评测与自然语言处理问答平台》是由江西农业大学软件学院的21级、22级本科学生共同完成，" +
                        "该项目成员为：喻凯、张俊万、吴思远、沈舒婷、胡子欣。该项目由软件学院邓泓老师指导。";
                chatInfo.setAnswer(answer);
            }
            //chatInfo.setAnswer(content);
            else {
                answer = chatInfo.getAnswer();
            }
            System.out.println("answer:"+answer);

            LambdaQueryWrapper<ChatInfo> qw = new LambdaQueryWrapper();
            qw.eq(ChatInfo::getId, chatInfo.getId());
            chatInfoService.update(chatInfo, qw);

            return ResultData.success(chatInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return ResultData.fail("请求失败");
        }
    }
    // todo 导出对话
}
