package com.xiao.xoj.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author 肖恩
 * @create 2023/5/9 20:28
 * @description: TODO
 */
public class JudgeUtils {


    // todo 代码看不懂，toProcess的格式不是很清楚，等运行的时候看看是什么
    public static List<String> translateCommandline(String toProcess) {
        if (toProcess != null && !toProcess.isEmpty()) {
            int state = 0;
            StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
            List<String> result = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            boolean lastTokenHasBeenQuoted = false;

            while (true) {
                while (tok.hasMoreTokens()) {
                    String nextTok = tok.nextToken();
                    switch (state) {
                        case 1:
                            if ("'".equals(nextTok)) {
                                lastTokenHasBeenQuoted = true;
                                state = 0;
                            } else {
                                current.append(nextTok);
                            }
                            continue;
                        case 2:
                            if ("\"".equals(nextTok)) {
                                lastTokenHasBeenQuoted = true;
                                state = 0;
                            } else {
                                current.append(nextTok);
                            }
                            continue;
                    }

                    if ("'".equals(nextTok)) {
                        state = 1;
                    } else if ("\"".equals(nextTok)) {
                        state = 2;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() > 0) {
                            result.add(current.toString());
                            current.setLength(0);
                        }
                    } else {
                        current.append(nextTok);
                    }

                    lastTokenHasBeenQuoted = false;
                }

                if (lastTokenHasBeenQuoted || current.length() > 0) {
                    result.add(current.toString());
                }

                if (state != 1 && state != 2) {
                    return result;
                }

                throw new RuntimeException("unbalanced quotes in " + toProcess);
            }
        } else {
            return new ArrayList<>();
        }
    }

}
