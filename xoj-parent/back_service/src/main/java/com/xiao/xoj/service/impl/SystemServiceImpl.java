package com.xiao.xoj.service.impl;

import com.xiao.xoj.pojo.vo.SystemInfoVO;
import com.xiao.xoj.service.SystemService;
import org.springframework.stereotype.Service;
import oshi.hardware.CentralProcessor;
import oshi.util.Util;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.text.DecimalFormat;

/**
 * @author 肖恩
 * @create 2023/8/28 20:12
 * @description: TODO
 */
@Service
public class SystemServiceImpl implements SystemService {

    @Override
    public SystemInfoVO getSystemInfo() {
        SystemInfoVO systemInfoVO = new SystemInfoVO();
        // 获取操作系统信息
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        systemInfoVO.setSystemName(osBean.getName());
        systemInfoVO.setSystemVersion(osBean.getVersion());
        systemInfoVO.setSystemProcessors(osBean.getAvailableProcessors());

        // 获取CPU使用率
        oshi.SystemInfo si = new oshi.SystemInfo();
        CentralProcessor processor = si.getHardware().getProcessor();
        long[] prevTicks  = processor.getSystemCpuLoadTicks();
        Util.sleep(500); // 必须要设置延迟
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softIrq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long ioWait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = Math.max(user + nice + cSys + idle + ioWait + irq + softIrq + steal, 0);
        DecimalFormat format = new DecimalFormat("#.00");
        if (totalCpu == 0) {
            systemInfoVO.setCpuUsage(0.0);
        } else {
            systemInfoVO.setCpuUsage(Double.parseDouble(format.format(100d * ioWait / totalCpu)));
        }

        // 获取内存信息
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long totalMemory = heapMemoryUsage.getMax();
        long useMemory = heapMemoryUsage.getUsed();
        long committedMemory = heapMemoryUsage.getCommitted();
        systemInfoVO.setTotalMemory(totalMemory / 1024 / 1024); // 总内存,MB
        systemInfoVO.setUseMemory(useMemory / 1024 / 1024);     // 已使用内存,MB
        systemInfoVO.setFreeMemory((totalMemory - useMemory) / 1024 / 1024);    // 空闲内存,MB
        systemInfoVO.setMemoryUsage(new Double(String.format("%.2f", committedMemory * 100.0 / totalMemory))); // 内存使用率

        return systemInfoVO;
    }

}
